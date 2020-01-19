package cn.fancychuan.spark244.wordcount;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

import java.util.Arrays;

public class WordCountApp {

    public static void main(String[] args) {
        if (args.length == 0) {
            args = new String [] {"spark/data/wordcount.txt", "spark/target/wordcount"};
        }
        SparkConf sparkConf = new SparkConf().setAppName("wordcount-java");
        JavaSparkContext sc = new JavaSparkContext(sparkConf);
        JavaRDD<String> wordsRDD = sc.textFile(args[0]).flatMap(line -> Arrays.asList(line.split(" ")).iterator());
        JavaPairRDD<String, Long> resultRDD = wordsRDD.mapToPair(word -> new Tuple2<>(word, 1L)).reduceByKey((x, y) -> x + y);
        System.out.println("=========== gogogo ============");
        System.out.println(resultRDD.collect());
        System.out.println("===============================");
        resultRDD.saveAsTextFile(args[1]);
    }
}
