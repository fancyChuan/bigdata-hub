import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;

import java.io.IOException;
import java.net.URI;

public class Main {
    public static void main(String[] args) throws Exception {
        testCatFile();

    }

    /**
     * 1. 测试查看HDFS上的文件内容
     */
    public static void testCatFile() throws IOException {
        String uri = "/user/beifeng/mapreduce/wordcount/input/wc.input";
        FileSystemCat.main(new String[]{uri});
    }


}
