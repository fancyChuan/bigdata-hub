package cn.fancychuan.scala

import org.apache.flink.streaming.api.scala._

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


        env.execute("source test job") // 参数表示作业名
    }
}

case class SensorReading(id: String, timestamp: Long, temperature: Double)

