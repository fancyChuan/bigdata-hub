package cn.fancychuan;


import com.aliyun.odps.FileResource;
import com.aliyun.odps.Odps;
import com.aliyun.odps.OdpsException;
import com.aliyun.odps.Resource;
import com.aliyun.odps.account.Account;
import com.aliyun.odps.account.AliyunAccount;
import org.apache.spark.SparkContext;
import org.apache.spark.SparkFiles;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.rdd.RDD;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.SparkSession;
import scala.Tuple2;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ReadResourceDemo {
    public static void main(String[] args) throws OdpsException, IOException {
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
                .appName("readResourceWC")
                .getOrCreate();
        JavaSparkContext sc = JavaSparkContext.fromSparkContext(spark.sparkContext());

        String fileName = args[0];

        System.out.println("===========================");
        System.out.println(System.getenv("SPARK_YARN_STAGING_DIR"));
        Dataset<String> rddx = spark.read().textFile(System.getenv("SPARK_YARN_STAGING_DIR") + '/' + fileName);
        System.out.println(rddx.collect());
        File dir = new File("./");
        String targetFile = "file://" + dir.getCanonicalPath() + "/" + fileName;
        System.out.println(targetFile);
        System.out.println("[==========]length：" + readFromLocalFile(targetFile).length());

        JavaRDD<String> rdd = sc.textFile(targetFile);
        System.out.println(rdd);

    }

    public static String readFromLocalFile(String path) throws IOException {
        StringBuilder sqls = new StringBuilder();

        int hasRead = 0;
        byte[] bytes = new byte[1024];
        FileInputStream inputStream = new FileInputStream(path);
        while ((hasRead = inputStream.read(bytes))>0) {
            String part = new String(bytes, 0, hasRead);
            sqls.append(part);
        }
        return sqls.toString();
    }
}
