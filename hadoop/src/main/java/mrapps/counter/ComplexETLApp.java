package mrapps.counter;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class ComplexETLApp {
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        args = new String [] {"hadoop/input/weblog.txt", "hadoop/target/counter-complex"};

        Job job = Job.getInstance(new Configuration());

        job.setJarByClass(ComplexETLApp.class);
        job.setMapperClass(ETLMapper.class);
        job.setNumReduceTasks(0);

        FileInputFormat.setInputPaths(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        job.waitForCompletion(true);
    }

    static class ETLMapper extends Mapper<LongWritable, Text, Text, NullWritable> {
        private LogBean bean = new LogBean();
        private Text k = new Text();

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString();

            parseLog(line);

            if (bean.isValid()) {
                context.write(value, NullWritable.get());
                context.getCounter("ETL", "True").increment(1);
            } else {
                context.getCounter("ETL", "False").increment(1);
            }
        }
        
        private void parseLog(String line) {
            String[] fields = line.split(" ");

            if (fields.length > 11) {
                bean.setRemote_addr(fields[0]);
                bean.setRemote_user(fields[1]);
                bean.setTime_local(fields[3].substring(1));
                bean.setRequest(fields[6]);
                bean.setStatus(fields[8]);
                bean.setBody_bytes_sent(fields[9]);
                bean.setHttp_referer(fields[10]);

                if (fields.length > 12) {
                    bean.setHttp_user_agent(fields[11] + " "+ fields[12]);
                }else {
                    bean.setHttp_user_agent(fields[11]);
                }

                // 大于400，HTTP错误
                if (Integer.parseInt(bean.getStatus()) >= 400) {
                    bean.setValid(false);
                } else {
                    bean.setValid(true);
                }
            } else {
                bean.setValid(false);
            }
        }
    }
}
