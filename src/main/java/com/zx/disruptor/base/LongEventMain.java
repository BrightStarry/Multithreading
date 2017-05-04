package com.zx.disruptor.base;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 主函数
 */
public class LongEventMain {

    public static void main(String[] args) {
        //创建缓存线程池
        ExecutorService executor = Executors.newCachedThreadPool();
        //创建工厂
        LongEventFactory factory = new LongEventFactory();
        //RingBuffer的大小，必须是2的n次方
        int ringBufferSize = 1024 * 1024;

        //创建Disruptor
        //1.工厂对象，用于创建线程
        //2.缓冲区大小
        //3.用于创建线程
        //4.生产者的类型 SINGLE:一个 MULTI：多个
        //5.一种策略：
        /**
         * 目前有三种策略：
         *  BlockingWaitStrategy 是最低效的策略，但是对CPU消耗最小且在不同部署环境中提供更加一致的性能表现
         *  WaitStrategy BLOCKING_WAIT = new BlockingWaitStrategy();
         *  SleepingWaitStrategy 性能和对CPU的消耗和BlockingWaitStrategy差不多，但它对生产者线程影响最小，适合异步日志类.
         *  WaitStrategy SLEEPING_WAIT = new SleepingWaitStrategy();
         *  YieldingWaitStrategy 性能最好，适用于低延迟系统。在要求极高性能且事件处理线程数小雨cpu逻辑核心数的场景中，推荐使用此策略。
         *  WaitStrategy YIELDING_WAIT = new YieldingWaitStrategy();
         */
        Disruptor<LongEvent> disruptor =
                new Disruptor<LongEvent>(factory,ringBufferSize, executor,ProducerType.SINGLE,new YieldingWaitStrategy());

        //连接消费事件方法  LongEventHandler就是消费者
        disruptor.handleEventsWith(new LongEventHandler());

        //启动
        disruptor.start();


        //Disruptor的事件发布过程是一个两阶段提交的过程
        //获取缓存对象,存放数据的地方，环形结构
        RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();

        //创建自定义的生产者
//        LongEventProducer producer = new LongEventProducer(ringBuffer);
        //使用Disruptor提供的生产者 ,相当于简化了OnData()
        LongEventProducerWithTranslator producer = new LongEventProducerWithTranslator(ringBuffer);


        //创建模拟的生产者数据
        //分配给这个对象8个空间
        ByteBuffer byteBuffer = ByteBuffer.allocate(8);
        for (long a = 0; a < 100; a++) {
            //在这个对象的第X个索引（现在都是第0个索引），放入a
            byteBuffer.putLong(0,a);
            //发布
            producer.onData(byteBuffer);
        }

        //关闭disruptor,会阻塞，知道所有事件都得到处理
        disruptor.shutdown();
        //关闭线程对象
        executor.shutdown();
    }
}
