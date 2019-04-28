package learningSpark.rddProgram;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.storage.StorageLevel;
import scala.Serializable;


/**
 * java版向Spark传递函数
 */
public class PassFunction implements Serializable {
    private JavaRDD<String> lines;

    public PassFunction() {
        SparkConf conf = new SparkConf().setMaster("local").setAppName("passFunction");
        JavaSparkContext sc = new JavaSparkContext(conf);
        this.lines = sc.textFile("E:\\JavaWorkshop\\bigdata-learn\\spark\\src\\main\\resources\\testfile.md");
        System.out.println("构造器初始化，共有行数：" + this.lines.count());
        System.out.println("持久化RDD...");
        this.lines.persist(StorageLevel.MEMORY_AND_DISK());
    }

    public PassFunction(JavaSparkContext sc) {
        this.lines = sc.textFile("E:\\JavaWorkshop\\bigdata-learn\\spark\\src\\main\\resources\\testfile.md");
        System.out.println("构造器初始化，共有行数：" + this.lines.count());
        System.out.println("持久化RDD...");
        this.lines.persist(StorageLevel.MEMORY_AND_DISK());
    }

    /**
     * 1. 使用匿名内部类进行函数传递
     */
    public void testAnonymousInnerClass() {
        JavaRDD<String> python = lines.filter(new Function<String, Boolean>() {
            @Override
            public Boolean call(String s) throws Exception {
                return s.contains("Python");
            }
        });
        System.out.println("使用匿名内部类：" + python.count());
    }

    /**
     * 2. 使用具名函数进行函数传递
     */
    public void testNamedClass() {
        JavaRDD<String> python = lines.filter(new ContainsPython());
        System.out.println("使用具名函数：" + python.count());
        // 传入关键词的具名类
        System.out.println("传入关键词的具名函数：" + lines.filter(new Contains("Spark")).count());
    }

    /**
     * 3. 使用Lambda表达式
     */
    public void testLambda() {
        JavaRDD<String> python = lines.filter(line -> line.contains("Python"));
        System.out.println("使用Lambda表达式：" + python.count());
    }
}

/**
 * 具名类
 */
class ContainsPython implements Function<String, Boolean>, Serializable {
    @Override
    public Boolean call(String s) throws Exception {
        return s.contains("Python");
    }
}

/**
 * 支持传关键词的具名类
 */
class Contains implements Function<String, Boolean>, Serializable {
    private String query;

    public Contains(String query) {
        this.query = query;
    }

    @Override
    public Boolean call(String s) throws Exception {
        return s.contains(query);
    }
}