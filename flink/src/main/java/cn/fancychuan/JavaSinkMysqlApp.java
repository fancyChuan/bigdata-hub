package cn.fancychuan;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

/**
 * @author fancy
 *
 *  JDBC自定义Sink
 */
public class JavaSinkMysqlApp {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStream<String> inputStream = env.readTextFile("E:\\JavaWorkshop\\bigdata-learn\\flink\\src\\main\\resources\\sensor.txt");
        DataStream<SensorReading> dataStream = inputStream.map(line -> {
            String[] items = line.split(",");
            return new SensorReading(items[0], Long.parseLong(items[1]), new Double(items[2]));
        });

        dataStream.addSink(new SelfJdbcSinkMysql());

        env.execute();
    }

    /**
     * 自定义MySQL连接器，需要继承 RishSinkFunction
     *
     * TODO: 这里并没有使用连接器，意味着每一个流都会创建一次，性能是一般的
     */
    private static class SelfJdbcSinkMysql extends RichSinkFunction<SensorReading> {
        Connection conn = null;
        PreparedStatement insertStmt = null;
        PreparedStatement upsertStmt = null;

        // 初始化，创建mysql连接
        @Override
        public void open(Configuration parameters) throws Exception {
            conn = DriverManager.getConnection("jdbc:mysql://hphost:3306/test", "root", "123456");

            // 创建预编译器
            insertStmt = conn.prepareStatement("insert into sensor_temp(id, temp, up_type) values(?, ?, 'insert')");
            upsertStmt = conn.prepareStatement("update sensor_temp set temp = ?, up_type='update' where id = ?");
        }
        // 正式处理流的地方
        @Override
        public void invoke(SensorReading value, Context context) throws Exception {
            // 可以获取的context信息
//            long currentProcessingTime = context.currentProcessingTime();
//            long currentWatermark = context.currentWatermark();
//            Long timestamp = context.timestamp();

            // 更新表
            upsertStmt.setDouble(1, value.getTemperature());
            upsertStmt.setString(2, value.getId());
            upsertStmt.execute();
            // 如果没有更新成功，那么执行插入语句
            if (upsertStmt.getUpdateCount() == 0) {
                insertStmt.setString(1, value.getId());
                insertStmt.setDouble(2, value.getTemperature());
                insertStmt.execute();
            }
        }

        @Override
        public void close() throws Exception {
            insertStmt.close();
            upsertStmt.close();
            conn.close();
        }
    }
}
