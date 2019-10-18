package mrapps.fileinputformat;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import java.io.IOException;

/**
 * 自定义InputFormat
 * 需求：
 *  将多个小文件合并成一个SequenceFile文件，Sequence文件是hadoop用来存储二进制形式的key-value对的文件格式，
 *  SequenceFile里面存储着多个文件，存储的形式为文件路径+名称为key，文件内容为value
 *
 * 需求分析：
 *  1. 一个文件不能切成多片，所以需要设置不允许切片
 *  2. 一个RecordReader处理一个文件：把文件直接读成一个KV值
 */
public class SelfFileInputFormat extends FileInputFormat<Text, BytesWritable> {
    @Override
    public RecordReader<Text, BytesWritable> createRecordReader(InputSplit split, TaskAttemptContext context) throws IOException, InterruptedException {
        return new SelfFileRecordReader();
    }

    @Override // 每个文件判断下是否可以切片
    protected boolean isSplitable(JobContext context, Path filename) {
        return false;
    }
}

/**
 * 自定义RecordReader 用来将数据切分为key/value对
 */
class SelfFileRecordReader extends RecordReader<Text, BytesWritable> {
    private boolean notRead = true;
    private Text key = new Text();
    private BytesWritable value = new BytesWritable();
    private FileSplit fileSplit;
    private FSDataInputStream inputStream;

    /**
     * 初始化方法，框架会在开始的时候调用一次
     * @param split the split that defines the range of records to read
     * @param context the information about the task
     */
    @Override
    public void initialize(InputSplit split, TaskAttemptContext context) throws IOException, InterruptedException {
        fileSplit = (FileSplit) split;
        Path path = fileSplit.getPath(); // 通过切片获取路径
        FileSystem fileSystem = path.getFileSystem(context.getConfiguration()); // 获取文件系统，通过上下文context可以获取到配置
        inputStream = fileSystem.open(path);
    }

    @Override
    // 读取下一行kv值，如果否存在返回true，否则是false
    public boolean nextKeyValue() throws IOException, InterruptedException {
        if (notRead) {
            // 具体读文件的过程
            key.set(fileSplit.getPath().toString());
            byte[] buf = new byte[(int) fileSplit.getLength()];
            inputStream.read(buf);
            value.set(buf, 0, buf.length);
            notRead = false;
            return true;
        }
        return false;
    }

    @Override
    // 获取当前读到的key
    public Text getCurrentKey() throws IOException, InterruptedException {
        return key;
    }

    @Override
    // 获得当前读到的value
    public BytesWritable getCurrentValue() throws IOException, InterruptedException {
        return value;
    }

    @Override
    // 当前数据读取的进度
    public float getProgress() throws IOException, InterruptedException {
        return notRead ? 0 : 1;
    }

    @Override
    // 关闭资源
    public void close() throws IOException {
        IOUtils.closeStream(inputStream);
    }
}
