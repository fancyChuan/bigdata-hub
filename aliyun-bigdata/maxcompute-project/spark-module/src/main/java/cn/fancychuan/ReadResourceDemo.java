package cn.fancychuan;


import com.aliyun.odps.FileResource;
import com.aliyun.odps.Odps;
import com.aliyun.odps.OdpsException;
import com.aliyun.odps.Resource;
import com.aliyun.odps.account.Account;
import com.aliyun.odps.account.AliyunAccount;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.rdd.RDD;
import org.apache.spark.sql.SparkSession;
import scala.Tuple2;

import java.util.Arrays;
import java.util.List;

public class ReadResourceDemo {
    public static void main(String[] args) throws OdpsException {
//        Account account = new AliyunAccount("orLPPOJCZqIohWjV", "7IiWCRgX4jK73VzAGLvnDrCU4m1Knx");
//        Odps odps = new Odps(account);
//        String endPoint = "http://service.odps.aliyun.com/api";
//
//        odps.setEndpoint(endPoint);
//        odps.setDefaultProject("wcl_dwh");
//
//        for (Resource resource : odps.resources()) {
//            resource.reload();
//            System.out.println(resource.getType());
//            System.out.println(resource.getName());
//            System.out.println(resource.getCreatedTime());
//        }

        SparkSession spark = SparkSession.builder()
                .appName("readResource WC").master("local[4]")
                .getOrCreate();
        JavaSparkContext sc = JavaSparkContext.fromSparkContext(spark.sparkContext());

        JavaRDD<String> linesRDD = sc.textFile("umkt_config.txt", 2);
        JavaRDD<String> wordsRDD = linesRDD.flatMap(line -> Arrays.asList(line.split(" ")).iterator());
        JavaPairRDD<String, Integer> result = wordsRDD.mapToPair(one -> new Tuple2<>(one, 1)).reduceByKey((cnt1, cnt2) -> cnt1 + cnt2);
        for (Tuple2<String, Integer> stringIntegerTuple2 : result.collect()) {
            System.out.println(stringIntegerTuple2);
        }
    }
}
