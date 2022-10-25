package com.azhon.basic.lifecycle;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by remilia on 2017/5/10.
 * 仓库分拣接口外层
 */

public class HttpResult<T> {
    @JSONField(name = "ret")
    private int ret;
    @JSONField(name = "msg")
    private String msg;
    @JSONField(name = "data")
    private T data;

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
