package com.zx.disruptor.generator1;

import com.lmax.disruptor.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 主进程2
 *
 * 不通过Disruptor对象进行使用
 * 使用WorkHandler
 */
public class Main2 {

    public static void main(String[] args) throws InterruptedException {
        int BUFFER_SIZE=1024;
        int THREAD_NUMBERS=4;

        EventFactory<Trade> eventFactory = new EventFactory<Trade>() {
            public Trade newInstance() {
                return new Trade();
            }
        };

        RingBuffer<Trade> ringBuffer = RingBuffer.createSingleProducer(eventFactory, BUFFER_SIZE);

        SequenceBarrier sequenceBarrier = ringBuffer.newBarrier();

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_NUMBERS);


        //消费者
        WorkHandler<Trade> handler = new TradeHandler();

        //使用工作池（消费者池）
        //有一个异常处理器
        WorkerPool<Trade> workerPool = new WorkerPool<Trade>(ringBuffer, sequenceBarrier, new IgnoreExceptionHandler(), handler);

        workerPool.start(executor);

        //下面这个生产8个数据
        for(int i=0;i<8;i++){
            long seq=ringBuffer.next();
            ringBuffer.get(seq).setPrice(Math.random()*9999);
            ringBuffer.publish(seq);
        }

        Thread.sleep(1000);
        workerPool.halt();
        executor.shutdown();
    }
}
