package com.zx.simpleexample;

/**
 * 继承 Thread类的 多线程类  不推荐
 */
public class ThreadTest extends Thread{
    @Override
    public void run() {
        while(true){
            System.out.println("继承Thread的多线程类");
        }
    }
    public static void main(String args[]){
        new ThreadTest().start();
    }
}
