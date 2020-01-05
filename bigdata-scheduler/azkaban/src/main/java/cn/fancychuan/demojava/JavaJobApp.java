package cn.fancychuan.demojava;

import java.io.FileOutputStream;
import java.io.IOException;

public class JavaJobApp {

    public static void main(String[] args) throws IOException {
        FileOutputStream fos = new FileOutputStream("/usr/local/azkaban/javajob-output.txt");
        fos.write("this is a java progress".getBytes());
        fos.close();

    }
}
