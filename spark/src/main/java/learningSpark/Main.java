package learningSpark;

public class Main {

    public static void main(String[] args) {
        testWordCount();
    }

    public static void testWordCount() {
        String input = "E:\\JavaWorkshop\\bigdata-learn\\spark\\src\\main\\resources\\testfile.md";
        String output = "E:\\JavaWorkshop\\bigdata-learn\\spark\\out\\wordcount";
        WordCount.main(new String[] {input, output});
    }
}
