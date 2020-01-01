package cn.fancychuan;


import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.junit.Test;

import java.io.IOException;

public class BaseKnowledgeApp {

    @Test
    public void createJson() throws IOException {
        XContentBuilder builder = XContentFactory.jsonBuilder().startObject();
        builder.field("title", "标题啦");
        builder.field("body", "内容啊");
        builder.endObject();
        String json = builder.bytes().utf8ToString();
        System.out.println(json);
    }
}
