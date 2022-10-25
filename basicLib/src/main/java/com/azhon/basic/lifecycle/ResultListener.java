package com.azhon.basic.lifecycle;

public interface ResultListener<T> {

    public abstract void onSucceed(T data);

    public abstract void onFail(String t);

}
