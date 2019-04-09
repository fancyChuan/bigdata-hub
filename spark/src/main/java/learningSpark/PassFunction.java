package learningSpark;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import scala.Serializable;

import static learningSpark.Utils.getTestFileRDD;

/**
 * java版向Spark传递函数
 */
public class PassFunction implements Serializable {

    /**
     * 1. 使用匿名内部类进行函数传递
     */
    public void testAnonymousInnerClass() {
        JavaRDD<String> lines = getTestFileRDD();
        JavaRDD<String> python = lines.filter(new Function<String, Boolean>() {
            @Override
            public Boolean call(String s) throws Exception {
                return s.contains("Python");
            }
        });
        System.out.println(python.count());
    }

    /**
     * 2. 使用具名函数进行函数传递
     */
    public void testNamedClass() {
        JavaRDD<String> lines = getTestFileRDD();
        JavaRDD<String> python = lines.filter(new ContainsPython());
        System.out.println(python.count());
        // 传入关键词的具名类
        System.out.println(lines.filter(new Contains("Spark")).count());
    }

    /**
     * 3. 使用Lambda表达式
     */
    public void testLambda() {
        JavaRDD<String> lines = getTestFileRDD();
        JavaRDD<String> python = lines.filter(line -> line.contains("Python"));
        System.out.println(python.count());
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