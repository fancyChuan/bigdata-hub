package compress;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.CompressionCodecFactory;
import org.apache.hadoop.io.compress.CompressionInputStream;
import org.apache.hadoop.io.compress.CompressionOutputStream;
import org.apache.hadoop.util.ReflectionUtils;

import java.io.*;

/**
 * 解压缩的使用示例
 */
public class CompressDecompressApp {
    public static void main(String[] args) throws Exception {
        String filename = "hadoop/input/wordcount.txt";
//        String method = "org.apache.hadoop.io.compress.DefaultCodec";
//        String method = "org.apache.hadoop.io.compress.GzipCodec";
        String method = "org.apache.hadoop.io.compress.BZip2Codec";
        String resultFile = compress(filename, method);
        decompress(resultFile);
    }

    private static String compress(String filename, String method) throws Exception {
        FileInputStream fis = new FileInputStream(new File(filename));
        Class<?> codecClass = Class.forName(method);
        CompressionCodec codec = (CompressionCodec) ReflectionUtils.newInstance(codecClass, new Configuration());

        String resultFilename = filename + codec.getDefaultExtension();
        FileOutputStream fos = new FileOutputStream(resultFilename);
        CompressionOutputStream cos = codec.createOutputStream(fos);

        IOUtils.copyBytes(fis, cos, 1024*1024*5, false);
        cos.close();
        fos.close();
        fis.close();
        return resultFilename;
    }

    private static void decompress(String filename) throws IOException {
        // 检查是否可以解压
        CompressionCodecFactory factory = new CompressionCodecFactory(new Configuration());
        CompressionCodec codec = factory.getCodec(new Path(filename));
        if (codec == null) {
            System.out.println("can not find codec for file " + filename);
            return ;
        }
        //
        CompressionInputStream cis = codec.createInputStream(new FileInputStream(filename));
        FileOutputStream fos = new FileOutputStream(filename + ".decoded");
        IOUtils.copyBytes(cis, fos, 1024*1024*5, false);
        cis.close();
        fos.close();
    }
}
