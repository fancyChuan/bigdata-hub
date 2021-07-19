package cn.fancychuan.flume;

import org.apache.flume.Context;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.PollableSource;
import org.apache.flume.conf.Configurable;
import org.apache.flume.event.SimpleEvent;
import org.apache.flume.source.AbstractSource;

import java.util.HashMap;

/**
 * 需求：使用flume接收数据，并给每条数据添加前缀，输出到控制台。前缀可从flume配置文件中配置
 */
public class MySource extends AbstractSource implements Configurable, PollableSource {
    // 定义配置文件将来要读取的字段
    private Long delay;
    private String field;

    /**
     * 最核心的方法：获取数据封装成event并写入channel，这个方法将被source所在的线程循环调用
     *  Status{READY,BACKOFF}：如果成功封装了event，并发到了channel，那么返回 READY，否则返回BACKOFF
     */
    @Override
    public Status process() throws EventDeliveryException {
        try {
            //创建事件
            SimpleEvent event = new SimpleEvent();
            //创建事件头信息
            HashMap<String, String> hearderMap = new HashMap<>();
            //循环封装事件
            for (int i = 0; i < 10; i++) {
                //给事件设置头信息
                event.setHeaders(hearderMap);
                //给事件设置内容
                if (i % 2 == 0) {
                    event.setBody((i + field).getBytes());
                } else {
                    event.setBody((field + i).getBytes());
                }

                //将事件写入channel
                getChannelProcessor().processEvent(event);
                Thread.sleep(delay);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Status.BACKOFF;
        }
        return Status.READY;

    }

    @Override
    public long getBackOffSleepIncrement() {
        return 0;
    }

    @Override
    public long getMaxBackOffSleepInterval() {
        return 0;
    }

    /**
     * 初始化context（读取配置文件内容）
     * @param context
     */
    @Override
    public void configure(Context context) {
        delay = context.getLong("delay");
        field = context.getString("field", "hello!");
    }
}
