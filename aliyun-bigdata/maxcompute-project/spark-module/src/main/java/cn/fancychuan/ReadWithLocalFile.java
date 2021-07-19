package cn.fancychuan;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;
import scala.Tuple2;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

public class ReadWithLocalFile {
    public static void main(String[] args) throws IOException {
        SparkSession spark = SparkSession.builder()
                .appName("readResourceWC")
                .getOrCreate();
        JavaSparkContext sc = JavaSparkContext.fromSparkContext(spark.sparkContext());

        String fileName = args[0];
        System.out.println("*********============***********");
        File dir = new File("./");
        String targetFile = "file://" + dir.getCanonicalPath() + "/" + fileName;
        System.out.println(targetFile);
        JavaRDD<String> linesRDD = sc.textFile(targetFile,1);
        System.out.println(linesRDD.count());
        StringBuilder content = new StringBuilder();
        for (String line : linesRDD.collect()) {
            content.append(line + "\n");
        }
        System.out.println(content.toString());

//        JavaRDD<String> words = (JavaRDD<String>) linesRDD.flatMap(line -> Arrays.asList(line.split(" ")).iterator());
//        JavaPairRDD<String, Integer> resultRDD = words.mapToPair(word -> new Tuple2<>(word, 1))
//                .reduceByKey((cnt1, cnt2) -> cnt1 + cnt2);
//        System.out.println(resultRDD.collect());
//        for (Tuple2<String, Integer> tuple2 : resultRDD.collect()) {
//            System.out.println(tuple2);
//        }
//        System.out.println(readFromLocalFile(targetFile).length());
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
