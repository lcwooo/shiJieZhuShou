package com.azhon.basic.lifecycle;

import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.azhon.basic.bean.DialogBean;

import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 项目名:    TODO-MVVM
 * 包名       com.azhon.basic.lifecycle
 * 文件名:    BaseViewModel
 * 创建时间:  2019-03-27 on 10:44
 * 描述:     TODO ViewModel基类，管理rxJava发出的请求，ViewModel销毁同时也取消请求
 *
 * @author 阿钟
 */

public abstract class BaseViewModel extends ViewModel {

    /**
     * 管理RxJava请求
     */
    private CompositeDisposable compositeDisposable;
    /**
     * 用来通知 Activity／Fragment 是否显示等待Dialog
     */
    public DialogLiveData<DialogBean> showDialog = new DialogLiveData<>();
    /**
     * 当ViewModel层出现错误需要通知到Activity／Fragment
     */
    protected MutableLiveData<Object> error = new MutableLiveData<>();

    //关闭页面
    public MutableLiveData<Void> finish = new MutableLiveData<>();

    /**
     * 添加 rxJava 发出的请求
     */
    protected void addDisposable(Disposable disposable) {
        if (compositeDisposable == null || compositeDisposable.isDisposed()) {
            compositeDisposable = new CompositeDisposable();
        }
        compositeDisposable.add(disposable);
    }

    public void getShowDialog(LifecycleOwner owner, Observer<DialogBean> observer) {
        showDialog.observe(owner, observer);
    }

    public void getError(LifecycleOwner owner, Observer<Object> observer) {
        error.observe(owner, observer);
    }

    /**
     * ViewModel销毁同时也取消请求
     */
    @Override
    protected void onCleared() {
        super.onCleared();
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
            compositeDisposable = null;
        }
        showDialog = null;
        error = null;
    }

    public void finish() {
        finish.setValue(null);
    }

    public <T> void request(Flowable<T> a, final ResultListener<T> listener) {
        Disposable disposable = a
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<T>() {
                    @Override
                    public void accept(T tHttpResult) throws Exception {

                        listener.onSucceed(tHttpResult);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        String msg = throwable.getMessage().toString();
                        Log.i("haha", "accept: "+msg);
                        if (msg.contains("timeout")) {
                            msg = "连接超时";
                        }
                        listener.onFail(msg);
                    }
                });
        addDisposable(disposable);
    }




    public void dbRequest(ObservableOnSubscribe<Void> t){
        Observable.create(t).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe();
    }



}
