package cn.fancychuan.flume;

import org.apache.flume.*;
import org.apache.flume.conf.Configurable;
import org.apache.flume.sink.AbstractSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MySink extends AbstractSink implements Configurable {
    // 创建Logger对象
    private static final Logger LOG = LoggerFactory.getLogger(AbstractSink.class);

    private String prefix;
    private String suffix;

    @Override
    public Status process() throws EventDeliveryException {
        Status status;

        Channel channel = getChannel();
        Transaction transaction = channel.getTransaction();
        Event event;
        // 开启事务
        transaction.begin();
        while (true) {
            event = channel.take();
            if (event != null) {
                break;
            }
        }
        try {
            LOG.info(prefix + new String(event.getBody()) + suffix);
            transaction.commit();
            status = Status.READY;
        } catch (Exception e) {
            transaction.rollback();
            status = Status.BACKOFF;
        } finally {
            transaction.close();
        }

        return status;
    }

    @Override
    public void configure(Context context) {
        //读取配置文件内容，有默认值
        prefix = context.getString("prefix", "hello:");
        //读取配置文件内容，无默认值
        suffix = context.getString("suffix");

    }
}
