package sparkSQL;

import common.Person;
import common.Student;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.*;


import java.util.Arrays;
import java.util.Collections;

import static org.apache.spark.sql.functions.col;

public class Main {
    private static SparkSession spark = SparkSession.builder().master("local[1]").appName("hello-sparkSQL").getOrCreate();

    /**
     * 1. SparkSQL基本使用
     */
    public static void helloSparkSQL() throws AnalysisException {
        SparkSession spark = SparkSession.builder().master("local[1]").appName("hello-word").getOrCreate();
        Dataset<Row>df = spark.read().json("E:\\JavaWorkshop\\bigdata-learn\\spark\\src\\main\\resources\\jsonFile.txt");
        df.show();
        df.printSchema(); // df的字段等元信息

        df.select("name").show();
        df.select(col("name"), col("score").plus(1));
        df.groupBy("name").count().show();
        df.filter(col("score").gt(95)).show();
        // 创建一个临时的视图
        df.createOrReplaceTempView("student");
        Dataset<Row> sqlDF = spark.sql("select id, name from student");
        sqlDF.show();
        // Register the DataFrame as a global temporary view
        df.createGlobalTempView("student");
        // Global temporary view is tied to a system preserved database `global_temp`
        spark.sql("SELECT * FROM global_temp.student").show();
    }

    /**
     * 2. 创建Datasets
     */
    public static void testCreateDataSet() {
        // 从java bean创键
        Student student = new Student("诸葛亮", 66.66, 66);
        Encoder<Student> beanEncoder = Encoders.bean(Student.class);
        Dataset<Student> dataset = spark.createDataset(Collections.singletonList(student), beanEncoder);
        dataset.show();
        // 从基本类型创建
        Encoder<Integer> integerEncoder = Encoders.INT();// 创键一个int类型的encoder
        Dataset<Integer> dataset1 = spark.createDataset(Arrays.asList(1, 2, 3, 4), integerEncoder);
        // Dataset.map() 方法有两个重载方法，需要通过 (MapFunction<Integer, Integer>) 将Lambda表达式的返回值类型转换
        Dataset<Integer> transformed = dataset1.map((MapFunction<Integer, Integer>) value -> value + 1, integerEncoder);
        transformed.show();
        System.out.println(transformed.collectAsList());
        // DataFrame可以转为Dataset
        String path = "E:\\JavaWorkshop\\bigdata-learn\\spark\\src\\main\\resources\\jsonFile.txt";
        Dataset<Student> dataset2 = spark.read().json(path).as(beanEncoder);
        dataset2.show();
    }

    /**
     * 3. 利用反射机制将RDD转为Datasets，
     *  - SparkSQL会根据反射的信息推断出Schema
     *  - Datasets每一行的列可以通过索引，也可以通过列名获取
     */
    public static void testConvertRDDUsingRelection() {
        // 这里使用SparkSession来读取文件，也可以通过SparkContext，只是SparkContext.textFile得到的是RDD，而SparkSession得到的是Datasets
        Dataset<String> dataset = spark.read().textFile("E:\\JavaWorkshop\\bigdata-learn\\spark\\src\\main\\resources\\people.txt");
        dataset.show();
        // 转为RDD，并处理成Person
        JavaRDD<Person> rdd = dataset.javaRDD().map(line -> {
            String[] strings = line.split(", ");
            Person person = new Person();
            person.setAge(Integer.parseInt(strings[1]));
            person.setName(strings[0]);
            return person;
        });

        // 转为DataFrame，但是需要传入java bean的元信息
        Dataset<Row> df = spark.createDataFrame(rdd, Person.class);
        df.createOrReplaceTempView("people");
        Dataset<Row> teenagersDF = spark.sql("SELECT name, age FROM people WHERE age BETWEEN 13 AND 19");
        teenagersDF.show();
        // 每一行的列可以通过索引获取，也可以通过名字获取
        Encoder<String> stringEncoder = Encoders.STRING();
        Dataset<String> ds = teenagersDF.map(
                (MapFunction<Row, String>) row -> "Name:" + row.getString(0) + " age:" + row.<Integer>getAs("age"),
                stringEncoder);
        ds.show();

    }

    public static void testJoin() {
        Dataset<Row> dept = spark.read().json("E:\\JavaWorkshop\\bigdata-learn\\spark\\src\\main\\resources\\department.json");
        Dataset<Row> emp = spark.read().json("E:\\JavaWorkshop\\bigdata-learn\\spark\\src\\main\\resources\\employee.json");
        dept.join(emp, "id");
    }

    public static void main(String[] args) throws AnalysisException {
        // helloSparkSQL();
        // testCreateDataSet();
        testConvertRDDUsingRelection();
    }
}
