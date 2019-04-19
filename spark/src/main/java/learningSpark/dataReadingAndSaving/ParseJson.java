package learningSpark.dataReadingAndSaving;

import common.Student;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.codehaus.jackson.map.ObjectMapper;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * spark读取解析json，使用 Jackson 这个包
 */
public class ParseJson implements FlatMapFunction<Iterator<String>, Student> {
    @Override
    public Iterator<Student> call(Iterator<String> lines) throws Exception {
        ArrayList<Student> people = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        while (lines.hasNext()) {
            String line = lines.next();
            try {
                people.add(mapper.readValue(line, Student.class));
            } catch (Exception e) {
                e.printStackTrace();
                // 跳过解析出错的元素
            }
        }
        return people.iterator();
    }
}
