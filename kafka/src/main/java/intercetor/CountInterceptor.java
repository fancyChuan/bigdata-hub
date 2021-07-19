package intercetor;

import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Map;

/**
 * 计数拦截器：统计多少数据发送成功，多少发送失败
 *  重写onAcknowledgement方法
 */
public class CountInterceptor implements ProducerInterceptor<String, String> {
    private int successCnt = 0;
    private int errorCnt = 0;

    @Override
    public ProducerRecord<String, String> onSend(ProducerRecord<String, String> record) {
        return record;
    }

    @Override
    public void onAcknowledgement(RecordMetadata metadata, Exception exception) {
        if (exception == null) {
            successCnt ++;
        } else {
            errorCnt ++;
        }
    }

    @Override
    public void close() {
        System.out.println("发送成功：" + successCnt + "条数据");
        System.out.println("发送失败：" + errorCnt + "条数据");
    }

    /**
     * 获取配置信息和初始化数据时调用
     */
    @Override
    public void configure(Map<String, ?> configs) {

    }
}
