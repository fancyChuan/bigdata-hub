package sparkSQL;

import common.Student;
import org.apache.spark.Aggregator;

import java.io.Serializable;

/**
 * Type-Safe User-Defined Aggregate Functions
 * 类型安全的自定义聚合函数，也就是用于row有可以跟bean对应的Datasets的函数
 */
public class MyAverageTyped extends Aggregator<Student, Average, Double> {
}


class Average implements Serializable {
    private long sum;
    private long count;

    public long getSum() {
        return sum;
    }

    public void setSum(long sum) {
        this.sum = sum;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
