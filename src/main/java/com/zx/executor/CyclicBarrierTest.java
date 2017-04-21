package com.zx.executor;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * 屏障类
 * 和CountDownLatch类相似
 * 在一组线程的运行过程中设置屏障（可以设置多个），
 * 当线程到达某个屏障时，需要等待其他线程，等到所有线程都到达屏障，才能继续运行
 */
public class CyclicBarrierTest{


    public static  void main(String[] args){
        Runnable executor1 = new Runnable(){
            @Override
            public void run() {
                System.out.println("屏障1，释放");
            }
        };
        Runnable executor2 = new Runnable(){
            @Override
            public void run() {
                System.out.println("屏障2，释放");
            }
        };

        //定义2个屏障，每个屏障都是拦截两个线程，每次释放屏障的时候，执行相应的线程
        CyclicBarrier barrier1 = new CyclicBarrier(2,executor1);
        CyclicBarrier barrier2 = new CyclicBarrier(2,executor2);
        //新建Runnable
        CyclicBarrierRunnable cyclicBarrierRunnable1 = new CyclicBarrierRunnable(barrier1,barrier2);
        CyclicBarrierRunnable cyclicBarrierRunnable2 = new CyclicBarrierRunnable(barrier1,barrier2);
        //执行
        new Thread(cyclicBarrierRunnable1).start();
        new Thread(cyclicBarrierRunnable2).start();


    }
}

/**
 * 屏障Runnable
 */
class CyclicBarrierRunnable implements Runnable{
    CyclicBarrier cyclicBarrier1 = null;
    CyclicBarrier cyclicBarrier2 = null;
    public CyclicBarrierRunnable(CyclicBarrier cyclicBarrier1, CyclicBarrier cyclicBarrier2){
        this.cyclicBarrier1 = cyclicBarrier1;
        this.cyclicBarrier2 = cyclicBarrier2;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(2000);
            System.out.println(Thread.currentThread() + "执行步骤1");
            cyclicBarrier1.await();

            Thread.sleep(2000);
            System.out.println(Thread.currentThread() + "执行步骤2");
            cyclicBarrier1.await();
            System.out.println(Thread.currentThread().getName() + "done");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
