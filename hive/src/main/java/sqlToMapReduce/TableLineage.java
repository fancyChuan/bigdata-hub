package sqlToMapReduce;

import org.apache.hadoop.hive.ql.lib.*;
import org.apache.hadoop.hive.ql.parse.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Stack;
import java.util.TreeSet;

/**
 * TODO：未调通
 */
public class TableLineage implements NodeProcessor {
    TreeSet<String> inputTableList = new TreeSet<String>();
    TreeSet<String> outputTableList = new TreeSet<String>();
    TreeSet<String> withTableList = new TreeSet<String>();

    public TreeSet<String> getInputTableList() {
        return inputTableList;
    }

    public TreeSet<String> getOutputTableList() {
        return outputTableList;
    }

    public TreeSet<String> getWithTableList() {
        return withTableList;
    }

    public Object process(Node node, Stack<Node> stack, NodeProcessorCtx nodeProcessorCtx, Object... objects) throws SemanticException {
        ASTNode astNode = (ASTNode) node;
        switch (astNode.getToken().getType()) {
            // create table
            case HiveParser.TOK_CREATETABLE: {
                String createName = BaseSemanticAnalyzer.getUnescapedName((ASTNode) astNode.getChild(0));
                outputTableList.add(createName);
                break;
            }
            // insert table
            case HiveParser.TOK_TAB: {
                String insertName = BaseSemanticAnalyzer.getUnescapedName((ASTNode) astNode.getChild(0));
                outputTableList.add(insertName);
                break;
            }
            // from table
            case HiveParser.TOK_TABREF: {
                ASTNode tabTree = (ASTNode) astNode.getChild(0);
                String fromName = tabTree.getChildCount() == 1 ? BaseSemanticAnalyzer.getUnescapedName((ASTNode) tabTree.getChild(0)) :
                        BaseSemanticAnalyzer.getUnescapedName((ASTNode) tabTree.getChild(0)) + "." +
                                BaseSemanticAnalyzer.getUnescapedName((ASTNode) tabTree.getChild(1));
                inputTableList.add(fromName);
                break;
            }
            // with 语句
            case HiveParser.TOK_CTE: {
                for (int i = 0; i < astNode.getChildCount(); i++) {
                    ASTNode tmp = (ASTNode) astNode.getChild(i);
                    String withName = BaseSemanticAnalyzer.getUnescapedName((ASTNode) tmp.getChild(0));
                    withTableList.add(withName);
                    break;
                }
            }
        }
        return null;
    }

    public void getLineageInfo(String query) throws ParseException, SemanticException {
        ParseDriver parseDriver = new ParseDriver();
        ASTNode tree = parseDriver.parse(query);

        while (tree.getToken() == null && tree.getChildCount() > 0) {
            tree = (ASTNode) tree.getChild(0);
        }
        inputTableList.clear();
        outputTableList.clear();
        withTableList.clear();

        LinkedHashMap<Rule, NodeProcessor> rules = new LinkedHashMap<Rule, NodeProcessor>();

        Dispatcher ruleDispatcher = new DefaultRuleDispatcher(this, rules, null);
        GraphWalker graphWalker = new DefaultGraphWalker(ruleDispatcher);

        ArrayList topNodes = new ArrayList();
        topNodes.add(tree);
        graphWalker.startWalking(topNodes, null);
    }

    public static void main(String[] args) throws SemanticException, ParseException {
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

        // ParseDriver.HiveLexerX lexerX = new ParseDriver.HiveLexerX(new ExpressionTree.ANTLRNoCaseStringStream(sql));

        TableLineage tableLineage = new TableLineage();
        tableLineage.getLineageInfo(sql);

        System.out.println("input:" + tableLineage.getInputTableList());
        System.out.println("output:" + tableLineage.getOutputTableList());
        System.out.println("with:" + tableLineage.getWithTableList());

    }
}
