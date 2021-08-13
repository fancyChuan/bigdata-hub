package cn.fancychuan.hbase.tools;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;

import java.io.IOException;

/**
 * 1. TableName： 代表表名
 *      - valueOf(String 库名, String 表名) 如果库名为空，则使用default作为库名
 * 2. HTableDescriptor：表的描述，还包含表中列族的描述
 * 3. HColumnDescriptor：列族描述
 */
public class TableUtil {
    // 验证表名是否合法并返回
    public static TableName checkTableName(String tableName, String nameSpace) {
        if (StringUtils.isBlank(tableName)) {
            System.out.println("请输入正确的表名！");
            return null;
        }
        return TableName.valueOf(nameSpace, tableName);
    }

    /**
     * 1. 判断表是否存在
     */
    public static boolean ifTableExist(Connection connection, String tableName, String nameSpace) throws IOException {
        Admin admin = connection.getAdmin();
        // 校验表名
        TableName table = checkTableName(tableName, nameSpace);
        if (table == null) {
            return false;
        }
        boolean exist = admin.tableExists(table);
        admin.close();
        return exist;
    }

    /**
     * 2. 创建表
     */
    public static boolean createTable(Connection connection, String nameSpace, String tableName, String... columnFamily) throws IOException {
        // 校验表名
        TableName table = checkTableName(tableName, nameSpace);
        if (table == null) {
            return false;
        }
        // 判断表是否存在
        if (ifTableExist(connection, tableName, nameSpace)) {
            System.out.println("表已经存在，创建失败");
            return false;
        }

        // 至少存入一个列族
        if (columnFamily.length < 1) {
            System.out.println("至少需要指定一个列族");
            return false;
        }

        Admin admin = connection.getAdmin();
        // 创建表的描述
        HTableDescriptor hTableDescriptor = new HTableDescriptor(table);
        // 将列族的描述添加到表描述中
        for (String column : columnFamily) {
            HColumnDescriptor hColumnDescriptor = new HColumnDescriptor(column);
            // 可以对列族进行设置
            hColumnDescriptor.setMaxVersions(3);
            hTableDescriptor.addFamily(hColumnDescriptor);
        }
        // 根据表的描述创建表
        admin.createTable(hTableDescriptor);
        admin.close();
        return true;
    }

    /**
     * 3. 删除表
     */
    public static boolean dropTable(Connection connection, String tableName, String nameSpace) throws IOException {
        // 检查表是否存在
        if (!ifTableExist(connection, tableName, nameSpace)) {
            System.out.println("表不存在！");
            return false;
        }
        // 检验并获得TableName对象
        TableName table = checkTableName(tableName, nameSpace);
        if (table == null) {
            return false;
        }

        Admin admin = connection.getAdmin();
        // 先禁用表
        admin.disableTable(table);
        admin.deleteTable(table);
        admin.close();
        return true;
    }





}
