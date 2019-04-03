
public class Main {

    public static void main(String[] args) throws Exception {
        // testCreateGroup();
        testJoinGroup();
    }

    /**
     * 1. 创建一个持久性的组
     */
    public static void testCreateGroup() throws Exception {
        String hosts = "s00";
        String znodeName = "fromIDEA";
        CreateGroup.main(new String[] {hosts, znodeName});
    }


    /**
     * 2. 加入组，把每个组成员视为一个程序运行
     */
    public static void testJoinGroup() throws Exception {
        String hosts = "s00";
        String znodeName = "fromIDEA";
        String joinName = "joinIn";
        JoinGroup.main(new String[] {hosts, znodeName, joinName});
    }
}
