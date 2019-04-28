package learningSpark.rddProgram;

import java.io.Serializable;

/**
 * 用于 aggregate() 操作的辅助类
 */
public class AvgCount implements Serializable {
    public int total;
    public int cnt;

    public AvgCount(int total, int cnt) {
        this.total = total;
        this.cnt = cnt;
    }

    public double avg() {
        return total / (double) cnt;
    }
}
