package com.zx.disruptor.generator1;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;

import java.util.UUID;

/**
 * 消费者 同时实现了两种 handler接口，其实只要任意一种就可以了
 */
public class TradeHandler implements EventHandler<Trade>,WorkHandler<Trade>{

    @Override
    public void onEvent(Trade trade, long l, boolean b) throws Exception {
        this.onEvent(trade);
    }

    @Override
    public void onEvent(Trade trade) throws Exception {
        //生成个UUID就ok了
        trade.setId(UUID.randomUUID().toString());
        System.out.println(trade.getId());
    }
}
