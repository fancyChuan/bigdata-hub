package cn.fancychuan.hbase.tools;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.ArrayList;
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

    public static Table getTable(Connection connection, String nameSpace, String name) throws IOException {
        TableName tableName = TableUtil.checkTableName(name, nameSpace);
        if (tableName == null) {
            return null;
        }
        return connection.getTable(tableName);
    }

    /**
     * 使用put来插入数据
     */
    public static void insertRowData(Connection connection, String nameSpace, String tname,
                                     String rowKey, String columnFamily, String column, String value) throws IOException {
        Table table = getTable(connection, nameSpace, tname);
        if (table == null) {
            return;
        }

        Put put = new Put(Bytes.toBytes(rowKey));
        put.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(column), Bytes.toBytes(value));

        table.put(put);
        table.close();
    }

    /**
     * 使用scan来获取所有的数据
     */
    public static void scanAllRows(Connection connection, String nameSpace, String tname) throws IOException {
        Table table = getTable(connection, nameSpace, tname);
        if (table == null) {
            return ;
        }
        // 获取用于扫描region的对象
        Scan scan = new Scan();
        // 设置扫描的起始行
        // scan.setStartRow(startRow);
        // scan.setStartRow()

        // 结果集扫描器
        ResultScanner resultScanner = table.getScanner(scan);
        for (Result result : resultScanner) {
            parseResult(result);
//            for (Cell cell : result.rawCells()) {
//                System.out.println("行健rowkey：" + new String(CellUtil.cloneRow(cell)));
//                System.out.println("列族： " + new String(CellUtil.cloneFamily(cell)));
//                System.out.println("列： " + new String(CellUtil.cloneQualifier(cell)));
//                System.out.println("值： " + new String(CellUtil.cloneValue(cell)));
//                System.out.println("--------------");
//            }
        }
        table.close();
    }

    /**
     * 使用get方法获取某一行数据
     */
    public static Result getRow(Connection connection, String nameSpace, String tname, String rowKey) throws IOException {
        Table table = getTable(connection, nameSpace, tname);
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
        table.close();
        return result;
    }

    // 获取某一行指定“列族：列”的数据
    public static Result getRowQualifier(Connection connection, String nameSpace, String tname,
                                       String rowKey, String columnFamily, String column) throws IOException {
        Table table = getTable(connection, nameSpace, tname);
        if (table == null) {
            return null;
        }
        Get get = new Get(Bytes.toBytes(rowKey));
        get.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(column));
        Result result = table.get(get);
        parseResult(result);
        table.close();

        return result;
    }

    /**
     * 使用delete来删除指定行
     *      delete.addColumn()  删除某个具体列时，具体行为是：为该列的最新cell添加一条type=Delete的标识，
     *                          只能删除最新的一条记录，如果有历史版本的记录，则不会删除
     *      delete.addColumns() 具体行为是：直接新增一条type=DeleteColumn的记录，表示所有版本的记录都会被删除
     */
    public static void delete(Connection connection, String nameSpace, String tname, String rowKey) throws IOException {
        Table table = getTable(connection, nameSpace, tname);
        if (table == null) {
            return ;
        }

        Delete delete = new Delete(Bytes.toBytes(rowKey));
        // 不添加column的时候，表示删除所有列.
        // 指定要删除的列名，只会删除最新version的记录，不会删除所有
        // delete.addColumn(columnFamily, column);
        // 删除指定列的所有版本的记录
        // delete.addColumns(columnFamily, column);
        // 删除整个列族
        // delete.addFamily()

        table.delete(delete);
        table.close();
    }

    /**
     * 使用delete来删除多行数据
     */
    public static void deleteMultiRow(Connection connection, String nameSpace, String tname, String... rowKeys) throws IOException {
        Table table = getTable(connection, nameSpace, tname);
        if (table == null) {
            return ;
        }
        ArrayList<Delete> deleteList = new ArrayList<>();
        for (String rowKey : rowKeys) {
            Delete delete = new Delete(Bytes.toBytes(rowKey));
            deleteList.add(delete);
        }
        table.delete(deleteList);
        table.close();
    }

    // 将hbase的数据打印查询
    public static void parseResult(Result result) {
        if (result != null) {
            Cell[] cells = result.rawCells();
            for (Cell cell : cells) {
                // 两种方式将byte[]转为string
                System.out.println("rowkey： " + new String(CellUtil.cloneRow(cell)) +
                        "   列族：" + new String(CellUtil.cloneFamily(cell)) +
                        "   列名： " + Bytes.toString(CellUtil.cloneQualifier(cell)) +
                        "   值： " + Bytes.toString(CellUtil.cloneValue(cell)));
            }
        }
    }
}
