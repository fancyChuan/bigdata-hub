package cn.fancychuan.hbase.tools;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.Arrays;

/**
 * 对数据的增删改查，使用Table对象
 *      1. Put：代表对单行数据的put操作
 *      2. hbase操作的都是字节，需要把常用的数据类型转为byte[]，用的是Bytes工具类
 *          - Bytes.toBytes(x) 把x类型的数据转为byte[]
 *          - Bytes.toXxx(y)    把byte[]转为Xxx类型
 *      3. Get：对单行数据的get操作
 *      4. Result：get/scan操作所返回的单行结果集
 *      5. Cell： 代表一个单元格，hbase提供了CellUtil.cloneXxx(cell)来获取列族、列名和值
 *
 */
public class DataUtil {

    public static Table getTable(Connection connection, String name, String nameSpace) throws IOException {
        TableName tableName = TableUtil.checkTableName(name, nameSpace);
        if (tableName == null) {
            return null;
        }
        return connection.getTable(tableName);
    }

    public static void insertRowData(Connection connection, String nameSpace, String tname,
                                     String rowKey, String columnFamily, String column, String value) throws IOException {
        Table table = getTable(connection, tname, nameSpace);
        if (table == null) {
            return;
        }

        Put put = new Put(Bytes.toBytes(rowKey));
        put.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(column), Bytes.toBytes(value));

        table.put(put);
        table.close();
    }

    public static void deleteMultiRow(String tableName, String... rows) {

    }

    // 获取所有数据
    public static void getAllRows(String tableName) throws IOException {
        // 获取用于扫描region的对象
        Scan scan = new Scan();
        TableName tableName1 = TableName.valueOf(tableName);
        Connection connection = ConnectUtil.getConnection();
        Table table = connection.getTable(tableName1);

        ResultScanner resultScanner = table.getScanner(scan);
        for (Result result : resultScanner) {
            for (Cell cell : result.rawCells()) {
                System.out.println("行健rowkey：" + new String(CellUtil.cloneRow(cell)));
                System.out.println("列族： " + new String(CellUtil.cloneFamily(cell)));
                System.out.println("列： " + new String(CellUtil.cloneQualifier(cell)));
                System.out.println("值： " + new String(CellUtil.cloneValue(cell)));
                System.out.println("--------------");
            }
        }
    }

    // 获取某一行数据
    public static Result getRow(Connection connection, String nameSpace, String tname, String rowKey) throws IOException {
        Table table = getTable(connection, tname, nameSpace);
        if (table == null) {
            return null;
        }
        Get get = new Get(Bytes.toBytes(rowKey));
        // 设置查询哪个列族
        // get.addFamily(Bytes.toBytes(columnFamily));
        // 设置查询哪个列
        // get.addColumn(column);
        // 只查某个时间戳的数据
        // get.setTimeStamp(timestamp);
        // 设置返回的version
        // get.setMaxVersions(maxVersion);

        Result result = table.get(get);
        System.out.println(result);
        parseResult(result);
        return result;
    }

    // 获取某一行指定“列族：列”的数据
    public static void getRowQualifier(String tableName, String rowKey, String family, String qualifier) {

    }

    // 将hbase的数据打印查询
    public static void parseResult(Result result) {
        if (result != null) {
            Cell[] cells = result.rawCells();
            for (Cell cell : cells) {
                System.out.println("列族：" + new String(CellUtil.cloneFamily(cell)) +
                        "   列名： " + Bytes.toString(CellUtil.cloneQualifier(cell)) +
                        "   值： " + Bytes.toString(CellUtil.cloneValue(cell)));
            }
        }
    }
}
