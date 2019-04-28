package mlWithSpark;

import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.SparkSession;

public class Main {
    private static SparkConf conf = new SparkConf().setMaster("local").setAppName("mlWithSpark");
    private static SparkSession spark = SparkSession.builder().config(conf).getOrCreate();

    public static void main(String[] args) {
    }

    public static void javaApp() {
        SQLContext sqlContext = spark.sqlContext();
        SparkContext sc = spark.sparkContext();
    }

}
