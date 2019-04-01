package hdfs;

import org.apache.hadoop.fs.FsUrlStreamHandlerFactory;
import org.apache.hadoop.io.IOUtils;

import java.io.InputStream;
import java.net.URL;

/**
 * 1. 通过URLStreamHandler实例以标准输出方式显示Hdfs的文件
 */
public class URLCat {

    static {
        URL.setURLStreamHandlerFactory(new FsUrlStreamHandlerFactory());
    }

    public static void main(String[] args) throws Exception {
        InputStream in = null;
        try {
            String url = args[0];
            System.out.println("输入的url为：" + url);
            in = new URL(url).openStream();
            IOUtils.copyBytes(in, System.out, 4096, false); // buffSize设置缓冲区大小， close设置复制结束后是否关闭输入流
        } finally {
            IOUtils.closeStream(in);
        }
    }
}
