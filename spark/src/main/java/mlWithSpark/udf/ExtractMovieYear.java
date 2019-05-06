package mlWithSpark.udf;

import org.apache.spark.sql.api.java.UDF1;

/**
 * 提取电影上映年份的UDF函数
 */
public class ExtractMovieYear implements UDF1<String, Integer> { // 这里居然不用序列化？？原来是UDF1已经继承了 Serializable
    @Override
    public Integer call(String movieDate) throws Exception {
        try {
            int length = movieDate.length();
            String year = movieDate.substring(length - 4, length);
            return Integer.valueOf(year);
        }
        catch (Exception e) {
            return 1900;
        }
    }
}
