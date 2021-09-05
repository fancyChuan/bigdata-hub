package cn.fancychuan.shopapp.source;

import cn.fancychuan.shopapp.bean.MarketingUserBehavior;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * 自定义source：模拟生成用户行为
 */
public class GenMarketingUserBehavior implements SourceFunction<MarketingUserBehavior> {
    boolean flag = true;
    private List<String> userBehaviorList = Arrays.asList("DOWNLOAD", "INSTALL", "UPDATE", "UNINSTALL");
    private List<String> channelList = Arrays.asList("HUAWEI", "XIAOMI", "OPPO", "VIVO");

    @Override
    public void run(SourceContext<MarketingUserBehavior> ctx) throws Exception {
        Random random = new Random();
        while (flag) {
            ctx.collect(new MarketingUserBehavior(
                    random.nextLong(),
                    userBehaviorList.get(random.nextInt(userBehaviorList.size())),
                    channelList.get(random.nextInt(channelList.size())),
                    System.currentTimeMillis()
            ));
            Thread.sleep(1000);
        }
    }

    @Override
    public void cancel() {
        flag = false;
    }
}
