package cn.fancychuan.hbase.tools;

import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;

import java.io.IOException;

public class ConnectUtil {

    public static Connection getConnection() throws IOException {
        Connection connection = ConnectionFactory.createConnection();
        return connection;
    }
}
