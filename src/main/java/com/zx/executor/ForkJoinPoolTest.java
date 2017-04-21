package com.zx.executor;

import java.util.concurrent.*;

/**
 * 分解、合并线程池
 * 实例
 * 1+2+3+...+10000
 */
public class ForkJoinPoolTest {

    public static void main(String []args) throws ExecutionException, InterruptedException {
        long startA = System.currentTimeMillis();
        int sumA = 0;
        for (int i = 1; i <= 10000; i++) {
            sumA += i;
        }
        long timeA = System.currentTimeMillis() - startA;
        System.out.println("A执行时间：" + timeA + "--结果：" + sumA);



        long startB = System.currentTimeMillis();
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        ForkJoinTask<Integer> result = forkJoinPool.submit(new SimpleTask(1, 10000));

        long timeB = System.currentTimeMillis() - startB;
        forkJoinPool.shutdown();
        System.out.println("B执行时间：" + timeA +"--结果："+ result.get());
    }

}

class SimpleTask extends RecursiveTask<Integer> {
    //当 相加数超过 100个，就分成子任务 也就是每个子任务最多是 100个数相加
    private static int MAX = 5;

    //相加数起始值
    private int start;
    //相加数结束值
    private int end;

    SimpleTask(int start,int end){
        this.start = start;
        this.end = end;
    }

    //RecursiveTask类的重写方法
    //这个类是抽象类，子类必须重写抽象类的抽象方法
    @Override
    protected Integer compute() {
        int sum = 0;
        //如果相加数个数<相加数个数最大值，直接执行任务
        if(end-start <=MAX){

            for (int i = start; i <= end; i++) {
                sum += i;
            }
            System.out.format("本次的start：%s---end:%s--结果:%s%n",start,end,sum);
        }else{
            //超出最大值，分解任务
            System.out.println("-------任务分解--------");
            //取中间值
            int middle = (start + end) / 2;
            //分解成两个小任务
            SimpleTask taskA = new SimpleTask(start, middle);
            SimpleTask taskB = new SimpleTask(middle + 1, end);
            //执行两个小任务
            taskA.fork();
            taskB.fork();
            //将小任务结果相加
            //写法1：
//            Integer resultA = taskA.join();
//            Integer resultB = taskB.join();
//            sum = resultA + resultB;
            //写法2：之前遇到一个问题，运行结果比正确值小很多。现在没事了。莫名其妙的
            sum = taskA.join() + taskB.join();
        }
        return sum;
    }
}
