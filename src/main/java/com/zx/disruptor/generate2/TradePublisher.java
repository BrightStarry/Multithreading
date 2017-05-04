package com.zx.disruptor.generate2;

import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.dsl.Disruptor;
import com.zx.disruptor.generator1.Trade;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * 生产者
 */
public class TradePublisher implements Runnable {
    private CountDownLatch countDownLatch;
    Disruptor<Trade> disruptor;

    private static int LOOP = 10;//模拟百万次交易的发生

    public TradePublisher(CountDownLatch latch, Disruptor<Trade> disruptor) {
        this.countDownLatch = latch;
        this.disruptor = disruptor;
    }

    @Override
    public void run() {
        TradeEventTranslator tradeEventTranslator = new TradeEventTranslator();
        for (int i = 0; i < LOOP; i++) {
            //发布事件 使用框架提供的api
            disruptor.publishEvent(tradeEventTranslator);
        }
        //释放门闩，让主线程停止等待
        countDownLatch.countDown();

    }
}

/**
 * 框架提供的发布事件（生产者）的API
 */
class TradeEventTranslator implements EventTranslator<Trade>{
    private Random random = new Random();
    @Override
    public void translateTo(Trade trade, long sequence) {
        this.generateTrade(trade);
    }
    private Trade generateTrade(Trade trade){
        trade.setPrice(random.nextDouble() * 900);
        return trade;
    }
}
