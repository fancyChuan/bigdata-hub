package mlWithSpark.explore;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;

import java.util.Arrays;

public class DataExplore {
    private SparkSession spark;

    public DataExplore(SparkSession spark) {
        this.spark = spark;
    }

    public void userDataExplore() {
        StructType schame = DataTypes.createStructType(Arrays.asList(
                DataTypes.createStructField("id", DataTypes.IntegerType, true),
                DataTypes.createStructField("age", DataTypes.IntegerType, true),
                DataTypes.createStructField("gender", DataTypes.StringType, true),
                DataTypes.createStructField("occupation", DataTypes.StringType, true),
                DataTypes.createStructField("zipCode", DataTypes.StringType, true)
        ));

        Dataset<Row> userData = spark.read()
                .format("csv")
                .option("sep", "|")
                .option("header", "false") // 数据里面首行不是表头
                .schema(schame) // 指定schema有两种方式，还可以 .schema("id LONG, age LONG, gender STRING, occupation STRING, zipcode STRING")
                .load("E:\\JavaWorkshop\\bigdata-learn\\spark\\data\\ml-100k\\u.user");
        userData.printSchema();
        userData.show(5);
        System.out.println(userData.first());
        
        // 注册为view
        userData.createOrReplaceTempView("user_data");

        // 查看男女比例
        System.out.println("【总数据量】" + userData.count());
        System.out.println("【男女比例】");
        userData.groupBy("gender").count().show();
        System.out.println("【年龄分布】");
        Row row = spark.sql("select max(age) maxAge, min(age) minAge from user_data").first();
        System.out.println("最大年龄：" + row.get(0) + "\t最小年龄：" + row.get(1));
        // 新建一个统计年龄区间的自定义累加器
        AgeAccumulator ageAccumulator = new AgeAccumulator(row.getInt(0), row.getInt(1), 20);

        userData.select("age").toLocalIterator().forEachRemaining(row1 -> ageAccumulator.add(row1.getInt(0)));
        System.out.println(ageAccumulator.value()); // 原始的无序状态
        System.out.println(ageAccumulator.sortedValue(true)); // 正序
        System.out.println(ageAccumulator.sortedValue(false)); // 倒序
        System.out.println("【职业统计】");
    }
}
