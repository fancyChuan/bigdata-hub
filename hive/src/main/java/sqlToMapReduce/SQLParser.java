package sqlToMapReduce;

import org.apache.hadoop.hive.metastore.parser.ExpressionTree;
import org.apache.hadoop.hive.ql.parse.ParseDriver;

public class SQLParser {

    public static void main(String[] args) {
        String sql = "FROM\n" +
                "( \n" +
                "  SELECT\n" +
                "    p.datekey datekey,\n" +
                "    p.userid userid,\n" +
                "    c.clienttype\n" +
                "  FROM\n" +
                "    detail.usersequence_client c\n" +
                "    JOIN fact.orderpayment p ON p.orderid = c.orderid\n" +
                "    JOIN default.user du ON du.userid = p.userid\n" +
                "  WHERE p.datekey = 20131118 \n" +
                ") base\n" +
                "INSERT OVERWRITE TABLE `test`.`customer_kpi`\n" +
                "SELECT\n" +
                "  base.datekey,\n" +
                "  base.clienttype,\n" +
                "  count(distinct base.userid) buyer_count\n" +
                "GROUP BY base.datekey, base.clienttype";

        ParseDriver.HiveLexerX lexerX = new ParseDriver.HiveLexerX(new ExpressionTree.ANTLRNoCaseStringStream(sql));
    }
}
