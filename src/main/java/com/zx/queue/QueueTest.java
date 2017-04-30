package com.zx.queue;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
    阻塞队列测试
 */
public class QueueTest {

}

/**
 *  阻塞队列测试
 *  直接一起测试了。 最终的结果和预期的一样。 如果只是插入和取出，
 *  链表队列（有界）　>  链表队列（无界） >  数组队列
 */
class ArrayBlockingQueueTest{

    public static void main(String[] args) {
        test();
    }
        //数组
    public static ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<String>(200000);
    // 1120 - 1400
//    public static LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<String>(200000);

    public static void test(){
        //生产者线程
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < 200000; i++) {
                        queue.put("a");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    long startTime = System.currentTimeMillis();
                    for (int i = 0; i < 200000; i++) {

                        System.out.println(queue.take());
                    }
                    long endTime = System.currentTimeMillis();
                    System.out.println(endTime - startTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread1.start();
        thread2.start();
    }
}
