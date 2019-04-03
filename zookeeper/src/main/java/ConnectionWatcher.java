import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * 一个辅助类，等待与ZooKeeper建立连接
 */
public class ConnectionWatcher implements Watcher {

    private static final int SESSION_TIMEOUT = 5000;

    protected ZooKeeper zk;
    private CountDownLatch connectedSignal = new CountDownLatch(1);

    public void connect(String hosts) throws IOException, InterruptedException {
        zk = new ZooKeeper(hosts, SESSION_TIMEOUT, this); // 第三个参数是Watcher对象，该对象接收来自ZooKeeper的回调以获得事件的通知
        connectedSignal.await();
    }

    @Override
    public void process(WatchedEvent event) {
        if (event.getState() == Event.KeeperState.SyncConnected) { // 是一个连接事件
            connectedSignal.countDown(); // 递减计数器
        }
    }

    public void close() throws InterruptedException {
        zk.close();
    }
}
