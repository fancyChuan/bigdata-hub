package cn.fancychuan.hbase.tools;

import org.apache.hadoop.hbase.NamespaceDescriptor;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;

import java.io.IOException;

/**
 * 命名空间相关操作
 *
 */
public class NameSpaceUtil {

    public static void main(String[] args) throws IOException {
        Connection connection = ConnectionFactory.createConnection();
        Admin admin = connection.getAdmin();

        NamespaceDescriptor[] namespaces = admin.listNamespaceDescriptors();
        for (NamespaceDescriptor namespace : namespaces) {
            System.out.println(namespace);
        }


        connection.close();
    }
}
