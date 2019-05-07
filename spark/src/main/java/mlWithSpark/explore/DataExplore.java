package mlWithSpark.explore;

import mlWithSpark.udf.ExtractMovieYear;
import org.apache.spark.api.java.JavaDoubleRDD;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.functions;
import org.apache.spark.sql.types.DataTypes;
import scala.Tuple2;


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
                .schema(SchemaInfo.userSchame) // 指定schema有两种方式，还可以 .schema("id LONG, age LONG, gender STRING, occupation STRING, zipcode STRING")
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
        // 使用自定义累加器统计年龄区间情况
        userData.select("age").toLocalIterator().forEachRemaining((Row row1) -> ageAccumulator.add(row1.getInt(0)));
        System.out.println(ageAccumulator.value()); // 原始的无序状态
        System.out.println(ageAccumulator.sortedValue(true)); // 正序
        System.out.println(ageAccumulator.sortedValue(false)); // 倒序
        System.out.println("【职业统计】");
        spark.sql("select occupation, count(1) cnt from user_data group by occupation order by cnt desc").show();
    }

    /**
     * 电影数据探索
     *
     *  需求点：
     *      1. 提取电影上映年份并统计
     *          方法1： 转为RDD，使用mapToPair().reduceByKey()
     *          方法2： 自定义SparkSQL函数，实现 UDF1<String> 接口
     */
    public void movieDataExplore() {
        Dataset<Row> movieData = spark.read()
                .format("csv")
                .option("delimiter", "|")
                .option("header", "false")
                .schema(SchemaInfo.movieSchame)
                .load("E:\\JavaWorkshop\\bigdata-learn\\spark\\data\\ml-100k\\u.item");
        movieData.show(5, false);
        movieData.createOrReplaceTempView("movie_data");
        // LongAccumulator yearAccumulator = spark.sparkContext().longAccumulator();
        // movieData.toJavaRDD().foreach(row -> yearAccumulator.add(extractYear(row.getString(2)))); // 这里的map算子用到了外部对象的方法extractYear，应该对象需要能够序列化
        // 其实上面这一行直接使用 LongAccumulator 是有问题的，这个累加器并不能用来对月份计数，实际上是累加而已。还是乖乖的转为JavaPairRDD操作
        JavaPairRDD<Integer, Integer> movieYearCount = movieData.toJavaRDD()
                .mapToPair(row -> { // 把提取的逻辑放到Lambda中，避免序列化
                    Integer year;
                    try {
                        String movieDay = row.getString(2);
                        int length = movieDay.length();
                        year = Integer.valueOf(movieDay.substring(length - 4, length));
                    } catch (NullPointerException e) {
                        year = 1900;
                    }
                    return new Tuple2<>(year, 1);
                })
                .reduceByKey(((v1, v2) -> v1 + v2))
                .sortByKey();
        System.out.println(movieYearCount.collect());
        // SparkSQL UDF实现电影上映月份统计
        spark.udf().register("movieYear", (String movieDay) -> {
            Integer year;
            try {
                int length = movieDay.length();
                year = Integer.valueOf(movieDay.substring(length - 4, length));
            } catch (Exception e) {
                year = 1900;
            }
            return year;
        }, DataTypes.IntegerType);
        spark.sql("select movieYear(date) year, count(1) cnt from movie_data group by movieYear(date)").show();
        // 不使用Lambda实现UDF
        spark.udf().register("movieYear2", new ExtractMovieYear(), DataTypes.IntegerType);
        spark.sql("select movieYear2(date) year, count(1) cnt from movie_data group by movieYear2(date)").show();
    }

    public void ratingDataExplore() {
        Dataset<Row> ratingData = spark.read()
                .format("csv")
                .option("delimiter", "\t")
                .option("header", false)
                .schema(SchemaInfo.ratingSchema)
                .load("E:\\JavaWorkshop\\bigdata-learn\\spark\\data\\ml-100k\\u.data");
        ratingData.show(5);
        ratingData.createOrReplaceTempView("rating_data");
        // 求最大最小平均值
        spark.sql("select max(rating), min(rating), avg(rating) from rating_data").show();
        ratingData.agg(functions.max("rating"), functions.avg("rating")); // 另一种实现方法
        spark.sql("select rating, count(1) cnt from rating_data group by rating order by cnt desc").show();
        // 分组排序的几种写法
        spark.sql("select user_id, count(1) cnt from rating_data group by user_id order by cnt desc").show();
        ratingData.groupBy("user_id").count().orderBy("count"); // orderBy() 是 sort()的别名
        ratingData.groupBy("user_id").count().sort(functions.col("count").desc()); // 倒序的写法
        ratingData.groupBy("usre_id").agg(functions.count("user_id").as("cnt")).sort("cnt"); // 使用agg()函数
        // 使用 JavaDoubleRDD 实现同样的功能
        JavaDoubleRDD ratingRDD = ratingData.select("rating").toJavaRDD().mapToDouble((Row row) -> row.getInt(0));
        System.out.println("最大值：" + ratingRDD.max() + "\t平均值：" + ratingRDD.mean() + "\t最小值：" + ratingRDD.min());
        System.out.println("计数：" + ratingRDD.countByValue());

    }

}
