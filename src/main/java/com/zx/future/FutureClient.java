package com.zx.future;

/**
 * 客户端
 */
public class FutureClient {

    /**
     * 暂且返回一个空的数据对象，
     * 然后发起一个异步请求，去获取数据
     *
     */
    public Data request(final String query){
        //创建一个空的未来对象，先行返回
        final FutureData futureData = new FutureData();
        //再开启一个线程，发送异步请求，去获取数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                RealData realData = new RealData(query);
                futureData.setRealData(realData);
            }
        }).start();
        //先返回代理data
        return futureData;
    }
}
