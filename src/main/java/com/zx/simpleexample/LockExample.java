package com.zx.simpleexample;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 线程 获取锁 解锁 实例
 *
 */
public class LockExample implements Runnable{



    private Lock lock;
    public LockExample(Lock lock){
        this.lock = lock;
    }
    @Override
    public void run() {
        //尝试获取suo
        lock.lock();
        System.out.println(Thread.currentThread().getName());//获取成功
        try {
            Thread.sleep(3000);//睡眠3s
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        lock.unLock();//解锁
    }

    public static void main(String [] args){
        Lock lock = new Lock();
        new Thread(new LockExample(lock)).start();
        new Thread(new LockExample(lock)).start();
    }


}
//锁 类
class Lock{
    private AtomicBoolean lock = new AtomicBoolean(false);
    //获取锁1
    public boolean lock(){
        //lock.compareAndSet(false,true
        //这句话的意思是，期望把false改成true，只有当前锁为false才能成功，否则返回false
        //!lock.compareAndSet(false,true)
        //那这句话的意思也就是，如果该方法返回的是false，也就是锁一直都是true(被别的线程占用)，那么就一直循环等待
        //结束之后,获取到锁之后，什么都不做，返回false
        while(!lock.compareAndSet(false,true)){
//            System.out.println(Thread.currentThread().getName() + "我在尝试获取锁");
        }
        return false;
    }
    //解锁
    public void unLock(){
        lock.compareAndSet(true,false);
    }
}
