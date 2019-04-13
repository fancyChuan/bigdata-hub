package intercetor;

import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Map;

public class CountInterceptor implements ProducerInterceptor<String, String> {
    private int successCnt = 1;
    private int errorCnt = 1;

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

    @Override
    public void configure(Map<String, ?> configs) {

    }
}
