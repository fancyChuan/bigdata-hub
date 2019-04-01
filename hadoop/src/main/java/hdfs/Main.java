package hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;

import java.io.IOException;
import java.net.URI;

public class Main {
    public static void main(String[] args) throws Exception {
        // testCatFile();
        testHdfsFileSystem();
    }

    /**
     * 1. 测试查看HDFS上的文件内容
     */
    public static void testCatFile() throws Exception {
        String uri = "/user/beifeng/mapreduce/wordcount/input/wc.input";

        System.out.println("====== 使用URL读取 ======");
        URLCat.main(new String[] {"hdfs://s00:8020" + uri});

        System.out.println("====== 使用文件系统API读取 ======");
        FileSystemCat.main(new String[]{uri});
    }


    /**
     * 2. 获取HDFS文件系统的三个静态工厂方法
     */
    public static void testHdfsFileSystem() throws IOException, InterruptedException {
        String path = "/user/beifeng/mapreduce";
        String uri = "hdfs://s00/user/beifeng/mapreduce"; // 提供了URI方案和权限
        Configuration conf = new Configuration();
        // 获取HDFS文件系统的三个方法
        FileSystem fs1 = FileSystem.get(conf); // 返回默认的文件系统
        FileSystem fs2 = FileSystem.get(URI.create(uri), conf); // 返回给定URI方案、权限的文件系统
        FileSystem fs3 = FileSystem.get(URI.create(uri), conf, "beifeng"); // 指定了访问HDFS的用户
        System.out.println("======== working directory =======");
        System.out.println(fs1.getWorkingDirectory());
        System.out.println(fs2.getWorkingDirectory());
        System.out.println(fs3.getWorkingDirectory());
        System.out.println("======== 不提供URI方案时的工作目录 =========");
        System.out.println(FileSystem.get(conf).getWorkingDirectory());
        System.out.println(FileSystem.get(URI.create(path), conf).getWorkingDirectory());
        System.out.println(FileSystem.get(URI.create(path), conf, "beifeng").getWorkingDirectory());
        /* 运行结果如下，可以看出没有提供URI方案时，使用的是默认的hdfs://s00:8020
        ======== working directory =======
        hdfs://s00:8020/user/fancyChuan
        hdfs://s00/user/fancyChuan
        hdfs://s00/user/beifeng
        ======== 不提供URI方案时的工作目录 =========
        hdfs://s00:8020/user/fancyChuan
        hdfs://s00:8020/user/fancyChuan
        hdfs://s00:8020/user/beifeng
        */
    }
}
