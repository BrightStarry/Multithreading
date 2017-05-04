package com.zx.disruptor.base;

import com.lmax.disruptor.EventFactory;

/**
 * 元素对象 工厂
 */
public class LongEventFactory implements EventFactory {

    /**
     *直接创建一个LongEvent返回。
     */
    @Override
    public Object newInstance() {
        return new LongEvent();
    }
}
