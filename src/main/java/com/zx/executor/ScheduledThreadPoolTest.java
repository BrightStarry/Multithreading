package com.zx.executor;

import java.util.concurrent.*;

/**
 * 定时线程池
 */
public class ScheduledThreadPoolTest {

    public static  void main(String []args) throws ExecutionException, InterruptedException {
        test2();
    }

    /**
     * 使用Callable接口执行延时方法
     */
    public static void test1() throws ExecutionException, InterruptedException {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(10);
        ScheduledFuture<String> result = scheduledExecutorService.schedule(new Callable<String>() {
            @Override
            public String call() throws Exception {
                System.out.println("执行中");
                return "result";
            }
        }, 5, TimeUnit.SECONDS);
        System.out.println(result.get());
    }

    /**
     * 第一次延时，
     * 而后周期性执行，不受每次任务执行时间影响，到期就执行
     */
    public static void test2(){
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(10);
        ScheduledFuture<?> future = pool.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                while(true)
                    System.out.println(Thread.currentThread().getName() + "执行中..........");

            }
        }, 1000, 1, TimeUnit.MILLISECONDS);
    }
}
