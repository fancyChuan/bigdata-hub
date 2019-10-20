package cn.fancychuan.oozie.apps;

import org.apache.oozie.client.OozieClient;
import org.apache.oozie.client.OozieClientException;

import java.util.Properties;

public class JavaOozieClientApp {
    private OozieClient client = new OozieClient("http://hadoop01:11000/oozie");

    public static void main(String[] args) {

    }

    public void javaApi(String jobId) {
        try {
//            client.resume(jobId);
//            client.kill(jobId);
            OozieClient.Metrics metrics = client.getMetrics();
            System.out.println(metrics.getTimers());
        } catch (OozieClientException e) {
            e.printStackTrace();
        }
    }

    /**
     * 重新提交作业必须具备的属性，分别是oozie.wf.rerun.failnodes和oozie.wf.rerun.skip.nodes。
     * 这两个必须必须存在一个，第一个是自动运行失败的流程节点，第二个是需要跳过的节点
     */
    public void jobKilledRerun(String jobId, Properties conf) {
        Properties properties = client.createConfiguration();
        properties.setProperty("nameNode", "hdfs://192.168.1.133:9000");
        properties.setProperty("queueName", "default");
        properties.setProperty("examplesRoot", "examples");
        properties
                .setProperty("oozie.wf.application.path",
                        "${nameNode}/user/cenyuhai/${examplesRoot}/apps/map-reduce");
        properties.setProperty("outputDir", "map-reduce");
        properties.setProperty("jobTracker", "http://192.168.1.133:9001");
        properties.setProperty("inputDir",
                "/user/cenyuhai/examples/input-data/text");
        properties.setProperty("outputDir",
                "/user/cenyuhai/examples/output-data/map-reduce");
        properties.setProperty("oozie.wf.rerun.failnodes", "true");
        //这两个参数只能选一个，第一个是重新运行失败的节点，第二个是需要跳过的节点
        // properties.setProperty("oozie.wf.rerun.skip.nodes", ":start:");
        try {
            client.reRun(jobId, properties);
        } catch (OozieClientException e) {
            e.printStackTrace();
        }
    }
}
