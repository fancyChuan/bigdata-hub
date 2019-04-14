package learningSpark.dataReadingAndSaving;

import org.apache.spark.api.java.function.FlatMapFunction;
import org.codehaus.jackson.map.ObjectMapper;

import java.util.ArrayList;
import java.util.Iterator;

public class WriteJson implements FlatMapFunction<Iterator<Student>, String> {
    @Override
    public Iterator<String> call(Iterator<Student> students) throws Exception {
        ArrayList<String> text = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        while (students.hasNext()) {
            Student student = students.next();
            text.add(mapper.writeValueAsString(student));
        }
        return text.iterator();
    }
}
