package learningSpark.sparkSQL;

import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

/**
 * schema生成的几种方式
 */
public class SchemaTest {

    public static void main(String[] args) {
        first();
        second();
    }

    /**
     * 1. DataTypes.createStructType() 有两个重写方法，参数分别为：
     *  [1] StructField[] fields
     *  [2] List<StructField> list
     */
    public static void first() {
        StructField name = DataTypes.createStructField("name", DataTypes.StringType, true);
        StructField age = DataTypes.createStructField("age", DataTypes.IntegerType, true);
        StructType structType = DataTypes.createStructType(new StructField[]{name, age});
        System.out.println(structType);
    }

    /**
     * 2. 只使用StructType类
     *
     */
    public static void second() {
        StructType structType = new StructType();
        structType = structType.add("name", "STRING")
                .add("age", DataTypes.IntegerType);
        System.out.println(structType);
    }
}
