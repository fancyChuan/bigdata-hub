package learningSpark.sparkSQL;

import learningSpark.common.Employee;
import learningSpark.common.Person;
import learningSpark.common.Student;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.*;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.apache.spark.sql.functions.col;

public class Main {
    private static SparkSession spark = SparkSession.builder().master("local[1]").appName("hello-learningSpark.sparkSQL").getOrCreate();

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
    public static void testConvertRDDUsingReflection() {
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

    /**
     * 4. 指定Schema信息将RDD转为Datasets
     *
     *  使用DataTypes.createStructField 创建结构化信息的字段
     *  使用DataTypes.createStructType  创建结构化信息schema
     */
    public static void testConvertRDDGivingSchema() {
        JavaRDD<String> rdd = spark.sparkContext()
                .textFile("E:\\JavaWorkshop\\bigdata-learn\\spark\\src\\main\\resources\\people.txt", 1)
                .toJavaRDD(); // 上面一行得到的RDD类型，并不是JavaRDD，需要转换
        // Generate the schema based on the string of schema
        String schemaString = "name age";
        ArrayList<StructField> arrayList = new ArrayList<>();
        for (String fieldName : schemaString.split(" ")) {
            StructField field = DataTypes.createStructField(fieldName, DataTypes.StringType, true);
            arrayList.add(field);
        }
        StructType schema = DataTypes.createStructType(arrayList);

        // 先把RDD转为Row数据，才能使用自定义的Schema
        JavaRDD<Row> rowRDD = rdd.map(line -> {
            String[] fields = line.split(", ");
            return RowFactory.create(fields[0], fields[1]);
        });
        Dataset<Row> df = spark.createDataFrame(rowRDD, schema);
        df.show();
    }

    /**
     * 5. 无类型的自定义聚合函数
     */
    public static void testUntypedUDF() {
        // 注册函数
        spark.udf().register("myAverage", new MyAverageUntyped());
        // 使用函数
        Dataset<Row> emp = spark.read().json("E:\\JavaWorkshop\\bigdata-learn\\spark\\src\\main\\resources\\employee.json");
        emp.createOrReplaceTempView("employee");
        Dataset<Row> result = spark.sql("select depId, myAverage(salary) avgSalary from employee group by depId");
        result.show();
    }

    /**
     * 6. 强类型的自定义聚合函数
     * 可能异常：
     *  No applicable constructor/method found for actual parameters "long"; candidates are: "public void common.Employee.setAge(int)"
     *  [*]需要把Employee的age类型从int改为long，似乎spark里面不能使用int
     *  No applicable constructor/method found for actual parameters "long"; candidates are: "public static java.lang.Integer java.lang.Integer.valueOf(java.lang.String, int) throws java.lang.NumberFormatException", "public static java.lang.Integer java.lang.Integer.valueOf(int)", "public static java.lang.Integer java.lang.Integer.valueOf(java.lang.String) throws java.lang.NumberFormatException"
     *  [*]也不能是Integer，要改为long
     *  No applicable constructor/method found for zero actual parameters; candidates are: "public long learningSpark.sparkSQL.Average.getCount()"
     *  [*]需要把Average类设为public，不能跟MyAverageTyped写在一个文件
     */
    public static void testTypedUDF() {
        //TODO: No applicable constructor/method found for zero actual parameters; candidates are: "public long learningSpark.sparkSQL.Average.getCount()"
        // 创建Datasets的时候指定类型，注意和上一个函数的对比Dataset<Row> emp 这里是Dataset<Employee> emp
        Dataset<Employee> emp = spark.read()
                .json("E:\\JavaWorkshop\\bigdata-learn\\spark\\src\\main\\resources\\employee.json")
                .as(Encoders.bean(Employee.class));
        emp.show();
        emp.printSchema();

        MyAverageTyped average = new MyAverageTyped();
        TypedColumn<Employee, Double> averageSalary = average.toColumn().name("average_salary");
        Dataset<Double> result = emp.select(averageSalary);
        result.show();
        spark.stop();
    }

    public static void testJoin() {
        Dataset<Row> dept = spark.read().json("E:\\JavaWorkshop\\bigdata-learn\\spark\\src\\main\\resources\\department.json");
        Dataset<Row> emp = spark.read().json("E:\\JavaWorkshop\\bigdata-learn\\spark\\src\\main\\resources\\employee.json");
        dept.join(emp, "id");
    }

    public static void main(String[] args) throws AnalysisException {
        helloSparkSQL();
        testCreateDataSet();
        testConvertRDDUsingReflection();
        testConvertRDDGivingSchema();
        testUntypedUDF();
        testTypedUDF();
    }
}
