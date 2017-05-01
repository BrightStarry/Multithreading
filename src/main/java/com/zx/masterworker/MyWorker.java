package com.zx.masterworker;

/**
 * 我的工作类 继承  工作类，让每个工作类可以重写handle方法
 */
public class MyWorker extends Worker {

    //处理方法
    public  static Object handle(Task task) {
        Object result = null;
        //处理任务耗时
        try {
            Thread.sleep(500);
            result = task.getPrice();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return result;

    }
}
