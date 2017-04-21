package com.zx.producer;

import java.util.Random;

/**
 * 生产者，放入消息
 * 每隔1-5S放入一条消息，最后放入 done 表示完成
 */
public class Producer implements Runnable{
    private Drop drop;
    public Producer(Drop drop){
        this.drop = drop;
    }

    @Override
    public void run() {
        String [] messages = new String[]{
                "消息1：aaa",
                "消息2：bbb",
                "消息3：ccc",
                "消息4：ddd",
                "消息5：eee"
        };
        Random random = new Random();
        for (int i = 0; i < messages.length; i++) {
            drop.set(messages[i]);
            try {
                Thread.sleep(random.nextInt(5000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        drop.set("done");
    }
}
