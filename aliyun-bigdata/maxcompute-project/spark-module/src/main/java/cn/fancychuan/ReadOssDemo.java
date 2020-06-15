package cn.fancychuan;

import org.apache.spark.SparkFiles;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ReadOssDemo {
    public static void main(String[] args) {
        SparkSession spark = SparkSession.builder()
                .config("spark.hadoop.fs.oss.credentials.provider", "org.apache.hadoop.fs.aliyun.oss.AliyunStsTokenCredentialsProvider")
                .config("spark.hadoop.fs.oss.ststoken.roleArn", "acs:ram::1270739458340527:role/aliyunodpsdefaultrole")
                .config("spark.hadoop.fs.oss.endpoint", "oss-cn-hangzhou-zmf.aliyuncs.com")
                .appName("SparkUnstructuredDataCompute")
                .getOrCreate();
        JavaSparkContext sc = JavaSparkContext.fromSparkContext(spark.sparkContext());

        String fileName = args[0];

        System.out.println("===========================first");
        File f1 = new File(fileName);
        System.out.println("默认file:" + f1.getAbsolutePath());
        System.out.println("读取长度：" + readFromLocalFile(f1.getAbsolutePath()).length());

        File f2 = new File("file://./" + fileName);
        System.out.println("相对路径：" + f2.getAbsolutePath());
        System.out.println("读取长度：" + readFromLocalFile(f1.getAbsolutePath()).length());
        System.out.println("===========================secord");
        String absPath = SparkFiles.get(fileName);
        System.out.println("绝对路径" + absPath);
        JavaRDD<String> linesRDD = sc.textFile(absPath);
        System.out.println(linesRDD.collect());
        System.out.println("============================end===");
    }

    public static String readFromLocalFile(String path) {
        StringBuilder sqls = new StringBuilder();
        try {
            int hasRead = 0;
            byte[] bytes = new byte[1024];
            FileInputStream inputStream = new FileInputStream(path);
            while ((hasRead = inputStream.read(bytes))>0) {
                String part = new String(bytes, 0, hasRead);
                sqls.append(part);
            }
        } catch (Exception e) {
            System.out.println("--------- error -------------");
            e.printStackTrace();
        }

        return sqls.toString();
    }
}
