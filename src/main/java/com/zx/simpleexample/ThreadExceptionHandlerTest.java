package com.zx.simpleexample;

import java.util.concurrent.*;

/**
 * 线程异常处理测试
 */
public class ThreadExceptionHandlerTest {
    private static ThreadExceptionHandler threadExceptionHandler = new ThreadExceptionHandler();

    public static void main(String[] args) throws InterruptedException {

        /**
         * 这样不通过线程池，直接启动线程是可以使用异常处理类处理异常的
         */
        /*Thread thread = new Thread(()->{
            System.out.println("111");
            int a = 1/0;
        }, "我是线程");
        thread.setUncaughtExceptionHandler(threadExceptionHandler);
        thread.start();
        Thread.sleep(3000);*/


        CustomeThreadPool pool = new CustomeThreadPool(1);

        pool.scheduleAtFixedRate(() -> {
            System.out.println("111");
//            throw new RuntimeException("a");
        }, 0, 1, TimeUnit.SECONDS);

        pool.scheduleAtFixedRate(() -> {
            System.out.println("111");
//            throw new RuntimeException("b");
        }, 0, 1, TimeUnit.SECONDS);


    }
}

/**
 * 线程未捕获异常处理器
 */
class ThreadExceptionHandler implements Thread.UncaughtExceptionHandler {

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        System.out.println(t.getName() + "发生异常，error:" + e.getMessage());
    }
}

/**
 * 自定义定时线程池执行器
 */
class CustomeThreadPool extends ScheduledThreadPoolExecutor {

    public CustomeThreadPool(int corePoolSize) {
        super(corePoolSize);
    }

    public CustomeThreadPool(int corePoolSize, ThreadFactory threadFactory) {
        super(corePoolSize, threadFactory);
    }

    public CustomeThreadPool(int corePoolSize, RejectedExecutionHandler handler) {
        super(corePoolSize, handler);
    }

    public CustomeThreadPool(int corePoolSize, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, threadFactory, handler);
    }

    /**
     * 重写执行完毕的方法
     * 这个方法是每个线程执行完毕都会触发一次
     */
    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        /**
         * 像下面这么get，会把线程内抛出的异常再次抛出
         */
        try {
            ((Future<?>)r).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if (null != t)
            System.out.println(t.getMessage());
    }
}