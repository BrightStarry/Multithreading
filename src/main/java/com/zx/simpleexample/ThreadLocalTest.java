package com.zx.simpleexample;

/**
 * 测试  ThreadLocal类
 */
public class ThreadLocalTest implements Runnable{
    private ThreadLocal<String> str;
    public ThreadLocalTest(ThreadLocal<String> str){
        this.str = str;
    }

    @Override
    public void run() {
        for (int i = 0; i < 3; i++) {
            System.out.format("我是线程：%s,当前的ThreadLocal:%s %n",Thread.currentThread().getName(),this.str.get());
            if(Thread.currentThread().getName().equals("A"))
                this.str.set("aa" + i);
            if(Thread.currentThread().getName().equals("B"))
                this.str.set("bb" + i);
        }
    }

    public static void main(String [] args){
        ThreadLocal<String> str = new ThreadLocal<String>(){
            @Override
            protected String initialValue() {
                return "init";
            }
        };
        new Thread(new ThreadLocalTest(str),"A").start();
        new Thread(new ThreadLocalTest(str),"B").start();

    }
}
