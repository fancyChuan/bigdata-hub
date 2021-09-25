package cn.fancychuan.state;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.checkpoint.ListCheckpointed;

import java.util.List;

/**
 * 需要实现ListCheckpointed来实现状态编程
 */
public class StateMapFunction implements MapFunction, ListCheckpointed<String> {
    @Override
    public Object map(Object o) throws Exception {
        return null;
    }

    /**
     * 状态快照的保存处理
     */
    @Override
    public List<String> snapshotState(long checkpointId, long timestamp) throws Exception {
        return null;
    }

    /**
     * 恢复状态
     */
    @Override
    public void restoreState(List<String> state) throws Exception {

    }
}
