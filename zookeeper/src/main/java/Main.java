
public class Main {

    public static void main(String[] args) throws Exception {
        testCreateGroup();
    }

    public static void testCreateGroup() throws Exception {
        String hosts = "s00";
        String znodeName = "fromIDEA";
        CreateGroup.main(new String[] {hosts, znodeName});
    }


    public static void testJoinGroup() throws Exception {

    }
}
