package com.zx.disruptor.multi;

import java.util.concurrent.atomic.AtomicInteger;

import com.lmax.disruptor.WorkHandler;

/**
 * 消费者
 */
public class Consumer implements WorkHandler<Order>{
	//消费者id
	private String consumerId;

	//消费者处理的信息总数 因为是static，所以是所有消费者消费的总数。
	private static AtomicInteger count = new AtomicInteger(0);

	//创建时指定消费者id
	public Consumer(String consumerId){
		this.consumerId = consumerId;
	}

	//消费方法
	@Override
	public void onEvent(Order order) throws Exception {
		System.out.println("当前消费者: " + this.consumerId + "，消费信息：" + order.getId());
		count.incrementAndGet();
	}


	//获取处理数据的总数
	public int getCount(){
		return count.get();
	}

}
