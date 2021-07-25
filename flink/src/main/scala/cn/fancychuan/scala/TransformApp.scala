package cn.fancychuan.scala

import org.apache.flink.streaming.api.scala._

class TransformApp {
    def main(args: Array[String]): Unit = {
        val env = StreamExecutionEnvironment.getExecutionEnvironment
        val streamFromFile: DataStream[String] = env.readTextFile("E:\\JavaWorkshop\\bigdata-learn\\flink\\src\\main\\resources\\sensor.txt")

        streamFromFile.map(data => {
            val items = data.split(",")
            return SensorReading(items(0), items(1).toLong, items(2).toDouble)
        }).keyBy("id")
            .keyBy(0)

    }

}
