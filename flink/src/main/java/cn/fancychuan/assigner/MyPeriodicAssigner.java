package cn.fancychuan.assigner;

import cn.fancychuan.SensorReading;
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.watermark.Watermark;

import javax.annotation.Nullable;

/**
 * 自定义周期性生成Watermark
 * 1. 先调用extractTimestamp方法，提取流数据中的时间戳
 * 2. 调用getCurrentWatermark()生成水位，
 *      此时的逻辑是，根据截至到目前所收到的所有数据的最大时间戳，减掉延迟时间60s，作为Watermark的时间戳
 */
public class MyPeriodicAssigner implements AssignerWithPeriodicWatermarks<SensorReading> {
    // 延迟1分钟
    private Long bound = 60 * 1000L;
    // 当前最大时间戳
    private Long maxTs = Long.MIN_VALUE;

    /**
     * 每隔env.getConfig.setAutoWatermarkInterval(100)所设置的时间间隔，周期性的调用这个方法
     * 返回的结果：
     *  1. 如果Watermark大于之前水位的时间戳，则新的Watermark会被插入流中
     *  2. 如果Watermark小于或等于之前的Watermark，则不会产生新的Watermark，保证单调递增
     */
    @Nullable
    @Override
    public Watermark getCurrentWatermark() {
        return new Watermark(maxTs - bound);
    }

    @Override
    public long extractTimestamp(SensorReading element, long recordTimestamp) {
        maxTs = Math.max(maxTs, element.getTimestamp());
        return element.getTimestamp();
    }
}
