package cn.fancychuan.hbase.tools;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.NamespaceDescriptor;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
 *      也就是说，Table和Admin对象要用的生活再实例化，实例化后直接销毁
 */
public class NameSpaceUtil {

    /**
     * 1.查询所有名称空间
     */
    public static List<String> listNameSpace(Connection connection) throws IOException {
        Admin admin = connection.getAdmin();

        ArrayList<String> nss = new ArrayList<>();
        NamespaceDescriptor[] namespaceDescriptors = admin.listNamespaceDescriptors();
        for (NamespaceDescriptor namespaceDescriptor : namespaceDescriptors) {
            String name = namespaceDescriptor.getName();
            nss.add(name);
        }
        admin.close();
        return nss;
    }

    /**
     * 2. 判断命名空间是否存在
     *      通过getNamespaceDescriptor来看是否拿得到
     */
    public static boolean ifNameSpaceExists(Connection connection, String name) throws IOException {
        // 合法性校验
        if (StringUtils.isBlank(name)) {
            System.out.println("请输入正常的库名");
            return false;
        }
        // 提供一个admin对象
        Admin admin = connection.getAdmin();
        try {
            // 如果找不到会抛异常
            admin.getNamespaceDescriptor(name);
            return true;
        } catch (IOException e) {
            System.out.println("不存在该库名");
        } finally {
            admin.close();
        }
        return false;
    }

    /**
     * 3. 创建库
     */
    public static boolean createNameSpace(Connection connection, String name) throws IOException {
        Admin admin = connection.getAdmin();

        NamespaceDescriptor descriptor = NamespaceDescriptor.create(name).build();
        try {
            admin.createNamespace(descriptor);
            System.out.println("命名空间创建成功");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            admin.close();
        }
        return false;
    }

    /**
     * 4. 删除库：只能删除空表，不为空无法删除
     */
    public static boolean deleteNameSpace(Connection connection, String name) throws IOException {
        // 合法性校验
        if (StringUtils.isBlank(name)) {
            System.out.println("请输入正常的库名");
            return false;
        }
        Admin admin = connection.getAdmin();
        // 先查看该命名空间下是否存在表
        List<String> tablesInNameSpace = getTablesInNameSpace(connection, name);
        if (tablesInNameSpace.size() == 0) {
            admin.deleteNamespace(name);
            System.out.println("删除命名空间成功");
            admin.close();
            return true;
        } else {
            System.out.println(name + "命名空间非空，删除失败");
            admin.close();
            return false;
        }

    }

    // 查询库下有哪些表
    public static List<String> getTablesInNameSpace(Connection conn, String nsName) throws IOException {
        ArrayList<String> tables = new ArrayList<>();
        Admin admin = conn.getAdmin();

        return tables;
    }
}
