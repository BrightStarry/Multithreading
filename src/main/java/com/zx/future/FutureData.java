package com.zx.future;

/**
 * future data   返回回去的未来对象(代理对象)
 * 同样使用泛型
 */
public class FutureData implements Data{

    private RealData realData;

    //对象是否是空的
    private boolean isEmpty = true;

    public synchronized void setRealData(RealData realData){
        //如果不是空的，不执行
        if(!isEmpty){
            return;
        }
        //如果是空的，装载真实对象对象
        this.realData = realData;
        isEmpty = false;
        notify();
    }



    @Override
    public synchronized String getResult() {
        //如果没装载好，一直等待
        while(isEmpty){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return this.realData.getResult();
    }
}
