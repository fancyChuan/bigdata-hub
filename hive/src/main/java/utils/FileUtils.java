package utils;

import java.io.FileInputStream;
import java.io.IOException;

public class FileUtils {

    public static String readFromFile(String path) throws IOException {
        String sqls = "";

        int hasRead = 0;
        byte[] bytes = new byte[1024];
        FileInputStream inputStream = new FileInputStream(path);
        while ((hasRead = inputStream.read(bytes))>0) {
            String part = new String(bytes, 0, hasRead);
            sqls = sqls + part;
        }
        return sqls;
    }
}
