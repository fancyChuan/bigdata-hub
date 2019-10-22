package mrapps.outputformat;

import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 自定义
 */
public class MyRecordWriter extends RecordWriter<LongWritable, Text> {
    private FileOutputStream want;
    private FileOutputStream other;

    public void initialize(String outputPath) throws FileNotFoundException {
        want = new FileOutputStream(outputPath + "/want.txt");
        other = new FileOutputStream(outputPath + "/other.txt");
    }

    @Override // 将KV写出，每个KV调用一次
    public void write(LongWritable key, Text value) throws IOException, InterruptedException {
        String out = value.toString() + "\n";
        if (out.contains("baidu")) {
            want.write(out.getBytes());
        } else {
            other.write(out.getBytes());
        }
    }

    @Override
    public void close(TaskAttemptContext context) throws IOException, InterruptedException {
        IOUtils.closeStream(want);
        IOUtils.closeStream(other);
    }
}
