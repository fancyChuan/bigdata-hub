package apps;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * zk客户端
 */
public class ZkClientApp {

    private ZooKeeper zkCli;
    private static final String CONNECT_STRING = "hadoop101:2181,hadoop102:2181,hadoop103:2181";
    private static final int SESSION_TIMEOUT = 2000;

    /**
     * 初始化一个zk的客户端，并实例化一个监听器
     *  1. 监听器里面可以写用户所需业务逻辑，客户端收到事件通知后，就会调用监听器的process，也就是回调函数
     *  2. 意味着，如果有多重业务逻辑，那么就需要有多个监听器，对应的需要多个客户端
     */
    @Before
    public void init() throws IOException {
        zkCli = new ZooKeeper(CONNECT_STRING, SESSION_TIMEOUT,
                new Watcher() {
                    @Override
                    public void process(WatchedEvent event) {
                        // 收到事件通知后的回调函数（用户的业务逻辑）
                        System.out.println(event.getType() + " -- " + event.getPath());

                        // 再次启动监听
                        try {
                            zkCli.getChildren("/", true);
                        } catch (KeeperException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
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

    /**
     * 创建子节点
     *  zkCli.create()
     *  参数1：要创建的节点的路径； 参数2：节点数据 ； 参数3：节点权限 ；参数4：节点的类型
     */
    @Test
    public void create() throws KeeperException, InterruptedException {
        // ACL访问控制列表：可以控制那个IP那个节点可以访问
        String result = zkCli.create("/forlearn/test", "from idea".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        System.out.println(result);
    }

    /**
     * 获取子节点的数据内容，并通过true开启监听该节点的变化
     */
    @Test
    public void get() throws KeeperException, InterruptedException {
        byte[] data = zkCli.getData("/forlearn/test", true, new Stat());
        zkCli.getChildren("/forlearn/test", true);
        String s = new String(data);
        System.out.println(s);
    }

    @Test
    public void set() throws KeeperException, InterruptedException {
        Stat stat = zkCli.setData("/forlearn/test", "just do".getBytes(), 0); // 指定要修改的数据的版本
        System.out.println(stat.getVersion());
        System.out.println(stat.getAversion());
    }

    @Test
    public void statAndDelete() throws KeeperException, InterruptedException {
        Stat exists = zkCli.exists("/forlearn/test", false);
        if (exists == null) {
            System.out.println("节点不存在");
        } else {
            System.out.println(exists.getDataLength());
            zkCli.delete("/forlearn/test", exists.getVersion());
            System.out.println("节点已经删除");
        }
    }

    /**
     * 实现一个循环注册的功能，也就是注册的节点发生变化的时候，重新注册
     */
    public void register() throws KeeperException, InterruptedException {
        byte[] data = zkCli.getData("/forlearn", new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                try {
                    register();
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, null);
        System.out.println(new String(data));
    }

    @Test
    public void testRegister() {
        try {
            register();
            Thread.sleep(Long.MAX_VALUE);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
