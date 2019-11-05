package apps;

import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 监听服务器节点动态上下线。
 * TODO：需要调试
 */
public class WatchServerOnlineOfflineApp {
    public static void main(String[] args) throws Exception {
        DistributeServer server = new DistributeServer();
        DistributeClient client = new DistributeClient();
        // 上线一台服务器
        server.onLine("host1");
        // 启动客户端
        client.clientDo();
        // 5秒后再上线一台服务器
        Thread.sleep(5000);
        server.onLine("host2");
        Thread.sleep(5000);
    }
}

class DistributeServer {
    private static String connectString = "s01:2181,s02:2181,s03:2181";
    private static int sessionTimeout = 2000;
    private ZooKeeper zk = null;
    private String parentNode = "/forlearn/servers";

    // 创建到zk的客户端连接
    public void getConnect() throws IOException{
        zk = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
            }
        });
    }
    // 注册服务器
    public void registServer(String hostname) throws Exception{
        String create = zk.create(parentNode + "/server", hostname.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println(hostname +" is online "+ create);
    }
    // 业务功能
    public void business(String hostname) throws Exception{
        System.out.println(hostname+" is working ...");
        Thread.sleep(Long.MAX_VALUE);
    }
    public void onLine(String hostname) throws Exception {
        // 1获取zk连接
        DistributeServer server = new DistributeServer();
        server.getConnect();
        // 2 利用zk连接注册服务器信息
        server.registServer(hostname);
        // 3 启动业务功能
        server.business(hostname);
    }
}


class DistributeClient {
    private static String connectString = "s01:2181,s02:2181,s03:2181";
    private static int sessionTimeout = 2000;
    private ZooKeeper zk = null;
    private String parentNode = "/forlearn/servers";

    // 创建到zk的客户端连接
    public void getConnect() throws IOException {
        zk = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                // 再次启动监听
                try {
                    getServerList(); // 节点变化时都会执行这个方法
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // 获取服务器列表信息
    public void getServerList() throws Exception {
        // 1获取服务器子节点信息，并且对父节点进行监听
        List<String> children = zk.getChildren(parentNode, true);
        // 2存储服务器信息列表
        ArrayList<String> servers = new ArrayList<>();
        // 3遍历所有节点，获取节点中的主机名称信息
        for (String child : children) {
            byte[] data = zk.getData(parentNode + "/" + child, false, null);
            servers.add(new String(data));
        }
        // 4打印服务器列表信息
        System.out.println(servers);
    }

    // 业务功能
    public void business() throws Exception{
        System.out.println("client is working ...");
        Thread.sleep(Long.MAX_VALUE);
    }

    public void clientDo() throws Exception {
        // 1获取zk连接
        DistributeClient client = new DistributeClient();
        client.getConnect();
        // 2获取servers的子节点信息，从中获取服务器信息列表
        client.getServerList();
        // 3业务进程启动
        client.business();
    }
}

