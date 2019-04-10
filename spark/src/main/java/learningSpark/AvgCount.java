package learningSpark;

public class AvgCount {
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
