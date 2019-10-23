package joinapps.mapjoin;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.*;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class MapJoinApp {
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        args = new String [] {"hadoop/input/forJoin/t_order.txt", "hadoop/target/mapjoin"};

        Job job = Job.getInstance(new Configuration());

        job.setJarByClass(MapJoinApp.class);
        job.setMapperClass(MapJoinMapper.class);
        job.setNumReduceTasks(0); // map join 不需要reduce

        job.addCacheFile(URI.create("file:///E:/JavaWorkshop/bigdata-learn/hadoop/input/forJoin/t_product.txt"));

        FileInputFormat.setInputPaths(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        job.waitForCompletion(true);
    }

    static class MapJoinMapper extends Mapper<LongWritable, Text, Text, NullWritable> {
        private Map<String, String> pMap = new HashMap<>();
        private Text k = new Text();

        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            // 先把缓冲的文件读进来
            URI[] cacheFiles = context.getCacheFiles();
            String path = cacheFiles[0].toString();
            System.out.println(path);
            String path2 = cacheFiles[0].getPath();
            System.out.println(path2);
            // TODO：下面这种开流的方式要注意下。可以设置编码
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(path2), "UTF-8"));
            // 也可以使用hadoop的文件系统，但是无法指定编码
//            FileSystem fileSystem = FileSystem.get(context.getConfiguration());
//            FSDataInputStream open = fileSystem.open(new Path(path2));
//            open.readLine()
            String line;
            while (StringUtils.isNotEmpty(line = bufferedReader.readLine())) {
                String[] fields = line.split("\t");
                pMap.put(fields[0], fields[1]);
            }
            IOUtils.closeStream(bufferedReader);
        }

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String[] items = value.toString().split("\t");
            String panme = pMap.get(items[1]);
            k.set(items[0] + "\t" + panme + "\t" + items[2]);
            context.write(k, NullWritable.get());
        }
    }
}
