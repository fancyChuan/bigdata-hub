package learningSpark.dataReadingAndSaving;

import learningSpark.common.Student;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

/**
 * spark数据读取与保存
 */
public class Main {
    private static SparkConf conf = new SparkConf().setMaster("local[4]").setAppName("javaSpark");
    private static JavaSparkContext sc = new JavaSparkContext(conf);

    /**
     * 1. sc.textFile() 与 sc.wholeTextFiles() 的区别
     *
     *  textFile(): 传递目录时，分区数由文件数决定，得到是一个普通RDD，每个文件中的每一行均为一个元素
     *  wholeTextFiles(): 得到一个PairRDD，文件名作为键，整个文件的内容作为value
     */
    public static void testWholeTextFiles() {
        String dir = "E:\\JavaWorkshop\\bigdata-learn\\hadoop\\input\\ncdc\\micro-tab";
        JavaPairRDD<String, String> pairRDD = sc.wholeTextFiles(dir, 2);
        System.out.println("分区数为：" + pairRDD.getNumPartitions());
        System.out.println(pairRDD.collect());

        System.out.println("====== 使用textFile() =======");
        JavaRDD<String> rdd = sc.textFile(dir, 2); // 虽然设置了2，但是实际分区为3，因为有三个文件
        System.out.println("textFile分区数：" + rdd.getNumPartitions());
        System.out.println(rdd.collect());
    }

    /**
     * 2. JSON操作
     */
    public static void testJsonOpt() {
        String path = "E:\\JavaWorkshop\\bigdata-learn\\spark\\src\\main\\resources\\jsonFile.txt";
        JavaRDD<String> inputs = sc.textFile(path);
        JavaRDD<Student> result = inputs.mapPartitions(new ParseJson());
        // System.out.println("解析结果为：");
        // System.out.println(result.collect());

        JavaRDD<String> formatted = result.filter(student -> student.getScore() > 90).mapPartitions(new WriteJson());
        System.out.println(formatted.collect());
        formatted.saveAsTextFile("E:\\JavaWorkshop\\bigdata-learn\\spark\\target\\out\\writeJson");
    }


    public static void main(String[] args) {
        testWholeTextFiles();
        testJsonOpt();
    }
}
