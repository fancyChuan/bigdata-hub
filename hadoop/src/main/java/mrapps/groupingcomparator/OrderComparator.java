package mrapps.groupingcomparator;


import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

public class OrderComparator extends WritableComparator {

    protected OrderComparator() { // todo:需要好好思考下为什么要加上这个构造器。
        super(OrderBean.class, true); // 在反序列化的时候需要先创建一个空对象，之后把反序列化之后的内容丢到空对象成为真正的对象使用
    }

    @Override
    public int compare(WritableComparable a, WritableComparable b) {
        OrderBean oa = (OrderBean) a;
        OrderBean ob = (OrderBean) b;
        return oa.getOrderId().compareTo(ob.getOrderId());
    }
}
