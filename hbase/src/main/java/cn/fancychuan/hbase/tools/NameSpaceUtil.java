package cn.fancychuan.hbase.tools;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.NamespaceDescriptor;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;

import java.io.IOException;

/**
 * 命名空间相关操作
 *
 *  1.Admin：提供对hbase管理的一些API
 *      - 通过Connection.getAdmin() 来获取，使用后调用close()关闭
 *
 *  2.Connection：代表客户端和集群的一个连接，这个连接包含对master和对zk的连接
 *      - 可以使用 ConnectionFactory来创建
 *      - Connection的创建时重量级的，建议一个应用只创建一个Connection对象
 *      - Connection是线程安全的，可以在多线程中共享一个Connection实例
 *      - Connection的生命周期是用户自己控制的
 *
 *  3.从Connection对象中可以获取Table和Admin对象实例。这两种对象的创建是轻量级的，但不是线程安全的，因此不建议池化或者缓存
 */
public class NameSpaceUtil {

    public static void main(String[] args) throws IOException {
        Connection connection = ConnectionFactory.createConnection();
        Admin admin = connection.getAdmin();

        // 1.查询所有名称空间
        NamespaceDescriptor[] namespaces = admin.listNamespaceDescriptors();
        for (NamespaceDescriptor namespace : namespaces) {
            System.out.println(namespace.getName());
        }
        // 2.判断库是存在，通过getNamespaceDescriptor来看是否拿得到
        try {
            admin.getNamespaceDescriptor("test");
        } catch (IOException e) {
            System.out.println("命名空间不存在");
        }
        // 3. 创建库
        admin.createNamespace(NamespaceDescriptor.create("test").build());

        // 4. 删除库：只能删除空表，不为空无法删除
        admin.deleteNamespace("test");

        // admin的创建是轻量级的，及时关闭
        admin.close();
        connection.close();
    }
}
