package cn.fancychuan.shopapp.bean;

/**
 * 用于代替Tuple的POJO类
 */
public class ItemCountBean {
    private Long itemId;
    private Long itemCount;
    private Long windowEnd;

    public ItemCountBean() {
    }

    public ItemCountBean(Long itemId, Long itemCount, Long windowEnd) {
        this.itemId = itemId;
        this.itemCount = itemCount;
        this.windowEnd = windowEnd;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Long getItemCount() {
        return itemCount;
    }

    public void setItemCount(Long itemCount) {
        this.itemCount = itemCount;
    }

    public Long getWindowEnd() {
        return windowEnd;
    }

    public void setWindowEnd(Long windowEnd) {
        this.windowEnd = windowEnd;
    }
}
