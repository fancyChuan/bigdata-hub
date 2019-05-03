package mlWithSpark;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;

public class DataExplore {
    private SparkSession spark;

    public DataExplore(SparkSession spark) {
        this.spark = spark;
    }

    public void userDataExplore() {

        Dataset<Row> userData = spark.read()
                .format("csv")
                .option("sep", "|")
                .option("header", "false") // 数据里面首行不是表头
                .option("inferSchema", "true")
                .schema("id LONG, age LONG, gender STRING, occupation STRING, zipcode STRING")
                .load("E:\\JavaWorkshop\\bigdata-learn\\spark\\data\\ml-100k\\u.user");
        userData.printSchema();
        System.out.println(userData.first());
    }
}
