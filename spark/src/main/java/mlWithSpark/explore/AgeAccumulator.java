package mlWithSpark.explore;

import org.apache.spark.util.AccumulatorV2;

import java.util.*;

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
        reset(); // todo: 只能这里调用吗？
    }

    @Override
    public boolean isZero() {
        return false;
    }

    @Override
    public AccumulatorV2<Integer, Map<String, Integer>> copy() {
        return null;
    }

    /**
     * 0值初始化
     */
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

    /**
     * 类似于map阶段，对本地的map对象执行更新操作：根据传入的值判断在哪个key里面，存在再value加1
     */
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

    /**
     * 类似reduce阶段，对map对象合并
     */
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
        SortedMap<String, Integer> sortedMap = new TreeMap<>();
        sortedMap.putAll(map); // 默认按照key排序
        return sortedMap;
    }

    /**
     * 对value进行排序
     * 原理：利用 Collections.sort() 通过自定义排序器实现
     * 条件：Collections.sort()只支持List的排序，因此先把排序的对象转为List
     *
     * 【另外的方法】使用TreeSet，并指定比较器，因为TreeSet只能对key排序，指定的比较器也只能针对key，那么这里我们需要把key改造成 Entry
     */
    public Map<String, Integer> sortedValue(boolean ascending) {
        Set<Map.Entry<String, Integer>> entrySet = map.entrySet();
        // 转为List
        List<Map.Entry<String, Integer>> entryList = new LinkedList<>(entrySet);
        // 自定义比较器
        Comparator<Map.Entry<String, Integer>> comparator = new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> entry1, Map.Entry<String, Integer> entry2) {
                // 升序
                // return entry1.getValue().compareTo(entry2.getValue()); // 实现逻辑为 var0 < var1 ? -1 : (var0 == var1 ? 0 : 1)
                // 换一种排序算法：值相等，则认为前一个比后一个大
                return entry1.getValue() >= entry2.getValue() ? 1 : -1;
            }
        };
        // 两种方法实现对Map的value进行排序
        // 方法1：使用Collections.sort()
        if (ascending) { // 升序
            Collections.sort(entryList, comparator);
            Map<String, Integer> ascMap = new LinkedHashMap<>(); // 使用能够保持put顺序的Map
            for (Map.Entry<String, Integer> entry : entryList) {
                ascMap.put(entry.getKey(), entry.getValue());
            }
            return ascMap;
        }
        // 方法2：直接用TreeMap排序
        else { // 降序
            TreeMap<Map.Entry<String, Integer>, Integer> compTreeMap = new TreeMap<>(comparator.reversed()); // 指定了比较器
            // 把元素传入TreeMap，从而排序
            entrySet.forEach(entry -> compTreeMap.put(entry, entry.getValue()));
            // 把排序后的TreeMap转为需要的格式
            Map<String, Integer> descMap = new LinkedHashMap<>();
            compTreeMap.forEach((entry,value) -> descMap.put(entry.getKey(), value));
            return descMap;
        }
    }
}
