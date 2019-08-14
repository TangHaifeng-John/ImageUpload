package com.haifeng.example.iamgeupload.message;


/**
 * Toast信息类，EventBus会使用到
 */
public class ToastMessage {


    private String data;
    /**
     * 可以保存一个int数据类型的数据
     */
    private  int time;




    public ToastMessage(String data) {
        this.data = data;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }



    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
