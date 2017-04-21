package com.zx.producer;

/**
 * 消息中间类
 * 类似 阻塞队列  但这个类中，消息只能放入一个，
 * 只有消息被取走，才可以再次放入，只有消息存在，才可以取走，否则都被阻塞
 */
public class Drop {

    private String message;//消息
    private boolean empty = true;//是否不存在 true:不存在 false：存在

    /**
     * 获取消息
     */
    public synchronized String get(){
        //如果是不存在的,true，就一直循环，等消息被放入，再取走
        while(empty){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        empty = true;//设为空，因为消息被获取了
        notifyAll();//唤醒其他所有线程，告诉他们，消息被获取了。
        return message;
    }

    /**
     * 放入消息
     */
    public synchronized void set(String message){
        //如果消息存在，false,就一直循环，不放入消息，等消息被取走，再放入
        while(!empty){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        empty = false;//设为存在，因为消息已经被放入
        this.message = message;
        notifyAll();
    }

    public static void main(String[] agrs){
        Drop drop = new Drop();
        new Thread(new Producer(drop)).start();
        new Thread(new Customer(drop)).start();

    }

}
