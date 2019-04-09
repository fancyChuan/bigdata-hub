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

    /**
     * 3. 测试转化函数
     */
    public static void testTransformation() {
        SparkConf conf = new SparkConf().setMaster("local").setAppName("helloSpark");
        JavaSparkContext sc = new JavaSparkContext(conf);
        JavaRDD<String> inputRDD = sc.textFile("E:\\JavaWorkshop\\bigdata-learn\\spark\\src\\main\\resources\\testfile.md");
        JavaRDD<String> scala = inputRDD.filter(line -> line.contains("Scala"));
        JavaRDD<String> merge = scala.union(inputRDD.filter(line -> line.contains("Python")));
        // 下面是行动函数
        System.out.println(merge.count());
        System.out.println(merge.collect());
        System.out.println(merge.take(2));
        System.out.println(merge.first());
    }
}
