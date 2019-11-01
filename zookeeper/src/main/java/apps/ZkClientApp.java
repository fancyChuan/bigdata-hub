package apps;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class ZkClientApp {

    private ZooKeeper zkCli;
    private static final String CONNECT_STRING = "s01:2181,s02:2181,s03:2181";
    private static final int SESSION_TIMEOUT = 2000;

    @Before
    public void before() throws IOException {
        zkCli = new ZooKeeper(CONNECT_STRING, SESSION_TIMEOUT, (p) -> {
            System.out.println("默认回调函数");
        });
    }

    @Test
    public void ls() throws KeeperException, InterruptedException {
        List<String> children = zkCli.getChildren("/", msg -> {
            System.out.println("自定义的回调函数：" + msg);
        });
        System.out.println("=============");
        for (String child : children) {
            System.out.println(child);
        }
        System.out.println("=============");
        Thread.sleep(Long.MAX_VALUE); // 这里等待，为了跟服务端异步通信，能够接受服务端的通知
    }

    @Test
    public void create() throws KeeperException, InterruptedException { // 访问控制列表，可以控制那个IP那个节点可以访问
        String result = zkCli.create("/forlearn", "from idea".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        System.out.println(result);
    }

    @Test
    public void get() throws KeeperException, InterruptedException {
        byte[] data = zkCli.getData("/forlearn", true, new Stat());
        String s = new String(data);
        System.out.println(s);
    }

    @Test
    public void set() throws KeeperException, InterruptedException {
        Stat stat = zkCli.setData("/forlearn", "just do".getBytes(), 0); // 指定要修改的数据的版本
        System.out.println(stat.getVersion());
        System.out.println(stat.getAversion());
    }

    @Test
    public void statAndDelete() throws KeeperException, InterruptedException {
        Stat exists = zkCli.exists("/forlearn", false);
        if (exists == null) {
            System.out.println("节点不存在");
        } else {
            System.out.println(exists.getDataLength());
            zkCli.delete("/forlearn", exists.getVersion());
            System.out.println("节点已经删除");
        }
    }

}
