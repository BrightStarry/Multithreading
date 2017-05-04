package com.zx.disruptor.base;


import com.lmax.disruptor.EventHandler;

/**
 * 消费者 LongEvent数据对象处理类
 * disruptor获取到生产者的数据，就交给这个类处理
 */
public class LongEventHandler implements EventHandler<LongEvent> {

    @Override
    public void onEvent(LongEvent longEvent, long l, boolean b) throws Exception {
        //暂时只做一个简单的打印处理
        System.out.println(longEvent.getValue());
    }
}
