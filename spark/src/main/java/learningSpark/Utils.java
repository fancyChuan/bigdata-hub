package learningSpark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

/**
 * 常用的工具类
 */
public class Utils {

    public static JavaSparkContext getSparkContext() {
        SparkConf conf = new SparkConf().setMaster("local").setAppName("common-sample");
        JavaSparkContext sc = new JavaSparkContext(conf);
        return sc;
    }

    public static JavaRDD<String> getTestFileRDD() {
        return getSparkContext().textFile("E:\\JavaWorkshop\\bigdata-learn\\spark\\src\\main\\resources\\testfile.md");
    }
}
