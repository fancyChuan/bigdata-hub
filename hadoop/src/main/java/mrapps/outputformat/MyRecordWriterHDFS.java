package mrapps.outputformat;

import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;


import java.io.IOException;

/**
 * 自定义RecordWriter 数据写到HDFS上并且能够使用Driver中设置的输出路径
 */
public class MyRecordWriterHDFS extends RecordWriter<LongWritable, Text> {
    private FSDataOutputStream want;
    private FSDataOutputStream other;

    public void initialize(TaskAttemptContext job) throws IOException {
        String outDir = job.getConfiguration().get(FileOutputFormat.OUTDIR);
        FileSystem fileSystem = FileSystem.get(job.getConfiguration());
         want = fileSystem.create(new Path(outDir + "/want.txt"));
         other = fileSystem.create(new Path(outDir + "/other.txt"));
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
