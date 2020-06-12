/**
  * Licensed to the Apache Software Foundation (ASF) under one
  * or more contributor license agreements.  See the NOTICE file
  * distributed with this work for additional information
  * regarding copyright ownership.  The ASF licenses this file
  * to you under the Apache License, Version 2.0 (the
  * "License"); you may not use this file except in compliance
  * with the License.  You may obtain a copy of the License at
  * <p>
  * http://www.apache.org/licenses/LICENSE-2.0
  * <p>
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */

package com.aliyun.odps.spark.examples.zeppelin

import java.io.{File, FilenameFilter, PrintWriter}
import java.net.{Inet4Address, InetAddress, NetworkInterface, ServerSocket}
import java.util.concurrent.{ScheduledExecutorService, ScheduledThreadPoolExecutor, TimeUnit}

import com.aliyun.odps.cupid.CupidSession
import com.aliyun.odps.cupid.requestcupid.CupidProxyTokenUtil
import com.google.common.util.concurrent.ThreadFactoryBuilder
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.log4j.Logger
import org.apache.spark.sql.SparkSession
import org.apache.zeppelin.interpreter.remote.RemoteInterpreterServer

import scala.collection.JavaConverters._
import scala.io.Source
import scala.sys.process.Process

object ZeppelinServerPublic {

  def newDaemonSingleThreadScheduledExecutor(threadName: String): ScheduledExecutorService = {
    val threadFactory = new ThreadFactoryBuilder().setDaemon(true).setNameFormat(threadName).build()
    val executor = new ScheduledThreadPoolExecutor(1, threadFactory)
    // By default, a cancelled task is not automatically removed from the work queue until its delay
    // elapses. We have to enable it manually.
    executor.setRemoveOnCancelPolicy(true)
    executor
  }

  private val LOG = Logger.getLogger(ZeppelinServer.getClass)
  // zeppelin package location
  val zeppelinHome = s"${new File(".").getCanonicalPath}/" +
    s"public.zeppelin-0.8.1-bin-netinst.tar.gz/" +
    s"zeppelin-0.8.1-bin-netinst/"
  val fixedReplOutputDir = "/home/admin/replClassOutputDir"
  val pool = newDaemonSingleThreadScheduledExecutor("link4replClassOutputDir")

  def getRandomPort(): Int = {
    val socket = new ServerSocket(0)
    val port = socket.getLocalPort
    socket.close()
    port
  }


  /**
    * COPY from spark source code.
    * @return
    */
  def findLocalInetAddress(): InetAddress = {
    val defaultIpOverride = System.getenv("SPARK_LOCAL_IP")
    if (defaultIpOverride != null) {
      InetAddress.getByName(defaultIpOverride)
    } else {
      val address = InetAddress.getLocalHost
      if (address.isLoopbackAddress) {
        // Address resolves to something like 127.0.1.1, which happens on Debian; try to find
        // a better address using the local network interfaces
        // getNetworkInterfaces returns ifs in reverse order compared to ifconfig output order
        // on unix-like system. On windows, it returns in index order.
        // It's more proper to pick ip address following system output order.
        val activeNetworkIFs = NetworkInterface.getNetworkInterfaces.asScala.toSeq
        val reOrderedNetworkIFs = activeNetworkIFs.reverse

        for (ni <- reOrderedNetworkIFs) {
          val addresses = ni.getInetAddresses.asScala
            .filterNot(addr => addr.isLinkLocalAddress || addr.isLoopbackAddress).toSeq
          if (addresses.nonEmpty) {
            val addr = addresses.find(_.isInstanceOf[Inet4Address]).getOrElse(addresses.head)
            // because of Inet6Address.toHostName may add interface at the end if it knows about it
            val strippedAddress = InetAddress.getByAddress(addr.getAddress)
            // We've found an address that looks reasonable!
            LOG.warn("Your hostname, " + InetAddress.getLocalHost.getHostName + " resolves to" +
              " a loopback address: " + address.getHostAddress + "; using " +
              strippedAddress.getHostAddress + " instead (on interface " + ni.getName + ")")
            LOG.warn("Set SPARK_LOCAL_IP if you need to bind to another address")
            return strippedAddress
          }
        }
        LOG.warn("Your hostname, " + InetAddress.getLocalHost.getHostName + " resolves to" +
          " a loopback address: " + address.getHostAddress + ", but we couldn't find any" +
          " external IP address!")
        LOG.warn("Set SPARK_LOCAL_IP if you need to bind to another address")
      }
      address
    }
  }

  def replaceZeppelinCoreSite(zeppelinPort: Int): Unit = {
    Process(s"cp -f ${zeppelinHome}/conf/zeppelin-site.xml.todo ${zeppelinHome}/conf/zeppelin-site.xml").!

    val file = new File(s"${zeppelinHome}/conf/zeppelin-site.xml")
    val replaceStr = "##zeppelin_port##"
    val replaceContent = Source.fromFile(file).mkString.replace(replaceStr, zeppelinPort.toString)
    val writer = new PrintWriter(file)
    writer.write(replaceContent)
    writer.close()
  }

