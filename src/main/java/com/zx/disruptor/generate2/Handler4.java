package com.zx.disruptor.generate2;


import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;
import com.zx.disruptor.generator1.Trade;

/**
 * 消费者4
 */
public class Handler4 implements EventHandler<Trade>,WorkHandler<Trade> {
	  
    @Override  
    public void onEvent(Trade event, long sequence, boolean endOfBatch) throws Exception {
        this.onEvent(event);  
    }  
  
    @Override  
    public void onEvent(Trade event) throws Exception {  
    	System.out.println("handler4: get name : " + event.getName());
    	event.setName(event.getName() + "h4");
    }  
}  