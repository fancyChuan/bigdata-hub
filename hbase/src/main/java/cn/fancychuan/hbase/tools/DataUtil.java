package cn.fancychuan.hbase.tools;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

public class DataUtil {

    public static void insertRowData(String tableName, String rowKey, String columnFamily, String column, String value) {
        Put put = new Put(Bytes.toBytes(rowKey));
        put.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(column), Bytes.toBytes(value));


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
    public static void getRow(Connection conn, String tableName, String rowKey) {

    }

    // 获取某一行指定“列族：列”的数据
    public static void getRowQualifier(String tableName, String rowKey, String family, String qualifier) {

    }

    public static void main(String[] args) throws IOException {
        getAllRows("student");
    }
}
