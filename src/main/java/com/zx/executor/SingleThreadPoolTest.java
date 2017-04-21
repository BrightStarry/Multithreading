package com.zx.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 单线程化的线程池 确保所有任务按顺序执行
 */
public class SingleThreadPoolTest {

    public static void main(String[] args){
        singleThreadPool();

    }

    public static void singleThreadPool(){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        //运行一个线程
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println("跑啊跑1");
            }
        });
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println("跑啊跑2");
            }
        });
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println("跑啊跑3");
            }
        });
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println("跑啊跑4");
            }
        });
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println("跑啊跑5");
            }
        });
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println("跑啊跑6");
            }
        });
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println("跑啊跑7");
            }
        });
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println("跑啊跑8");
            }
        });
        executorService.shutdown();
    }
}
