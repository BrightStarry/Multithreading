package com.zx.executor;

import java.util.concurrent.Exchanger;

/**
 * 数据交换类
 */
public class ExchangerRunnableTest {
    public static void main(String [] args){
        Exchanger<Object> exchanger = new Exchanger<>();
        Object obj1 = new String("1111");
        Object obj2 = new String("2222");
        ExchangeRunnable runnable1 = new ExchangeRunnable(exchanger,obj1);
        ExchangeRunnable runnable2 = new ExchangeRunnable(exchanger,obj2);
        new Thread(runnable1).start();
        new Thread(runnable2).start();

    }
}

class ExchangeRunnable implements Runnable{
    Exchanger<Object> exchanger;
    Object obj;
    public ExchangeRunnable (Exchanger<Object> exchanger, Object obj){
        this.exchanger = exchanger;
        this.obj = obj;
    }
    @Override
    public void run() {
        try {
            Object obj = this.obj;
            this.obj = this.exchanger.exchange(this.obj);
            System.out.println("原本的obj:" + obj + "--当前的obj:" + this.obj);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
