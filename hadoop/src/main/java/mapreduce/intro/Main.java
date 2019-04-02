package mapreduce.intro;

public class Main {

    public static void main(String[] args) throws Exception {
        // testMaxTemperatureOnLocal();
        testMaxTemperatureOnHadoop();
    }

    /**
     * 1. 没有给定资源文件的时候，默认使用本地的Hadoop，而本地的Hadoop并没有任何配置，是单机模式的，文件系统就是本地文件系统
     */
    public static void testMaxTemperatureOnLocal() throws Exception {
        String input = "E:\\javaProject\\bigdata-learn\\hadoop\\input\\ncdc\\sample.txt";
        String[] params = {input, "E:\\javaProject\\bigdata-learn\\hadoop\\out\\mr-max-temperature"};
        MaxTemperature.testMaxTemperature(params);
    }

    /**
     * 2. 给了虚拟机上的Hadoop资源文件，使用的是HDFS文件系统 TODO：还没执行成功
     */
    public static void testMaxTemperatureOnHadoop() throws Exception {
        String input = "/user/beifeng/mapreduce/maxtemperature/input/sample.txt";
        String[] params = {input, "/user/beifeng/mapreduce/maxtemperature/output"};
        MaxTemperature.testMaxTemperature(params);
    }
}
