package cn.fancychuan;

/**
 * 这个类需要是POJO
 */
public class SensorReading {
    private String id;
    private Long timestamp;
    private Double temperature;

    /**
     * 注意要加上这个默认无参构造器，否则会报以下错误
     * org.apache.flink.api.common.typeutils.CompositeType$InvalidFieldReferenceException: Cannot reference field by field expression on GenericType<cn.fancychuan.SensorReading>Field expressions are only supported on POJO types, tuples, and case classes. (See the Flink documentation on what is considered a POJO.)
     */
    public SensorReading() {
    }

    public SensorReading(String id, Long timestamp, Double temperature) {
        this.id = id;
        this.timestamp = timestamp;
        this.temperature = temperature;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    @Override
    public String toString() {
        return "SensorReading{" +
                "id='" + id + '\'' +
                ", timestamp=" + timestamp +
                ", temperature=" + temperature +
                '}';
    }


}
