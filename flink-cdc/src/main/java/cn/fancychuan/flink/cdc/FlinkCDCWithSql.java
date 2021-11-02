package cn.fancychuan.flink.cdc;


import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

public class FlinkCDCWithSql {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        //2.创建 Flink-MySQL-CDC的 source
        TableResult tableResult = tableEnv.executeSql("CREATE TABLE flinkcdc (\n" +
                " id INT NOT NULL,\n" +
                " name string,\n" +
                " sex string,\n" +
                "PRIMARY KEY(id) NOT ENFORCED\n" +
                ") WITH (\n" +
                " 'connector' = 'mysql-cdc',\n" +
                " 'hostname' = 'hphost',\n" +
                " 'port' = '3307',\n" +
                " 'username' = 'root',\n" +
                " 'password' = '123456',\n" +
                // " 'scan.startup.mode' = 'latest-offset',\n" +
                " 'scan.startup.mode' = 'initial',\n" +
                " 'database-name' = 'forlearn',\n" +
                " 'table-name' = 'flinkcdc'\n" +
                ")");
        tableEnv.executeSql("select * from flinkcdc").print();

        env.execute();
    }
}
