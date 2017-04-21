package com.zx.executor;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Lock类和Condition类的测试
 */
public class LockAndConditionTest {
    //锁对象
    final Lock lock = new ReentrantLock();
    //写线程条件
    final Condition notFull = lock.newCondition();
    //读线程条件
    final Condition notEmpty = lock.newCondition();
    //缓存队列
    final Object[] items = new Object[100];
    //写索引
    int putptr;
    //读索引
    int takeptr;
    //队列长度
    int count;

    public void put(Object obj) throws InterruptedException {
        lock.lock();//获取锁
        try{
            //如果线程满了
            while(count == items.length){
                notFull.await();//阻塞写线程
            }
            items[putptr] = obj;//赋值
            //如果写入到队列最后一个位置了，那么把写索引置为0
            if(++putptr == items.length)
                putptr = 0;
            ++count;//增加队列长度
            notEmpty.signal();//唤醒读线程
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

    public Object take(){
        lock.lock();
        try{
            //如果队列为空
            while(count == 0){
                notEmpty.await();//阻塞
            }
            Object obj = items[takeptr];//取值
            //如果读索引读到队列最后一个位置了，那么置为0
            if(++takeptr == items.length)
                takeptr = 0;
            --count;//减少队列长度
            notFull.signal();//唤醒写线程
            return obj;
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
        return null;
    }
}
