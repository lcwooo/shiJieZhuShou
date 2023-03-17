package video.videoassistant.playPage;

import android.text.TextUtils;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.azhon.basic.lifecycle.BaseViewModel;
import com.azhon.basic.lifecycle.ResultListener;
import com.jeremyliao.liveeventbus.LiveEventBus;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import video.videoassistant.base.BaseActivity;
import video.videoassistant.base.BaseApplication;
import video.videoassistant.base.BaseRoom;
import video.videoassistant.databinding.ActivityPlayBinding;
import video.videoassistant.me.handleManage.HandleDao;
import video.videoassistant.me.handleManage.HandleEntity;
import video.videoassistant.me.jsonManage.JsonDao;
import video.videoassistant.me.jsonManage.JsonEntity;
import video.videoassistant.net.Api;
import video.videoassistant.playPage.roomCollect.CollectDao;
import video.videoassistant.playPage.roomCollect.CollectEntity;
import video.videoassistant.util.Constant;
import video.videoassistant.util.UiUtil;

public class PlayModel extends BaseViewModel {

    private JsonDao jsonDao;
    HandleDao handleDao;

    CollectDao collectDao;
    public MutableLiveData<List<JsonEntity>> jsonList = new MutableLiveData<>();
    public MutableLiveData<List<HandleEntity>> handleList = new MutableLiveData<>();
    public MutableLiveData<String> playAddress = new MutableLiveData<>();

    public MutableLiveData<String> dlnaAddress = new MutableLiveData<>();


    public PlayModel() {
        jsonDao = BaseRoom.getInstance(BaseApplication.getContext()).getJsonDao();
        handleDao = BaseRoom.getInstance(BaseApplication.getContext()).getHandleDao();
        collectDao = BaseRoom.getInstance(BaseApplication.getContext()).getCollectDao();
    }

    public void getJsonList() {
        dbRequest(new ObservableOnSubscribe<Void>() {
            @Override
            public void subscribe(ObservableEmitter<Void> emitter) throws Exception {
                jsonList.postValue(jsonDao.getAll());
            }
        });
    }

    public void getHandleList() {
        dbRequest(new ObservableOnSubscribe<Void>() {
            @Override
            public void subscribe(ObservableEmitter<Void> emitter) throws Exception {
                handleList.postValue(handleDao.getAll());
            }
        });
    }

    public void getPlayAddress(String s) {

        Flowable<PlayBean> api = Api.getApi().getPlayUrl(s);
        showDialog.setValue(true);
        request(api, new ResultListener<PlayBean>() {
            @Override
            public void onSucceed(PlayBean data) {
                showDialog.setValue(false);
                if (TextUtils.isEmpty(data.getUrl())) {
                    UiUtil.showToastSafe("解析失败,请更换解析试试");
                } else {
                    playAddress.setValue(data.getUrl());
                }
            }

            @Override
            public void onFail(String t) {
                showDialog.setValue(false);
                UiUtil.showToastSafe(t);
            }
        });
    }

    public void addCollect(String url, String json) {
        dbRequest(new ObservableOnSubscribe<Void>() {
            @Override
            public void subscribe(ObservableEmitter<Void> emitter) throws Exception {
                List<CollectEntity> all = collectDao.getAll();
                if (all.size() >= 80) {
                    UiUtil.showToastSafe("收藏已经超过80条上限,请删除一些再试");
                    return;
                }

                List<CollectEntity> list = collectDao.getUrlList(url);
                if (!UiUtil.listIsEmpty(list)) {
                    UiUtil.showToastSafe("已经收藏该视频");
                    return;
                }
                CollectEntity collectEntity = new CollectEntity();
                collectEntity.setUrl(url);
                collectEntity.setJson(json);
                collectDao.insert(collectEntity);
                UiUtil.showToastSafe("收藏成功");
                LiveEventBus.get(Constant.refreshCollectMovie, String.class).post("yes");
            }
        });
    }

    public void getDlnaAddress(String playUrl, JsonEntity entity) {

        Flowable<PlayBean> api = Api.getApi().getPlayUrl(entity.getUrl()+playUrl);
        showDialog.setValue(true);
        request(api, new ResultListener<PlayBean>() {
            @Override
            public void onSucceed(PlayBean data) {
                showDialog.setValue(false);
                if (TextUtils.isEmpty(data.getUrl())) {
                    UiUtil.showToastSafe("解析失败,请更换解析试试");
                } else {
                    dlnaAddress.setValue(data.getUrl());
                }
            }

            @Override
            public void onFail(String t) {
                showDialog.setValue(false);
                UiUtil.showToastSafe(t);
            }
        });
    }
}