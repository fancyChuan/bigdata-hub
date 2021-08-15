package cn.fancychuan.hbase;

import cn.fancychuan.hbase.tools.ConnectUtil;
import cn.fancychuan.hbase.tools.DataUtil;
import cn.fancychuan.hbase.tools.NameSpaceUtil;
import cn.fancychuan.hbase.tools.TableUtil;
import org.apache.hadoop.hbase.client.Connection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class HBaseApiApp {
    private Connection connection;
    @Before
    public void init() throws IOException {
        connection = ConnectUtil.getConnection();
    }
    @After
    public void close() throws IOException {
        ConnectUtil.close(connection);
    }

    /**
     * 测试两种创建Connection对象的方法
     * 在客户端如果实例化两次，会创建两个Connection对象出来，Connection的创建不是单例的
     */
    @Test
    public void testConnection() throws IOException {
        System.out.println(ConnectUtil.getConnection());
        System.out.println(ConnectUtil.getConnection());
        System.out.println(ConnectUtil.getConnection2());
        System.out.println(ConnectUtil.getConnection2());

    }

    // ================== 测试命名空间的相关操作 ==================
    @Test
    public void testListNameSpace() throws IOException {
        System.out.println(NameSpaceUtil.listNameSpace(connection));
    }
    @Test
    public void testifNameSpaceExists() throws IOException {
        System.out.println(NameSpaceUtil.ifNameSpaceExists(connection, ""));
        System.out.println(NameSpaceUtil.ifNameSpaceExists(connection, "defaultx"));
        System.out.println(NameSpaceUtil.ifNameSpaceExists(connection, "default"));
    }
    @Test
    public void testCreateNameSpace() throws IOException {
        System.out.println(NameSpaceUtil.createNameSpace(connection,"hbase_test"));
        System.out.println(NameSpaceUtil.listNameSpace(connection));
    }

    @Test
    public void testDeleteNameSpace() throws IOException {
        System.out.println(NameSpaceUtil.deleteNameSpace(connection, "hbase_test"));
        System.out.println(NameSpaceUtil.listNameSpace(connection));
    }
    // ================== 测试表的相关操作 ==================
    @Test
    public void testIfTableExist() throws IOException {
        System.out.println(TableUtil.ifTableExist(connection, "student", "default"));
        System.out.println(TableUtil.ifTableExist(connection, "student", "hbase"));
    }
    @Test
    public void testCreateTable() throws IOException {
        System.out.println(TableUtil.createTable(connection, "default", "tablex", "infox"));
        System.out.println(NameSpaceUtil.listTablesByNameSpace(connection, "default"));
    }
    @Test
    public void testDropTable() throws IOException {
        System.out.println(TableUtil.dropTable(connection, "tablex", "default"));
        System.out.println(NameSpaceUtil.listTablesByNameSpace(connection, "default"));
    }

    // ================== 测试数据的相关操作 ==================
    @Test
    public void testPut() throws IOException {
        DataUtil.insertRowData(connection, "default", "tablex", "1001x", "infox", "namex", "hbase");
        DataUtil.insertRowData(connection, "default", "tablex", "1002x", "infox", "namex", "hbase");
        DataUtil.insertRowData(connection, "default", "tablex", "1003x", "infox", "namex", "hbase");
        DataUtil.scanAllRows(connection,"default", "tablex");
    }
    @Test
    public void testGetAllRows() throws IOException {
        DataUtil.scanAllRows(connection,"default", "tablex");
    }
    @Test
    public void testGetRow() throws IOException {
        DataUtil.getRow(connection, "default", "tablex", "1001x");
    }
    @Test
    public void testDeleteRow() throws IOException {
        DataUtil.delete(connection, "default", "tablex", "1003x");
        DataUtil.scanAllRows(connection,"default", "tablex");
    }


}
