package cn.fancychuan.assigner;

import cn.fancychuan.SensorReading;
import org.apache.flink.streaming.api.functions.AssignerWithPunctuatedWatermarks;
import org.apache.flink.streaming.api.watermark.Watermark;

import javax.annotation.Nullable;

/**
 * 自定义间断式生成Watermark
 */
public class MyPunctuatedAssigner implements AssignerWithPunctuatedWatermarks<SensorReading> {
    // 延迟1分钟
    private Long bound = 60 * 1000L;

    /**
     * 只给id为sensor_1的流中插入Watermark
     */
    @Nullable
    @Override
    public Watermark checkAndGetNextWatermark(SensorReading lastElement, long extractedTimestamp) {
        if (lastElement.getId().equals("sensor_1")) {
            return new Watermark(extractedTimestamp - bound);
        } else {
            return null;
        }
    }

    @Override
    public long extractTimestamp(SensorReading element, long recordTimestamp) {
        return 0;
    }
}
