package sparkSQL;

import org.apache.spark.SparkConf;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class Main {

    public static void helloSparkSQL() {
        SparkSession spark = SparkSession.builder().master("local[1]").appName("hello-word").getOrCreate();
        Dataset<Row>df = spark.read().json("E:\\JavaWorkshop\\bigdata-learn\\spark\\src\\main\\resources\\jsonFile.txt");
        df.show();
    }

    public static void main(String[] args) {
        helloSparkSQL();
    }
}
