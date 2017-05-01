package com.zx.masterworker;

import java.util.Random;

/**
 * 主进程
 */
public class Main {

    public static void main(String[] args) {

        System.out.println("本机可以用processor数量:" + Runtime.getRuntime().availableProcessors());
        Master master = new Master(new Worker(), Runtime.getRuntime().availableProcessors());

        //提交给master100个任务
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            Task task = new Task();
            task.setId(i);
            task.setName("任务" + i);
            task.setPrice(random.nextInt(1000));
            master.submit(task);
        }

        //执行任务
        master.execute();

        long startTime =System.currentTimeMillis();

        //等待任务执行完毕
        while(true){
            //如果执行完毕
            if(master.isComplete()){
                long time = System.currentTimeMillis() - startTime;
                int result = master.getResult();
                System.out.println("结果是" + result + ",耗时是" + time);
                break;
            }
        }
    }
}
