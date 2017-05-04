package com.zx.disruptor.multi;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.WorkerPool;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.ProducerType;

/**
 * 主函数
 * 多个生产者和多个消费者
 */
public class Main {
	
	public static void main(String[] args) throws Exception {

		//创建ringBuffer
		RingBuffer<Order> ringBuffer = 
				RingBuffer.create(ProducerType.MULTI, 
						new EventFactory<Order>() {  
				            @Override  
				            public Order newInstance() {  
				                return new Order();  
				            }  
				        }, 
				        1024 * 1024, 
						new YieldingWaitStrategy());

		//平衡生产者和消费者 速度的屏障类
		SequenceBarrier barriers = ringBuffer.newBarrier();

		//消费者数组
		Consumer[] consumers = new Consumer[3];
		//创建消费者 id为 1、2、3
		for(int i = 0; i < consumers.length; i++){
			consumers[i] = new Consumer("c" + i);
		}

		//创建工作池（消费者池），传入消费者数组
		WorkerPool<Order> workerPool = 
				new WorkerPool<Order>(ringBuffer, 
						barriers, 
						new IntEventExceptionHandler(),
						consumers);
		//让生产者获取到消费者目前的索引，和SequenceBarrier一起，平衡消费生产速度。
        ringBuffer.addGatingSequences(workerPool.getWorkerSequences());
        //开启消费者池线程
		//Runtime.getRuntime().availableProcessors()的意思是获取本机cpu可用核心数
        workerPool.start(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));  


        //100个生产者，每个生产者生产100个数据
        final CountDownLatch latch = new CountDownLatch(1);
        for (int i = 0; i < 100; i++) {  
        	final Producer p = new Producer(ringBuffer);
        	new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						latch.await();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					for(int j = 0; j < 100; j ++){
						p.onData(UUID.randomUUID().toString());
					}
				}
			}).start();
        }
        //停2s，让100个生产者线程都跑起来，并且被CountDownLatch阻塞
        Thread.sleep(2000);
        System.out.println("---------------开始生产-----------------");
        //解除阻塞，让100个生产者同时开始生产
        latch.countDown();
        //等待生产消费完成
        Thread.sleep(5000);
        System.out.println("总数:" + consumers[0].getCount() );
	}
	
	static class IntEventExceptionHandler implements ExceptionHandler {  
	    public void handleEventException(Throwable ex, long sequence, Object event) {}  
	    public void handleOnStartException(Throwable ex) {}  
	    public void handleOnShutdownException(Throwable ex) {}  
	} 
}
