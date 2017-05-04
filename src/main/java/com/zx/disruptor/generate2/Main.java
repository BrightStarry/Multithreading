package com.zx.disruptor.generate2;

import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.zx.disruptor.generator1.Trade;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 主函数
 * 复杂的操作
 *
 * 简单场景下可以直接使用RingBuffer
 * 复杂场景下必须使用Disruptor对象
 * 这个小例子我照着那个教学写的，但是一直
 * 有bug，只要使用
 * disruptor.after(h2).handleEventsWith(h5);这样的语句，就会卡死
 * 最后才发现，是
 * ExecutorService executor = Executors.newFixedThreadPool(8);
 * 这个，我原来写的固定线程是4，就卡死了，写成8就可以了。。。
 * 因为如果是4的话。线程不够。。。
 */
public class Main {

    public static void main(String[] args) throws InterruptedException {
        //开始时间
        long beginTime = System.currentTimeMillis();
        //缓存大小
        int bufferSize = 1024;
        //线程池
        ExecutorService executor = Executors.newFixedThreadPool(8);

        Disruptor<Trade> disruptor = new Disruptor<>(new EventFactory<Trade>() {
            @Override
            public Trade newInstance() {
                return new Trade();
            }
        }, bufferSize, executor, ProducerType.SINGLE, new BusySpinWaitStrategy());

        /**
         * P1生产出，给C1、C2消费，C1、C2消费完成后C3消费。
         * 多消费者，单一生产者
         * 使用handler组，一个数据会同时被C1、C2都处理
         */
//        //使用Disruptor创建消费者组 C1、C2
//        EventHandlerGroup<Trade> handlerGroup = disruptor.handleEventsWith(new Handler1(), new  Handler2());
//        //声明在C1、C2完成后执行C3 这里可以传递Handler... 也就是不只一个handler
//        handlerGroup.then(new Handler3());


        /**
         * 顺序执行 C1 -- C2  -- C3
         */
//        disruptor.handleEventsWith(new Handler1())
//                .handleEventsWith(new Handler2())
//                .handleEventsWith(new Handler3());
        /**
         * 先同时进行h1、h2,
         * 执行完h1后执行h4,
         * 执行完h2后执行h5
         * 最后h4、h5完毕后。执行h3
         *
         */
        Handler1 h1 = new Handler1();
        Handler2 h2 = new Handler2();
        Handler3 h3 = new Handler3();
        Handler4 h4 = new Handler4();
        Handler5 h5 = new Handler5();
        disruptor.handleEventsWith(h1, h2);
        disruptor.after(h1).handleEventsWith(h4);
        disruptor.after(h2).handleEventsWith(h5);
        disruptor.after(h4, h5).handleEventsWith(h3);




        //启动
        disruptor.start();
        //门闩，主线程等待生产者准备完毕
        CountDownLatch latch = new CountDownLatch(1);



        //生产者开始生产数据
        executor.submit(new TradePublisher(latch,disruptor));

        //主线程等待生产者完毕
        latch.await();


        //关闭
        disruptor.shutdown();
        executor.shutdown();
        System.out.println("总耗时：" + (System.currentTimeMillis() - beginTime));


    }
}
