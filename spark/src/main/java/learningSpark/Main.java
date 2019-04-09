package learningSpark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import java.util.Arrays;
import java.util.List;


public class Main {

    public static void main(String[] args) {
        // testWordCount();
        // testHelloSpark();
        testTransformation();
    }

    public static void testWordCount() {
        String input = "E:\\JavaWorkshop\\bigdata-learn\\spark\\src\\main\\resources\\testfile.md";
        String output = "E:\\JavaWorkshop\\bigdata-learn\\spark\\out\\wordcount";
        WordCount.main(new String[] {input, output});
    }

    /**
     * 2. 实例化一个sc并将集合并行化
     * 使用的是 JavaSparkContext 而不是 SparkContext
     * python、scala使用的是SparkContext
     */
    public static void testHelloSpark() {
        SparkConf conf = new SparkConf().setMaster("local").setAppName("helloSpark");
        JavaSparkContext sc = new JavaSparkContext(conf);
        List<String> list = Arrays.asList("hello", "spark");
        JavaRDD<String> lines = sc.parallelize(list);
        System.out.println(lines);
        System.out.println(lines.collect());
    }

    public static void testTransformation() {
        SparkConf conf = new SparkConf().setMaster("local").setAppName("helloSpark");
        JavaSparkContext sc = new JavaSparkContext(conf);
        JavaRDD<String> inputRDD = sc.textFile("../resources/testfile.md");
        inputRDD.filter(line -> {return line.contains("scala");});
    }
}
