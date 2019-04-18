package sparkSQL;

import org.apache.spark.sql.AnalysisException;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.util.HashSet;

import static org.apache.spark.sql.functions.col;

public class Main {

    public static void helloSparkSQL() throws AnalysisException {
        SparkSession spark = SparkSession.builder().master("local[1]").appName("hello-word").getOrCreate();
        Dataset<Row>df = spark.read().json("E:\\JavaWorkshop\\bigdata-learn\\spark\\src\\main\\resources\\jsonFile.txt");
        df.show();
        df.printSchema(); // df的字段等元信息

        df.select("name").show();
        df.select(col("name"), col("score").plus(1));
        df.groupBy("name").count().show();
        df.filter(col("score").gt(95)).show();
        // 创建一个临时的视图
        df.createOrReplaceTempView("student");
        Dataset<Row> sqlDF = spark.sql("select id, name from student");
        sqlDF.show();
        // Register the DataFrame as a global temporary view
        df.createGlobalTempView("student");
        // Global temporary view is tied to a system preserved database `global_temp`
        spark.sql("SELECT * FROM global_temp.student").show();
    }

    public static void main(String[] args) throws AnalysisException {
        helloSparkSQL();
    }
}
