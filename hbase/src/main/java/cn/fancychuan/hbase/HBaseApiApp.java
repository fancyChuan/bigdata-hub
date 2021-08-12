package cn.fancychuan.hbase;

import cn.fancychuan.hbase.tools.ConnectUtil;
import org.junit.Test;

import java.io.IOException;

public class HBaseApiApp {

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
}
