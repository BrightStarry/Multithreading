package com.zx.simpleexample;

/**
 * 要求，一本稿子，上面有若干段话，胖子每次讲一段话，每次讲中间隔3s。我让胖子开始讲话，然后开始等胖子讲完，
 * 如果等的时间大于10S，就把胖子杀了，然后胖子临死前要说一句，我还会回来的。23333
 */
public class PangZiTest {
    private static String says[] = {"1：我是智障", "2:我脑子有糠", "3：前几天临安地震，其实是我。。。", "4：我的爷爷叫郑星", "5:我的爷爷有大吊"};
    //打印消息 当前线程名 +　消息
    static void printMessage(String message){
        String threadName = Thread.currentThread().getName();
        System.out.format("%s: %s%n",threadName,message);
    }
    //胖子讲话线程 类
    private static class PangZi implements Runnable {
        @Override
        public void run() {
                for (int i = 0; i < says.length; i++) {
                    printMessage(says[i]);
                    try {
                        Thread.sleep(4000);
                    } catch (InterruptedException e) {
                        printMessage("我还会回来的");
                        return;
                    }
                }
        }
    }
    public static void main(String args[]) throws InterruptedException {
        printMessage("生出一个pangzi");
        long startTime = System.currentTimeMillis();//开始时间
        Thread t = new Thread(new PangZi(),"胖子");
        t.start();
        while (t.isAlive()){
            t.join(1000);
            printMessage("胖子你再说");
            if((System.currentTimeMillis() - startTime) > 11 * 1000 && t.isAlive()){
                printMessage("麻痹的，说的贼慢，滚");
                t.interrupt();
                t.join();
            }
        }
        printMessage("END");
    }

}
