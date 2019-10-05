package mrapps.flow;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class FlowReducer extends Reducer<Text, FlowBean, Text, FlowBean> {
    FlowBean sumBean = new FlowBean();

    @Override
    protected void reduce(Text key, Iterable<FlowBean> values, Context context) throws IOException, InterruptedException {
        long sumUpFlow = 0;
        long sumDownFlow = 0;
        for (FlowBean flow : values) {
            sumUpFlow += flow.getUpFlow();
            sumDownFlow += flow.getDownFlow();
        }
        sumBean.set(sumUpFlow, sumDownFlow);
        context.write(key, sumBean);
        System.out.println(key + " : " + sumBean);
    }
}
