package apps;

import lineage.TableLineage;
import org.apache.hadoop.hive.ql.parse.ParseException;
import org.apache.hadoop.hive.ql.parse.SemanticException;
import utils.FileUtils;

import java.io.IOException;

public class AnalyseSQL {

    public static void main(String[] args) throws IOException, SemanticException, ParseException {
        TableLineage tableLineage = new TableLineage();

        String sqltext = FileUtils.readFromFile("E:\\JavaWorkshop\\bigdata-learn\\hive\\src\\main\\resources\\sqls.txt");
        String[] sqls = sqltext.split(";");
        for (int i = 1000; i < sqls.length; i++) {
            String sql = sqls[i];
            System.out.println(i + "\tsql:" + sql);
            boolean needToParse = !sql.replace("\r\n", "")
                    .replace("\n", "")
                    .replace("\t", "")
                    .replace(" ", "").isEmpty();
            if (needToParse) {
                tableLineage.getLineageInfo(sql);
                System.out.println("input:" + tableLineage.getInputTableList()
                        + "\toutput:" + tableLineage.getOutputTableList()
                        + "\twith:" + tableLineage.getWithTableList());
            }
        }
    }
}
