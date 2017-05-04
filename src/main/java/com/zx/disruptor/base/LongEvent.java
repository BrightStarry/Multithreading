package com.zx.disruptor.base;

/**
 * 长整形的元素对象
 * disruptor需要保存的单个数据对象
 */
public class LongEvent {

    private long value;

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }
}
