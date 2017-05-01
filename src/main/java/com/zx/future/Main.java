package com.zx.future;

/**
 * 主进程
 */
public class Main {

    public static void main(String[] args) {
        //创建
        FutureClient client = new FutureClient();
        //设置参数
        String query = "请求参数";
        //发起请求，返回代理对象
        Data data = client.request(query);
        System.out.println("喝杯茶");
        //获取最终异步的值
        String result = data.getResult();
        System.out.println(result);

    }
}
