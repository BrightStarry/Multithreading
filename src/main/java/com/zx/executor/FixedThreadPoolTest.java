package com.zx.executor;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *  固定数量的线程池
 */
public class FixedThreadPoolTest {
    private static ExecutorService executorService = Executors.newFixedThreadPool(10);

    public static void main(String [] args) throws Exception {

//        fixedThreadPool();
        test();
    }


    public static void test() throws Exception {
        Future<?> future1 = executorService.submit(new Runnable() {
            @Override
            public void run() {
                System.out.println("aaaaaaaaaaa");
//                try {
//                    throw new NullPointerException("sss");
//                } catch (NullPointerException e) {
//                    e.printStackTrace();
//                }

            }
        });
        Future<?> future2 = executorService.submit(new Runnable() {
            @Override
            public void run() {
                System.out.println("aaaaaaaaaaa");
            }
        });
        System.out.println(future1.isDone());
        System.out.println(future2.isDone());
    }



    public static void fixedThreadPool(){
        AtomicBoolean flag = new AtomicBoolean(false);
        //执行器服务 接口    新建固定数量的连接池


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
//        executorService.execute(new Runnable() {
//            @Override
//            public void run() {
//                for (int i = 0; i < 50; i++) {
//                    System.out.println("跑啊跑10");
//                }
//                flag.compareAndSet(false,true);
//            }
//        });
//        executorService.execute(new Runnable() {
//            @Override
//            public void run() {
//                while(true){
//                    System.out.println("跑啊跑9");
//                }
//            }
//        });

//        while(!flag.get()){
//
//        }
//        executorService.execute(new Runnable() {
//            @Override
//            public void run() {
//                while(true){
//                    System.out.println("----------------");
//                }
//            }
//        });



        executorService.shutdown();
    }
}
