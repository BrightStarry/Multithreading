package com.zx.simpleexample;

/**
 * 死锁 例子
 * 两个好友要对对方的拥抱作出同样的回应，都是张开双手，当对方拥抱了自己，再拥抱对方。
 * 当双方同时张开双手，就形成了死锁
 */
public class DeadLockTest {

    static class Friend{
        private String name;
        private int age;
        public  void setName1(String name) {
            synchronized(this.name){
                this.name = name;
            }
        }
        public synchronized void setName2(String name) throws InterruptedException {
                System.out.println("进入setName2");
                this.name = name;
                System.out.println("setName2 修改name success");
            for (int i = 0; i < 15; i++) {
                Thread.sleep(1000);
                System.out.println("setName2等待了1S");
            }
        }
        public synchronized String getName() {
            return name;
        }
        public Friend(String name) {
            this.name = name;
        }
        //张开手，作拥抱动作。
        public  void bow(Friend friend) throws InterruptedException {
            synchronized(this){
                System.out.format("%s: %s" + "  拥抱我！%n",this.name,friend.getName());
                for (int i = 0; i < 10; i++) {
                    Thread.sleep(1000);
                    System.out.format("bow()等待了1s，name:%s %n",this.name);
                }
                System.out.println("bow()执行完毕");
            }
        }
        //回抱对方
        public synchronized void setNameTest(Friend friend) throws InterruptedException {
            friend.setName1("100");
            System.out.println("setName1执行完毕");
            friend.setName2("100");
            System.out.println("setName2执行完毕");


        }
    }
    public static void main(String [] args) throws InterruptedException {
        Friend xiaoHua = new Friend("xiaoHua");
        Friend xiaoMing = new Friend("xiaoMing");

        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    xiaoHua.bow(xiaoMing);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    xiaoMing.setNameTest(xiaoHua);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread1.start();
        thread1.join(2000);
        thread2.start();
    }
}
