package com.zx.simpleexample;

/**
 * Created by 97038 on 2017-04-17.
 */
public class InheritableThreadLocalTest implements Runnable {
    private InheritableThreadLocal<Integer> i;

    public InheritableThreadLocalTest(InheritableThreadLocal<Integer> i){
        this.i = i;
    }
    @Override
    public void run() {
        for (int i = 0; i < 5; i++) {
            System.out.format("我是线程：%s,当前的ThreadLocal:%s %n",Thread.currentThread().getName(),this.i.get());
            if(Thread.currentThread().getName().equals("A"))
                this.i.set(100 + i);
            if(Thread.currentThread().getName().equals("B"))
                this.i.set(100 - i);
        }
    }

    public static void main(String [] args){
//        InheritableThreadLocal<String> str = new InheritableThreadLocal<String>(){
//            @Override
//            protected String initialValue() {
//                return "init";
//            }
//        };
        InheritableThreadLocal<Integer> i = new InheritableThreadLocal<Integer>();
        i.set(100);
        new Thread(new InheritableThreadLocalTest(i),"A").start();
        new Thread(new InheritableThreadLocalTest(i),"B").start();

    }
}
