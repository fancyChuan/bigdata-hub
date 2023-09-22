package cn.fancychuan.spark3.java;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;

public class Test {
    public static void main(String[] args) {
        SparkConf sparkConf = new SparkConf().setAppName("wordcount-java").setMaster("local");
        JavaSparkContext sc = new JavaSparkContext(sparkConf);
    }
}
