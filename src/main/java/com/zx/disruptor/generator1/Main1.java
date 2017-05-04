package com.zx.disruptor.generator1;

import com.lmax.disruptor.*;

import java.util.concurrent.*;

/**
 * 主进程1
 *
 * 不通过Disruptor对象进行使用
 * 使用EventHandler
 */
public class Main1 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        int BUFFER_SIZE  = 1024;
        int THREAD_NUMBER = 4;
        /**
         * createSingleProducer() 创建一个单个生产者的RingBuffer。
         * 第一个参数叫EventFactory,事件工厂，也就是空数据工厂，生产空数据填充RingBuffer
         * 第二个参数是RingBuffer的大小，必须是2的n次方。目的是为了取模运算提高效率
         * 第三个参数是RingBuffer在生产者没有可用区块的时候（应该是消费者太慢了）的等待策略。
         */
        final RingBuffer<Trade> ringBuffer = RingBuffer.createSingleProducer(new EventFactory<Trade>() {
            @Override
            public Trade newInstance() {
                return new Trade();
            }
        },BUFFER_SIZE,new YieldingWaitStrategy());
        
        //创建线程池
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_NUMBER);

        //创建SequenceBarrier  平衡生产者和消费者的速度
        SequenceBarrier sequenceBarrier = ringBuffer.newBarrier();

        //创建消息处理器 相当于消费者
        BatchEventProcessor<Trade> tradeProcessor = new BatchEventProcessor<>(ringBuffer, sequenceBarrier, new TradeHandler());

        //目的是告诉生产者消费者读取的位置，以平衡速度  如果只有一个消费者可以省略
        ringBuffer.addGatingSequences(tradeProcessor.getSequence());

        //把消息处理器提交到线程池，也就是运行消息处理器
        executor.submit(tradeProcessor);

        //如果有多个消费者，那重复执行上面的三行代码，把TradeHandler换成其他消费者类
        //也就是每个消费者都需要消息处理器，并运行

        //生产者
        Future<Void> future = executor.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                long seq;
                for (int i = 0; i < 10; i++) {
                    seq = ringBuffer.next();
                    ringBuffer.get(seq).setPrice(Math.random() * 9999);
                    ringBuffer.publish(seq);
                }
                return null;
            }
        });

        //等待生产结束
        future.get();
        //再停止，等待消费者处理完成
        Thread.sleep(1000);
        //通知消息处理器停止 同样不是马上结束
        tradeProcessor.halt();
        executor.shutdown();

    }
}
