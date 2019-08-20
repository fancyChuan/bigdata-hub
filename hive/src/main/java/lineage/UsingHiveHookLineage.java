package lineage;

import org.apache.hadoop.hive.ql.parse.ParseException;
import org.apache.hadoop.hive.ql.parse.SemanticException;
import org.apache.hadoop.hive.ql.tools.LineageInfo;
import utils.FileUtils;

import java.io.IOException;

/**
 * 使用hive自带的LinkageInfo类获取表血缘关系
 *  从源码可知，LineageInfo类只是简单的解析了TOK_TABREF（input表）和TOK_TAB（output表），其实功能还是挺局限的
 *
 *  注意：
 *      org.apache.hadoop.hive.ql.hooks.LineageLogger;
 *      org.apache.hadoop.hive.ql.hooks.LineageInfo;
 *      这两个类是hook包下的类，todo：需要研究下如何不在钩子函数中使用？
 */
public class UsingHiveHookLineage {

    public static void main(String[] args) throws IOException, SemanticException, ParseException {

        String sqltext = FileUtils.readFromFile("E:\\JavaWorkshop\\bigdata-learn\\hive\\src\\main\\resources\\xxx.sql");

        LineageInfo.main(new String[] {sqltext});

    }
}
