package video.videoassistant.cloudPage;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.alibaba.fastjson.JSON;
import com.azhon.basic.lifecycle.BaseViewModel;
import com.azhon.basic.lifecycle.ResultListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import video.videoassistant.base.BaseApplication;
import video.videoassistant.base.BaseRoom;
import video.videoassistant.me.jointManage.JointDao;
import video.videoassistant.me.jointManage.JointEntity;
import video.videoassistant.net.Api;
import video.videoassistant.util.UiUtil;

public class CloudModel extends BaseViewModel {

    private JointDao jointDao;
    public MutableLiveData<List<JointEntity>> listJoint = new MutableLiveData<>();
    public MutableLiveData<String> jsonData = new MutableLiveData<>();
    public MutableLiveData<String> typeData = new MutableLiveData<>();
    public MutableLiveData<String> keyword = new MutableLiveData<>();
    public MutableLiveData<SoSumBean> soSum = new MutableLiveData<>();
    private static final String TAG = "CloudModel";

    private Queue<Flowable<String>> mQueue = new LinkedList<>();
    private Disposable mDisposable;

    public CloudModel() {
        jointDao = BaseRoom.getInstance(BaseApplication.getContext()).getJointDao();
    }

    public void getAll() {
        dbRequest(new ObservableOnSubscribe<Void>() {
            @Override
            public void subscribe(@NotNull ObservableEmitter<Void> emitter) throws Exception {
                List<JointEntity> list = jointDao.getAll();
                listJoint.postValue(list);
            }
        });
    }

    public void getData(String url, Map<String, String> map, boolean isShow) {
        Map<String, String> newMap = new HashMap<>();

        for (String key : map.keySet()) {
            if (!TextUtils.isEmpty(map.get(key))) {
                newMap.put(key, map.get(key));
            }
        }

        showDialog.setValue(true, "数据加载中...", true);

        Flowable<String> api = Api.getApi().initListJson(url, newMap);
        request(api, new ResultListener<String>() {
            @Override
            public void onSucceed(String data) {
                jsonData.postValue(data);
                showDialog.setValue(false);
            }

            @Override
            public void onFail(String t) {
                showDialog.setValue(false);
                UiUtil.showToastSafe(t);
            }
        });


    }

    public void getAllType(String url, Map<String, String> map) {
        Log.i(TAG, "getAllType: " + url + "\n" + JSON.toJSONString(map));
        Flowable<String> api = Api.getApi().initListType(url, map);
        showDialog.setValue(true, "数据加载中...", true);
        request(api, new ResultListener<String>() {
            @Override
            public void onSucceed(String data) {
                typeData.postValue(data);
                showDialog.setValue(false);
            }

            @Override
            public void onFail(String t) {
                UiUtil.showToastSafe(t);
                showDialog.setValue(false);
                Log.i(TAG, "onFail: "+t);
            }
        });
    }


    public void getSoSum(List<String> urlList) {
        for (String url : urlList) {
            Flowable<String> request = Api.getApi().checkUrl(url);
            mQueue.add(request);
        }
        if (mDisposable == null || mDisposable.isDisposed()) {
            start();
        }
    }

    private void start() {

        mDisposable = Flowable.create(new FlowableOnSubscribe<String>() {
            @SuppressLint("CheckResult")
            @Override
            public void subscribe(FlowableEmitter<String> emitter) throws Exception {
                while (!emitter.isCancelled() && !mQueue.isEmpty()) {
                    Flowable<String> request = mQueue.poll();
                    if (request == null) {
                        return;
                    }
                    int size = mQueue.size();
                    request.subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<String>() {
                                @Override
                                public void accept(String s) {
                                    SoSumBean soSumBean = new SoSumBean();
                                    soSumBean.setJson(s);
                                    soSumBean.setIndex(size);
                                    soSum.postValue(soSumBean);
                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) {
                                    SoSumBean soSumBean = new SoSumBean();
                                    soSumBean.setJson("null");
                                    soSumBean.setIndex(size);
                                    soSum.postValue(soSumBean);
                                }
                            });
                }
                emitter.onComplete();
            }
        }, BackpressureStrategy.BUFFER).subscribe();
    }

    public void cancelRequests() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
            mQueue.clear();
        }
    }
}
