package cn.fancychuan.state;

import cn.fancychuan.SensorReading;
import org.apache.flink.api.common.state.*;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

import java.util.Iterator;

public class StateKeyedProcessFunction extends KeyedProcessFunction {
    /**
     * 定义状态
     */
    ValueState<Integer> valueState;
    ListState<String> listState;
    MapState<String, SensorReading> mapState;

    /**
     * ValueState初始化/创建
     */
    @Override
    public void open(Configuration parameters) throws Exception {
        // 从运行时状态中获取状态，或者使用默认值对状态进行初始化
        valueState = getRuntimeContext().getState(new ValueStateDescriptor<Integer>("valueState", Integer.class, 0));
        listState = getRuntimeContext().getListState(new ListStateDescriptor<String>("listState", String.class));
        mapState = getRuntimeContext().getMapState(new MapStateDescriptor<String, SensorReading>("mapState", String.class,SensorReading.class));
    }

    @Override
    public void processElement(Object value, Context ctx, Collector out) throws Exception {
        valueState.value(); // 获取状态值
        // valueState.update(66);   // 更新状态值
        // valueState.clear(); // 清空

        // listState.add(item);
        // listState.addAll(list); // 添加整个list
        Iterator<String> iterator = listState.get().iterator();
        // listState.update(list); // 更新整个集合

        // mapState.get();
        // mapState.put();
        Iterator<String> iterator1 = mapState.keys().iterator();
        Iterator<SensorReading> iterator2 = mapState.values().iterator();
        // mapState.contains();
        // mapState.putAll(map);
        // mapState.remove(key);
        // mapState.clear();
    }
}
