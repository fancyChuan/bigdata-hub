package mlWithSpark.explore;

import org.apache.spark.util.AccumulatorV2;

import java.util.HashMap;
import java.util.Map;

/**
 * 20190504 用于统计年龄分布的自定义累加器
 *
 * 使用map来存放需要统计的区间，作为key，计数作为value。比如  12-20表示  12<=value and value < 20
 *
 * todo: 有没有更优雅的写法？
 */
public class AgeAccumulator extends AccumulatorV2<Integer,Map<String,Integer>> {
    private Integer max;
    private Integer min;
    private Integer bins;
    private Map<String, Integer> map = new HashMap<>(); // 存储统计的区间及其计数

    public AgeAccumulator(Integer max, Integer min, Integer bins) {
        this.max = max;
        this.min = min;
        this.bins = bins;
        reset();
    }

    @Override
    public boolean isZero() {
        return false;
    }

    @Override
    public AccumulatorV2<Integer, Map<String, Integer>> copy() {
        return null;
    }

    @Override
    public void reset() {
        Integer start = min;
        Integer step = (max - min) / bins;
        for (int i = 0; i < bins; i++) {
            Integer end = start + step;
            map.put(start + "-" + end, 0);
            start = end;
        }
    }

    @Override
    public void add(Integer v) {
        if (v != null) {
            for (String key : map.keySet()) {
                Integer start = Integer.valueOf(key.split("-")[0]);
                Integer end = Integer.valueOf(key.split("-")[1]);
                if (start <= v && v < end) {
                    map.put(key, map.get(key) + 1);
                }
            }
        }
    }

    @Override
    public void merge(AccumulatorV2<Integer, Map<String, Integer>> other) {
        Map<String, Integer> map2 = other.value();
        for (String key2 : map2.keySet()) {
            if (map.keySet().contains(key2)) {
                map.put(key2, map.get(key2) + map2.get(key2));
            } else {
                map.put(key2, map2.get(key2));
            }
        }
    }

    @Override
    public Map<String, Integer> value() {
        return map;
    }
}
