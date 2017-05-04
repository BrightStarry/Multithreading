package com.zx.disruptor.base;

import com.lmax.disruptor.RingBuffer;

import java.nio.ByteBuffer;

/**
 * 生产者
 *
 * 发布事件最少需要两步：获取下一个事件槽 ，并发布事件（发布的时候一定要使用try/finally保证事件一定会被发布）
 * 如果使用RingBuffer.next()获取一个事件槽，那么一定要发布对应的事件。
 * 尤其是在多个生产者的情况下会导致消费者失速，从而不得不重启应用才能恢复。
 */
public class LongEventProducer {

    //生产完成后存放数据的类
    private final RingBuffer<LongEvent> ringBuffer;
    //构造该对对象的时候初始化这个存放数据的类
    public LongEventProducer(RingBuffer<LongEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    /**
     * 发布时间的方法，每调用一次发布一次事件
     * 它的擦数会通过事件传递给消费者
     * byteBuffer就相当于LongEvent,因为正好是long类型的。所以就用这个NIO包中的
     */
    public void onData(ByteBuffer byteBuffer){
        //1.可以把ringBuffer看做一个事件队列，那么next就是得到下一个事件槽（序列号/下标）
        long sequence = ringBuffer.next();

        try {
            /**
             * 下面这个应该是获取到ringBuffer中下一个序列号对应的空的对象的引用，填充好数据
             * 然后就可以把这个序列号对应的数据对象使用publish()发布了
             */
            //2.用上面的索引取出一个空的事件(数据对象)用于填充（获取该序列号对应的事件对象）
            LongEvent event = ringBuffer.get(sequence);
            //3.填充数据对象 因为byteBuffer这个对象存入的索引就是0，所以取出的索引也是0
            event.setValue(byteBuffer.getLong(0));
        } finally {
            //4.发布事件
            //下面的publish方法必须被包含在finally中确保必须得到调用，
            //如果某个请求的sequence未被提交，会造成后续的发布操作堵塞，以及其他的生产者堵塞
            ringBuffer.publish(sequence);
        }
    }

}
