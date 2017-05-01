package com.zx.future;

/**
 * 返回回去的真实对象
 */
public class RealData implements Data{

    private String result;
    /**
     * 创建真实对象的时候进行查询
     */
    public RealData(String query){
        System.out.println("正在装载对象,查询条件是:" + query);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("装载成功");
        this.result = "result";
    }



    @Override
    public String getResult() {
        return this.result;
    }
}
