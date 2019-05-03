package mlWithSpark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.SparkSession;
import scala.Tuple2;

public class Main {
    private static SparkConf conf = new SparkConf().setMaster("local").setAppName("mlWithSpark");
    private static SparkSession spark = SparkSession.builder().config(conf).getOrCreate();

    public static void main(String[] args) {
        // firstJavaApp();
        exploreUserData();
    }

    public static void firstJavaApp() {
        JavaRDD<String> rdd = spark.read()
                .textFile("E:\\JavaWorkshop\\bigdata-learn\\spark\\data\\UserPurchaseHistory.csv")
                .toJavaRDD();
        JavaRDD<String[]> data = rdd.map(item -> item.split(","));
        long diffNum = data.map(strings -> strings[0]).distinct().count();
        Double totalRevenue = data.map(strings -> Double.valueOf(strings[2])).reduce(((v1, v2) -> v1 + v2));
        JavaPairRDD<String, Integer> productCnt = data.mapToPair(strings -> new Tuple2<>(strings[1], 1))
                .reduceByKey(((v1, v2) -> v1 + v2)).sortByKey(false);
        System.out.println("总购买人数：" + diffNum);
        System.out.println("总销售额：" + totalRevenue);
        productCnt.foreach(tuple -> System.out.println("most propular, product=" + tuple._1 + " 被购买的次数：" + tuple._2));

    }

    public static void exploreUserData() {
        DataExplore explore = new DataExplore(spark);
        explore.userDataExplore();
    }

}
