package sparkSQL;

import org.apache.spark.sql.Row;
import org.apache.spark.sql.expressions.MutableAggregationBuffer;
import org.apache.spark.sql.expressions.UserDefinedAggregateFunction;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义聚合函数
 */
public class MyAverage extends UserDefinedAggregateFunction {
    private StructType inputSchema; // 函数的参数信息
    private StructType bufferSchema;

    public MyAverage() {
        // 创建入参数据类型， select xxx(a, b) from xx  其中a, b的类型都应该在inputSchema中定义
        List<StructField> inputFields = new ArrayList<>();
        inputFields.add(DataTypes.createStructField("salary", DataTypes.LongType, true));
        inputSchema = DataTypes.createStructType(inputFields);
        // 创建中间数据类型，比如这里求平均，需要最后用 sum / count 所以需要sum和count两个StructField
        StructField[] bufferFields = new StructField[2];
        bufferFields[0] = DataTypes.createStructField("sum", DataTypes.LongType, true);
        bufferFields[1] = DataTypes.createStructField("count", DataTypes.LongType, true);
        bufferSchema = DataTypes.createStructType(bufferFields);
    }

    @Override
    public StructType inputSchema() {
        return inputSchema;
    }

    @Override
    public StructType bufferSchema() {
        return bufferSchema;
    }

    @Override
    public DataType dataType() {
        return DataTypes.DoubleType;
    }

    // Wheather return the same output on the identical input
    @Override
    public boolean deterministic() {
        return true;
    }

    // 初始中间数据。buffer支持Row的操作，用update来设初始值
    @Override
    public void initialize(MutableAggregationBuffer buffer) {
        buffer.update(0, 0L);
        buffer.update(1, 0L);
    }

    @Override
    public void update(MutableAggregationBuffer buffer, Row input) {
        if (!input.isNullAt(0)) { // row的第i个字段是否为null
            buffer.update(0, buffer.getLong(0) + input.getLong(0)); // 累加sum
            buffer.update(1, buffer.getLong(1) + 1); // 计数count
        }
    }

    @Override
    public void merge(MutableAggregationBuffer buffer1, Row buffer2) {
        buffer1.update(0, buffer1.getLong(0) + buffer2.getLong(0));
        buffer1.update(1, buffer1.getLong(1) + buffer2.getLong(1));
    }

    @Override
    public Double evaluate(Row buffer) {
        return ((double) buffer.getLong(0)) / buffer.getLong(1);
    }
}
