package learningSpark.sparkSQL;

import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.util.Arrays;

/**
 * schema生成的几种方式
 */
public class SchemaTest {

    public static void main(String[] args) {
        first();
        second();
        testStructField();
    }

    /**
     * 1. DataTypes.createStructType() 有两个重写方法，参数分别为：
     *  [1] StructField[] fields
     *  [2] List<StructField> list
     */
    public static void first() {
        StructField name = DataTypes.createStructField("name", DataTypes.StringType, true);
        StructField age = DataTypes.createStructField("age", DataTypes.IntegerType, true);
        // 直接新建一个StructField[]对象
        StructType structType = DataTypes.createStructType(new StructField[]{name, age});
        System.out.println(structType);
        // 使用Arrays类得到 List<StructType>
        StructType structType1 = DataTypes.createStructType(Arrays.asList(name, age));
        System.out.println(structType1);
        System.out.println("====================");
    }

    /**
     * 2. 只使用StructType类
     */
    public static void second() {
        StructField name = DataTypes.createStructField("name", DataTypes.StringType, true);
        StructField age = DataTypes.createStructField("age", DataTypes.IntegerType, true);
        // 使用new StructType()
        StructType schema = new StructType(new StructField[]{name, age});
        System.out.println(schema);
        // 从另一个StructType中新增得到
        StructType structType = new StructType();
        structType = schema.add("address", "STRING");
        System.out.println(structType);
        System.out.println("==============");
    }

    /**
     * 3. 创建StructField的方式
     */
    public static void testStructField() {
        // 使用DataTypes.createStructField()
        StructField name = DataTypes.createStructField("name", DataTypes.StringType, true);
        // 直接new StructField()
        StructField age = new StructField("age", DataTypes.IntegerType, false, null);
        System.out.println(new StructType(new StructField[]{name, age}));
    }
}
