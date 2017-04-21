package com.zx.producer;

import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 消费者类
 * 没每隔1-5S读取消息，当读取到消息为 done时停止
 */
public class Customer implements Runnable{
    private Drop drop;
    public Customer(Drop drop){
        this.drop = drop;
    }


    @Override
    public void run() {
        Random random = new Random();

        for(String message = drop.get(); !message.equals("done"); message = drop.get()){
            System.out.format("我读取到的消息是：%s,%n",message);
            try {
                Thread.sleep(random.nextInt(5000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