  def replaceZeppelinInterpreterJson(interpreterPort: Int): Unit = {
    Process(s"cp -f ${zeppelinHome}/conf/interpreter.json.todo ${zeppelinHome}/conf/interpreter.json").!

    val file = new File(s"${zeppelinHome}/conf/interpreter.json")
    val replaceStr = "##interpreter_port##"
    val replaceContent = Source.fromFile(file).mkString.replace(replaceStr, interpreterPort.toString)
    val writer = new PrintWriter(file)
    writer.write(replaceContent)
    writer.close()
  }

  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .config("spark.repl.class.outputDir", fixedReplOutputDir)
      .appName("ZeppelinServer")
      .getOrCreate()

    pool.scheduleWithFixedDelay(
      new Runnable {
        override def run(): Unit = {
          val linkDir = new File(fixedReplOutputDir)
          if (linkDir.exists()) {
            return
          } else {
            val tmpDir = new File(System.getProperty("java.io.tmpdir"))
            val dirs = tmpDir.list()
            for (dir <- dirs) {
              if (dir.startsWith("spark")) {
                // old interp like this spark-fdf1027a-a006-476c-9abb-c32b9fd784d7
                // new interp like this spark8819436304609717444
                val tmpDir2 = new File(s"${tmpDir.getAbsolutePath}/${dir}/")
                val lineCount = tmpDir2.list(new FilenameFilter {
                  override def accept(dir: File, name: String): Boolean = {
                    if (name.startsWith("$line")) {
                      true
                    } else {
                      false
                    }
                  }
                }).length

                if (lineCount > 0) {
                  val cmd = s"ln -s ${tmpDir.getCanonicalPath}/${dir} ${fixedReplOutputDir}"
                  LOG.info(s"find repl dir and do link with cmd ${cmd}")
                  Process(cmd).!
                }
              }
            }
          }
        }
      },
      0,
      1,
      TimeUnit.SECONDS
    )

    val interpreterServerPort = 12345
    val zeppelinPort = 54321
    LOG.info(s"interpreterServerPort: ${interpreterServerPort}, zeppelinPort: ${zeppelinPort}")

    // replaceZeppelinCoreSite(zeppelinPort)
    // replaceZeppelinInterpreterJson(interpreterServerPort)

    val zeppelinAppName = spark.sparkContext.getConf.get("spark.zeppelin.appName", "zeppelin")

    // prepare zeppelin end point
    // val address = findLocalInetAddress()
    // val zeppelinEndpoint = s"http://${address.getHostAddress}:${zeppelinPort}"
    // LOG.info(s"zeppelinEndpoint: ${zeppelinEndpoint}")

    // write CUPID appName meta info
    // val metaPanguPath = System.getenv("FUXI_JOB_TEMP_ROOT") + s"../../AppAddresses/${zeppelinAppName}"
    // val fs = FileSystem.get(spark.sparkContext.hadoopConfiguration)
    // LOG.info(s"metaPanguPath: ${metaPanguPath}")
    // val out = fs.create(new Path(metaPanguPath))
    // out.write(zeppelinEndpoint.getBytes)
    // out.close()

    // generate external url for end user
    val commonProxy = spark.sparkContext.getConf
      .get("spark.hadoop.odps.cupid.common.proxy",
      "open.maxcompute.aliyun.com")
    val externalUrl = s"http://${System.getenv("META_LOOKUP_NAME")}-zeppelin.${commonProxy}"
    println(s"Please visit the following url for zeppelin interaction.\n${externalUrl}")

    Process(s"cp -r $zeppelinHome/conf /worker/zeppelin_conf").!
    Process(s"cp -r $zeppelinHome/notebook /worker/local/notebook").!
    Process(s"mv /worker/zeppelin_conf/interpreter.json.public /worker/zeppelin_conf/interpreter.json").!
    Process(s"mv /worker/zeppelin_conf/zeppelin-site.xml.public /worker/zeppelin_conf/zeppelin-site.xml").!

    // start zeppelin
    // have to reset CLASSPATH to get rid of cupid runtime classpath
    // update PATH for hostname sleep nice nohup echo system command
    val pathEnv = System.getenv("PATH")
    val startZeppelinCmd = s"${zeppelinHome}/bin/zeppelin-daemon.sh --config /worker/zeppelin_conf start"
    Process(startZeppelinCmd, None,
      "CLASSPATH" -> "",
      "PATH" -> s"${zeppelinHome}/extraPath:${pathEnv}",
      "ZEPPELIN_HOME" -> zeppelinHome,
      "ZEPPELIN_LOG_DIR" -> "/worker/zeppelin_logs/",
      "ZEPPELIN_PID_DIR" -> "/worker/zeppelin_pids/"
    ).!

    val interpreter = new RemoteInterpreterServer(null, interpreterServerPort, null)
    interpreter.start()
    interpreter.join()
    System.exit(0)
  }

}
