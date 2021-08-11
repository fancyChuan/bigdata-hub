package cn.fancychuan.hbase;

import cn.fancychuan.hbase.tools.ConnectUtil;
import org.junit.Test;

import java.io.IOException;

public class HBaseApiApp {

    @Test
    public void testConnection() throws IOException {
        System.out.println(ConnectUtil.getConnection());
    }
}
