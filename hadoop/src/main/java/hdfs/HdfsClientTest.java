package hdfs;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class HdfsClientTest {
    private FileSystem fs;
    private String localFile = "E:\\JavaWorkshop\\bigdata-learn\\hadoop\\Hadoop原理实践.md";
    private Path apiTestHome = new Path("/user/beifeng/fsApi");
    private Path subDirPath = new Path("/user/beifeng/fsApi/sub/sub2/sub3");
    private Path userHome = new Path("/user/beifeng");
    @Before
    public void before() throws URISyntaxException, IOException, InterruptedException {
        Configuration conf = new Configuration();
        // conf.set("fs.defaultFS", "hdfs://s01:8020"); // 这里配置的话，优先级比配置文件里的高
        this.fs = FileSystem.get(new URI("hdfs://s01:8020"), conf, "beifeng"); // 不指定用户的时候就默认为执行这个程序的用户
        System.out.println(fs);
        fs.delete(apiTestHome);
    }

    @Test
    public void fsApi() throws IOException {
        // 当前登录用户的主目录 比如 hdfs://s01:8020/user/beifeng
        fs.getHomeDirectory();
        // 创建目录，目录存在也返回true，但不会清空目录再重建。注意相当于mkdir -p 可以创建多级目录
        boolean mkdirs = fs.mkdirs(subDirPath);
        if (mkdirs) System.out.println("创建目录成功");
        // 上传文件，可以直接重命名
        fs.copyFromLocalFile(new Path(localFile), new Path("/user/beifeng/fsApi/fromLocal.md"));
        // 新建一个文件，并追加写入，注意追加的时候文件需要存在，否则报错
        Path myPath = new Path(apiTestHome, "read-write-file.txt");
        fs.createNewFile(myPath);
        FSDataOutputStream append = fs.append(myPath, 1024);
        IOUtils.copyBytes(new FileInputStream(localFile), append, 1024, true);
        // 查看目录下的文件内容，第二个参数表示是否递归显示。注意这个方法不会显示目录
        RemoteIterator<LocatedFileStatus> listFiles = fs.listFiles(apiTestHome, false);
        System.out.println("========== " + apiTestHome + " ===========");
        while (listFiles.hasNext()) {
            LocatedFileStatus next = listFiles.next();
            System.out.println(next + "\n块信息为：");
            BlockLocation[] blockLocations = next.getBlockLocations();
            for (BlockLocation blockLocation : blockLocations) {
                System.out.print("host: " + StringUtils.join(blockLocation.getHosts(), ", "));
                System.out.println("\toffset: " + blockLocation.getOffset());
            }
        }
        // 这个方法可以查看到目录
        FileStatus[] listStatus = fs.listStatus(apiTestHome);
        System.out.println("========== " + apiTestHome + " ===========");
        for (FileStatus fileStatus : listStatus) {
            System.out.println(fileStatus);
            System.out.println("文件信息为：是否目录" + fileStatus.isDirectory() + "\t长度文件" + fileStatus.getLen());
        }

        // 递归删除，如果为False，那么目录有文件的时候就会报错。注意这个删除是完成删除，并不会把文件放回垃圾回收站
        // fs.delete(apiTestHome, true);
    }

    @After
    public void after() throws IOException {
        fs.close();
        System.out.println("\ndone~");
    }
}
