package com.zx.simpleexample;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 实现 Runnable接口 的多线程类
 */
public class RunnableTest implements Runnable {
    private static NumberTest numberTest = new NumberTest();
    private String name;
    @Override
    public void run() {
        while (numberTest.getI() >1) {
            System.out.println("当前线程：" + Thread.currentThread().getName());
            numberTest.decrement();
            numberTest.print();

        }
    }
    public static void main(String args[]) throws InterruptedException {
        Thread thread1 = new Thread(new RunnableTest(),"线程1");
        Thread thread2 = new Thread(new RunnableTest(),"线程2");
        Thread thread3 = new Thread(new RunnableTest(),"线程3");
        thread1.start();
        thread2.start();
        thread3.start();
    }

    public RunnableTest(String name) {
        this.name = name;
    }

    public RunnableTest() {
    }
}

/**
 * 储存值的类
 * 因为之前我之前在实现线程的类中定义了i，然后多个线程i--，发现会有同步问题
 * 即使加上了synchronized也同样存在问题
 * 所以这个类用来测试一下
 */
class NumberTest {
    private AtomicInteger i = new AtomicInteger(100);
    public  void decrement(){
        i.decrementAndGet();
    }
    public  int getI(){
        return i.get();
    }
    public synchronized void print(){
        synchronized(this){
            System.out.println("当前值：" + i.get());
        }
    }
}