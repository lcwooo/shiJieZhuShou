package video.videoassistant.cloudPage;

import android.text.TextUtils;

import androidx.lifecycle.MutableLiveData;

import com.azhon.basic.lifecycle.BaseViewModel;
import com.azhon.basic.lifecycle.ResultListener;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
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

    public void getData(String url, Map<String, String> map,boolean isShow) {
        Map<String,String> newMap = new HashMap<>();

        for (String key : map.keySet()) {
            if (!TextUtils.isEmpty(map.get(key))) {
               newMap.put(key,map.get(key));
            }
        }

        showDialog.setValue(isShow);

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
        Flowable<String> api = Api.getApi().initListType(url, map);
        showDialog.setValue(true);
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
            }
        });
    }
}
