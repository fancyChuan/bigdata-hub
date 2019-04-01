import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class CreateGroup implements Watcher {

    private static final int SESSION_TIMEOUT = 5000; // 5秒

    private ZooKeeper zk;
    private CountDownLatch connectedSignal = new CountDownLatch(1);

    /**
     * 一个ZooKeeper实例被创建时，会启动一个线程连接到ZooKeeper服务，在使用该实例前需要等待它与ZooKeeper服务之间成功建立连接
     */
    public void connect(String hosts) throws IOException, InterruptedException {
        zk = new ZooKeeper(hosts, SESSION_TIMEOUT, this); // 第三个参数是Watcher对象，该对象接收来自ZooKeeper的回调以获得事件的通知
        connectedSignal.await();
    }

    /**
     * 客户端与ZooKeeper服务建立连接后，watcher的process()方法会被调用
     *
     * 锁存器latch创建时带有一个值为1的计数器，用于表示在它释放所有等待线程之前需要发生的事件数，值变为0的时候，await()方法返回
     */
    @Override
    public void process(WatchedEvent event) { // watcher interface
        if (event.getState() == Event.KeeperState.SyncConnected) { // 是一个连接事件
            connectedSignal.countDown(); // 递减计数器
        }
    }

    /**
     * 创建一个znode，使用zk实例中的create方法
     *
     * ZooDefs.Ids.OPEN_ACL_UNSAFE      完全开放的ACL，允许任何客户端对znode进行读写
     * CreateMode.PERSISTENT            创建znode的类型为持久
     * CreateMode.EPERSISTENT           创建znode的类型为短暂，在客户端断开时ZooKeeper服务会删除znode
     */
    public void create(String groupName) throws KeeperException, InterruptedException {
        String path = "/" + groupName;
        String createdPath = zk.create(path, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        System.out.println("Created " + createdPath);
    }

    public void close() throws InterruptedException {
        zk.close();
    }

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        CreateGroup createGroup = new CreateGroup();
        createGroup.connect(args[0]);
        createGroup.create(args[1]);
        createGroup.close();
    }
}
