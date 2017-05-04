package com.zx.disruptor.base;

import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.RingBuffer;

import java.nio.ByteBuffer;

/**
 * 生产者，
 * 使用Disruptor提供的api发布
 * lambda式的api
 */
public class LongEventProducerWithTranslator {
    //这个Translator可以看做一个事件初始化器，publicEvent方法会调用它，填充Event
    private static final EventTranslatorOneArg<LongEvent, ByteBuffer> TRANSLATOR =
            new EventTranslatorOneArg<LongEvent, ByteBuffer>() {
                @Override
                public void translateTo(LongEvent longEvent, long l, ByteBuffer byteBuffer) {
                    longEvent.setValue(byteBuffer.getLong(0));
                }
            };

    private final RingBuffer<LongEvent> ringBuffer;
    public LongEventProducerWithTranslator(RingBuffer<LongEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    //发布
    public void onData(ByteBuffer byteBuffer){
        //使用写好的TRANSLATOR发布
        ringBuffer.publishEvent(TRANSLATOR,byteBuffer);
    }
}
