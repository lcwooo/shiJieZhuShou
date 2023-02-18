package video.videoassistant.collectPage;

import androidx.lifecycle.MutableLiveData;

import com.azhon.basic.lifecycle.BaseViewModel;
import com.azhon.basic.lifecycle.ResultListener;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import video.videoassistant.base.BaseApplication;
import video.videoassistant.base.BaseRoom;
import video.videoassistant.cloudPage.XmlMovieBean;
import video.videoassistant.net.Api;
import video.videoassistant.playPage.roomCollect.CollectDao;
import video.videoassistant.playPage.roomCollect.CollectEntity;
import video.videoassistant.util.UiUtil;

public class CollectModel extends BaseViewModel {

    CollectDao collectDao;

    public MutableLiveData<List<CollectEntity>> collectList = new MutableLiveData<>();

    public MutableLiveData<String> jsonData = new MutableLiveData<>();
    public MutableLiveData<Boolean> closePage = new MutableLiveData<>();
    public MutableLiveData<Boolean> clearData = new MutableLiveData<>();


    public CollectModel() {
        collectDao = BaseRoom.getInstance(BaseApplication.getContext()).getCollectDao();
    }

    public void closePage() {
        closePage.postValue(true);
    }

    public void clearData() {
        dbRequest(new ObservableOnSubscribe<Void>() {
            @Override
            public void subscribe(ObservableEmitter<Void> emitter) throws Exception {
                collectDao.deleteAll();
                clearData.postValue(true);
            }
        });

    }

    public void getData() {
        dbRequest(new ObservableOnSubscribe<Void>() {
            @Override
            public void subscribe(ObservableEmitter<Void> emitter) throws Exception {
                List<CollectEntity> list = collectDao.getAll();
                collectList.postValue(list);
            }
        });
    }

    public void deleteMovie(CollectEntity xmlMovieBean) {
        dbRequest(new ObservableOnSubscribe<Void>() {
            @Override
            public void subscribe(ObservableEmitter<Void> emitter) throws Exception {
                collectDao.delete(xmlMovieBean);
            }
        });
    }

    public void loadMovie(String url) {
        showDialog.setValue(true);

        Flowable<String> api = Api.getApi().checkUrl(url);
        request(api, new ResultListener<String>() {
            @Override
            public void onSucceed(String data) {
                jsonData.postValue(data);
                showDialog.setValue(false);
            }

            @Override
            public void onFail(String t) {
                showDialog.setValue(false);
                UiUtil.showToastSafe("获取第三方数据失败,请检查第三方网站是否能正常访问");
            }
        });
    }
}
