package cn.fancychuan.scala

import java.util.{Properties, Random}

import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011

object SourceApp {
    def main(args: Array[String]): Unit = {
        val env = StreamExecutionEnvironment.getExecutionEnvironment
        env.setParallelism(1)

        // 1. 从集合中读取数据
        val stream1: DataStream[SensorReading] = env.fromCollection(List(
            SensorReading("sensor_1", 1547718199, 35.8)
            , SensorReading("sensor_6", 1547718201, 15.4)
            , SensorReading("sensor_7", 1547718202, 6.7)
            , SensorReading("sensor_10", 1547718205, 38.1)
        ))

        stream1.print("流1 stream1").setParallelism(1)

        // 2. 从文件读取数据
        val stream2 = env.readTextFile("E:\\JavaWorkshop\\bigdata-learn\\flink\\src\\main\\resources\\sensor.txt")
        stream2.print("流2：")

        // 3. socket文本流
        val stream3 = env.socketTextStream("hadoop101", 7777)
        stream3.print("socket流：")

        // 4. 从kafka中读取
        val properties = new Properties()
        properties.setProperty("bootstrap.servers", "hadoop101:9092")
        properties.setProperty("group.id", "consumer-group")
        properties.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
        properties.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
        properties.setProperty("auto.offset.reset", "latest")

        // FlinkKafkaConsumer011 其中的011表示版本
        val stream4 = env.addSource(new FlinkKafkaConsumer011[String]("sensor", new SimpleStringSchema(), properties))
        stream4.print("kafka流")


        // 5. 自定义source
        val stream5 = env.addSource(new MySource())
        stream5.print("自定义Source ")


        env.execute("source test job") // 参数表示作业名
    }
}

case class SensorReading(id: String, timestamp: Long, temperature: Double)

class MySource() extends SourceFunction[SensorReading] {
    // 定义一个flag，表示数据源是否正常运行
    var running: Boolean = true

    //随机生成数据
    override def run(sourceContext: SourceFunction.SourceContext[SensorReading]): Unit = {
        // 定义一个随机数发生器
        val rand = new Random()
        // 定义初始温度
        var curTemps = 1.to(10).map(i => ("sensor_" + i, 60 + rand.nextGaussian() * 20))

        while(running) {
            // 更新温度值
            curTemps.map(
                t => (t._1, t._2 + rand.nextGaussian())
            )
            val curTime = System.currentTimeMillis()
            curTemps.foreach(
                // 包装成样例类，用sourceContext发出数据
                t => sourceContext.collect(SensorReading(t._1, curTime, t._2))
            )
        }
        Thread.sleep(1000)
    }

    override def cancel(): Unit = false
}