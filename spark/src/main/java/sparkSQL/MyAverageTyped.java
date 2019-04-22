package sparkSQL;

import common.Employee;

import org.apache.spark.sql.Encoder;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.expressions.Aggregator;

import java.io.Serializable;


/**
 * Type-Safe User-Defined Aggregate Functions
 * 类型安全的自定义聚合函数，也就是用于row有可以跟bean对应的Datasets的函数
 */
public class MyAverageTyped extends Aggregator<Employee, Average, Double> {
    @Override
    public Average zero() {
        return new Average(0L, 0L);
    }

    @Override
    public Average reduce(Average b, Employee a) {
        long newSalary = b.getSum() + a.getSalary();
        long newCount = b.getCount() + 1L;
        b.setSum(newSalary);
        b.setCount(newCount);
        return b;
    }

    @Override
    public Average merge(Average b1, Average b2) {
        b1.setSum(b1.getSum() + b2.getSum());
        b1.setCount(b1.getCount() + b2.getCount());
        return b1;
    }

    @Override
    public Double finish(Average reduction) {
        return ((double) reduction.getSum()) / reduction.getCount();
    }

    @Override
    public Encoder<Average> bufferEncoder() {
        return Encoders.bean(Average.class);
    }

    @Override
    public Encoder<Double> outputEncoder() {
        return Encoders.DOUBLE();
    }
}


