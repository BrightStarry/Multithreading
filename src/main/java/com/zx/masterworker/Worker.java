package com.zx.masterworker;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 单个子任务 工作者。
 */
public class Worker implements Runnable{

    private ConcurrentLinkedQueue<Task> workerQueue;

    private ConcurrentHashMap<String,Object> resultMap;

    public void setWorkerQueue(ConcurrentLinkedQueue<Task> workerQueue) {
        this.workerQueue = workerQueue;
    }

    public void setResultMap(ConcurrentHashMap<String,Object> resultMap) {
        this.resultMap = resultMap;
    }

    @Override
    public void run() {
        while(true){
            //从任务队列获取任务
            Task task = this.workerQueue.poll();
            //如果任务为空，则队列已经为空，退出循环。
            if(task == null) break;
            //处理任务
            Object result = MyWorker.handle(task);
            //将任务结果存储进结果集合
            this.resultMap.put(Integer.toString(task.getId()),result);
        }
    }




}
