import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;

public class ShowFileStatusTest {

    private MiniDFSCluster cluster; // in-process HDFS cluster for testing
    private FileSystem fs;

    @Before
    public void setUp() throws IOException {
        Configuration conf = new Configuration();
        if (System.getProperty("test.build.data") == null) {
            System.setProperty("test.build.data", "/tmp");
        }
        cluster = new MiniDFSCluster.Builder(conf).build();
        fs = cluster.getFileSystem();
        OutputStream out = fs.create(new Path("/dir/file"));
        out.write("content".getBytes("UTF-8"));
        out.close();
    }

    @After
    public void tearDown() throws IOException {
        if (fs != null) {
            fs.close();
        }
        if (cluster != null) {
            cluster.shutdown();
        }
    }

    @Test
    public void throwStatusForFile() throws IOException {
        fs.getFileStatus(new Path("no-such-file"));
    }

    @Test
    public void fileStatusForFile() throws IOException {
        Path path = new Path("/dir/file");
        FileStatus fileStatus = fs.getFileStatus(path);
        Assert.assertThat(fileStatus.getPath().toUri().getPath(), is("/data/file"));
        Assert.assertThat(fileStatus.isDirectory(), is(false));
        Assert.assertThat(fileStatus.getLen(), is(7L));
        Assert.assertThat(fileStatus.getModificationTime(), is(lessThanOrEqualTo(System.currentTimeMillis())));
        Assert.assertThat(fileStatus.getReplication(), is((short) 1));
        Assert.assertThat(fileStatus.getBlockSize(), is(128*128*1024L));
        Assert.assertThat(fileStatus.getOwner(), is(System.getProperty("user.name")));
        Assert.assertThat(fileStatus.getGroup(), is("supergroup"));
        Assert.assertThat(fileStatus.getPermission().toString(), is("rw-r--r--"));
    }

    @Test
    public void fileStatusForDir() throws IOException {
        Path path = new Path("/dir");
        FileStatus fileStatus = fs.getFileStatus(path);
        Assert.assertThat(fileStatus.getPath().toUri().getPath(), is("/data/dir"));
        Assert.assertThat(fileStatus.isDirectory(), is(true));
        Assert.assertThat(fileStatus.getLen(), is(0L));
        Assert.assertThat(fileStatus.getModificationTime(), is(lessThanOrEqualTo(System.currentTimeMillis())));
        Assert.assertThat(fileStatus.getReplication(), is((short) 0));
        Assert.assertThat(fileStatus.getBlockSize(), is(0L));
        Assert.assertThat(fileStatus.getOwner(), is(System.getProperty("user.name")));
        Assert.assertThat(fileStatus.getGroup(), is("supergroup"));
        Assert.assertThat(fileStatus.getPermission().toString(), is("rwxr-xr-x"));
    }
}
