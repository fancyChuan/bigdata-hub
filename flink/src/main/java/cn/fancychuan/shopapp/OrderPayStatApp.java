package cn.fancychuan.shopapp;

import cn.fancychuan.shopapp.bean.OrderEvent;
import cn.fancychuan.shopapp.bean.TxEvent;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.ConnectedStreams;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.CoProcessFunction;
import org.apache.flink.util.Collector;

import java.util.HashMap;

/**
 * 订单支付实时监控
 *  对账成功即打印出来
 */
public class OrderPayStatApp {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStreamSource<String> orderInput = env.readTextFile(OrderPayStatApp.class.getClassLoader().getResource("OrderLog.csv").getPath());
        DataStreamSource<String> payInput = env.readTextFile(OrderPayStatApp.class.getClassLoader().getResource("ReceiptLog.csv").getPath());

        SingleOutputStreamOperator<OrderEvent> orderStream =
                orderInput.map(new MapFunction<String, OrderEvent>() {
                    @Override
                    public OrderEvent map(String s) throws Exception {
                        String[] items = s.split(",");
                        return new OrderEvent(
                                Long.parseLong(items[0]),
                                items[1],
                                items[2],
                                Long.parseLong(items[3])
                        );
                    }
                });
        SingleOutputStreamOperator<TxEvent> payStream =
                payInput.map(new MapFunction<String, TxEvent>() {
                    @Override
                    public TxEvent map(String s) throws Exception {
                        String[] items = s.split(",");
                        return new TxEvent(items[0], items[1], Long.parseLong(items[2]));
                    }
                });

        ConnectedStreams<OrderEvent, TxEvent> connectedStream = orderStream.connect(payStream);

         // 注意下面这一行代码，如果不适用keyBy()直接使用process的话，就会受到并行度影响
         // 可能会导致同一个交易编号的两条数据被分配到不同的子任务中
        ConnectedStreams<OrderEvent, TxEvent> keyByStream = connectedStream.keyBy(order -> order.getTxId(), TxEvent::getTxId);
        SingleOutputStreamOperator<String> processResult =
                connectedStream.process(new CoProcessFunction<OrderEvent, TxEvent, String>() {
                    HashMap<String, OrderEvent> orderMap = new HashMap<String, OrderEvent>();
                    HashMap<String, TxEvent> payMap = new HashMap<>();
                    @Override
                    public void processElement1(OrderEvent value, CoProcessFunction<OrderEvent, TxEvent, String>.Context ctx, Collector<String> out) throws Exception {
                        // 订单流中，存在eventType="pay"的数据，才会有txId
                        OrderEvent orderEvent = orderMap.get(value.getTxId());
                        if (orderEvent == null) {
                            orderMap.put(value.getTxId(), value);
                        } else {
                            out.collect("订单[" + value.getOrderId() + "]对账成功");
                            orderMap.remove(value.getTxId());
                        }
                    }

                    /**
                     * 支付结果流的处理逻辑
                     */
                    @Override
                    public void processElement2(TxEvent value, CoProcessFunction<OrderEvent, TxEvent, String>.Context ctx, Collector<String> out) throws Exception {
                        OrderEvent orderEvent = orderMap.get(value.getTxId());
                        if (orderEvent == null) { //说明订单流迟到了
                            // 下面这行代码似乎没有必要加
                            // payMap.put(value.getTxId(), value);
                        } else {
                            out.collect("订单[" + orderEvent.getOrderId() + "]对账成功");
                            orderMap.remove(value.getTxId());
                        }
                    }
                });

        processResult.print("对账result");

        env.execute();
    }
}
