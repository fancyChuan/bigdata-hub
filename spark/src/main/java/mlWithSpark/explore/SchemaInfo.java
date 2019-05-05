package mlWithSpark.explore;

import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;

import java.util.Arrays;

public interface SchemaInfo {

    StructType userSchame = DataTypes.createStructType(Arrays.asList(
            DataTypes.createStructField("id", DataTypes.IntegerType, true),
            DataTypes.createStructField("age", DataTypes.IntegerType, true),
            DataTypes.createStructField("gender", DataTypes.StringType, true),
            DataTypes.createStructField("occupation", DataTypes.StringType, true),
            DataTypes.createStructField("zipCode", DataTypes.StringType, true)
    ));

    StructType movieSchame = DataTypes.createStructType(Arrays.asList(
            DataTypes.createStructField("id", DataTypes.StringType, true),
            DataTypes.createStructField("name", DataTypes.StringType, true),
            DataTypes.createStructField("date", DataTypes.StringType, true),
            DataTypes.createStructField("what", DataTypes.StringType, true), // 不知道这个字段代表什么，先用what代替
            DataTypes.createStructField("url", DataTypes.StringType, true)
    ));
}
