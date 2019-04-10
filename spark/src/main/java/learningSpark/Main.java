package learningSpark;

import org.apache.commons.lang3.StringUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaDoubleRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import java.util.Arrays;
import java.util.List;


public class Main {
    private static SparkConf conf = new SparkConf().setMaster("local[4]").setAppName("javaSpark");
    private static JavaSparkContext sc = new JavaSparkContext(conf);

    public static void main(String[] args) {
        // testWordCount();
        // testHelloSpark();
        // testTransformation();
        // testPassFunction();
        // testSample();
        // testAction();
        testConvertAndMean();
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

    /**
     * 4. 向Spark传递函数
     */
    public static void testPassFunction() {
        PassFunction pf = new PassFunction();
        pf.testAnonymousInnerClass();
        pf.testNamedClass();
        pf.testLambda();
    }

    /**
     * 5. 测试采样函数
     *
     * 使用场景： 检查导致数据倾斜的key
     */
    public static void testSample() {
        JavaRDD<Integer> rdd = sc.parallelize(Arrays.asList(1, 3, 5, 7, 8));
        // 元素不可多次采样，每个元素被抽到的概率是0.5
        JavaRDD<Integer> sample1 = rdd.sample(false, 0.5);
        sample1.foreach(x -> System.out.print(x + "\t"));
        // 元素可以多次采样，每个元素被抽到的期望次数是2
        System.out.println("===========");
        JavaRDD<Integer> sample2 = rdd.sample(true, 2);
        sample2.foreach(x -> System.out.print(x + "\t"));
    }

    /**
     * 6. 测试行动操作
     */
    public static void testAction() {
        JavaRDD<Integer> rdd = sc.parallelize(Arrays.asList(1, 3, 5, 7, 8));
        // reduce() 用于求RDD中元素的累加
        System.out.println(rdd.reduce((a, b) -> a + b));
        // fold() 每个分区第一次调用时都有个初始值作为第一次调用时的结果
        // setMaster("local[4]") 那么rdd就有4个分区，那么结果会比reduce的多500
        System.out.println("RDD分区数为： "+ rdd.getNumPartitions() + "\t运行结果: " + rdd.fold(100, (a, b) -> a + b));
        // aggregate()
        AvgCount avgCount = rdd.aggregate(new AvgCount(0, 0),
                (acc, value) -> new AvgCount(acc.total + value, acc.cnt + 1),
                (acc1, acc2) -> new AvgCount(acc1.total + acc2.total, acc1.cnt + acc2.cnt)
                );
        System.out.println("aggregate求平均： " + avgCount.avg());
    }

    /**
     * 7. 将IntegerRDD转为DoubleRDD并求平均
     */
    public static void testConvertAndMean() {
        JavaRDD<Integer> rdd = sc.parallelize(Arrays.asList(1, 3, 5));
        JavaDoubleRDD doubleRDD = rdd.mapToDouble(x -> x*x); // 注意这个不是 JavaRDD<Double>
        doubleRDD.foreach(x -> System.out.println("[doubleRDD]" + x));
        System.out.println("平均值为： " + doubleRDD.mean());
    }
}
