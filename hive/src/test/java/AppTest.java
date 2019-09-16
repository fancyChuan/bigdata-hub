import java.util.TreeSet;

public class AppTest {

    public static void main(String[] args) {
        testTreeSet();
    }



    public static void testTreeSet() {
        TreeSet<String> treeSet = new TreeSet<>();
        treeSet.add("a");
        treeSet.add("b");
        treeSet.add("a");
        System.out.println(treeSet);
    }
}
