package com.zx.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 自定义的线程池
 */
public class ThreadPoolTest {

    public static void main(String[] args){

    }

    public static void test(){
        int corePoolSize = 5;//核心线程大小
        int maxPoolSize = 10;//最大线程数
        long keepAliveTime = 5000;
        ExecutorService executorService = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                keepAliveTime,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>()
        );
    }
}
