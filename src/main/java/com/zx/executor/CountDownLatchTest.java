package com.zx.executor;

import java.util.concurrent.CountDownLatch;

/**
    计数器类
    确保一组线程能够阻塞自己，使一组线程相互等待，同时完成任务。
 */
public class CountDownLatchTest {
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(2);
        new Thread(new TestTask("任务1",3000,latch)).start();
        new Thread(new TestTask("任务2",10000,latch)).start();
        //等待所有线程完成
        latch.await();
        System.out.println("END");
    }

}

class TestTask implements Runnable{
    String name;
    long time;
    CountDownLatch latch;

    public TestTask(String name, long time, CountDownLatch latch){
        this.name = name;
        this.time = time;
        this.latch = latch;

    }

    @Override
    public void run() {
        System.out.println(name + "开始工作");
        latch.countDown();
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(name + "工作结束");

    }
}
