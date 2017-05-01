package com.zx.masterworker;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
    管理者
 */
public class Master {

    //1.一个存储所有任务的集合
    private ConcurrentLinkedQueue<Task> workerQueue = new ConcurrentLinkedQueue<>();

    //2.使用HashMap存储所有工作线程对象
    private HashMap<String,Thread> workers = new HashMap<>();

    //3.存储每个worker执行任务结果的集合
    private ConcurrentHashMap<String,Object> resultMap = new ConcurrentHashMap<>();

    //4.构造方法，传入执行子任务的工作类和工作类数量（子线程数量）
    public Master(Worker worker ,int workerCount){

        //将 存储所有任务的集合的引用 给worker
        worker.setWorkerQueue(this.workerQueue);
        //将 存储所有任务结果的集合 给 worker
        worker.setResultMap(this.resultMap);
        for (int i = 0; i < workerCount; i++) {
            //key是每个子线程的名字，value就是每个子线程
            workers.put("worker" + Integer.toString(i),new Thread(worker));
        }
    }

    //5.提交任务的方法
    public void submit(Task task){
        this.workerQueue.add(task);
    }

    //6.启动方法，让所有的worker工作
    public  void execute(){
        for (Map.Entry<String,Thread> map : workers.entrySet()){
            map.getValue().start();
        }
    }

    //7.获取执行结果 是否执行完毕
    public boolean isComplete() {
        for (Map.Entry<String,Thread> map : workers.entrySet()){
            //如果有任何一个任务没有停止
            if(map.getValue().getState() != Thread.State.TERMINATED){
                return false;
            }
        }
        return true;
    }

    //8.返回所有结果集
    public int getResult() {
        int result = 0;
        for (Map.Entry<String,Object> map : resultMap.entrySet()){
            result += (Integer)map.getValue();
        }
        return result;
    }
}
