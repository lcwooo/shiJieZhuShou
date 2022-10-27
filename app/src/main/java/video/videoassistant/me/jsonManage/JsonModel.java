package video.videoassistant.me.jsonManage;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.azhon.basic.lifecycle.BaseViewModel;

import java.util.List;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import video.videoassistant.base.BaseApplication;
import video.videoassistant.base.BaseRoom;
import video.videoassistant.util.UiUtil;

public class JsonModel extends BaseViewModel {

    private static final String TAG = "UrlManageModel";
    JsonDao urlTypeDao;
    public MutableLiveData<List<JsonEntity>> urlList = new MutableLiveData<>();
    public MutableLiveData<JsonEntity> addType = new MutableLiveData<>();
    public MutableLiveData<Boolean> isSort = new MutableLiveData<>();
    public MutableLiveData<Boolean> isFinish = new MutableLiveData<>();

    public JsonModel() {
        urlTypeDao = BaseRoom.getInstance(BaseApplication.getContext()).getJsonDao();
    }

    public void getAllUrlType() {
        dbRequest(new ObservableOnSubscribe<Void>() {
            @Override
            public void subscribe(ObservableEmitter<Void> emitter) throws Exception {
                urlList.postValue(urlTypeDao.getAll());
            }
        });
    }

    public void deleteUrlType(JsonEntity entity) {

        dbRequest(new ObservableOnSubscribe<Void>() {
            @Override
            public void subscribe(ObservableEmitter<Void> emitter) throws Exception {
                urlTypeDao.delete(entity);
                getAllUrlType();
            }
        });

    }

    public void addUrlType(JsonEntity entity) {



        dbRequest(new ObservableOnSubscribe<Void>() {
            @Override
            public void subscribe(ObservableEmitter<Void> emitter) throws Exception {
                List<JsonEntity> list = urlTypeDao.getUrlList(entity.url);
                if (UiUtil.listIsEmpty(list)) {
                    urlTypeDao.insert(entity);
                    addType.postValue(entity);
                } else {
                    UiUtil.showToastSafe("已存在该网址");
                }
            }
        });
    }

    public void updateSort(List<JsonEntity> sortList,boolean isClose) {

        Log.i(TAG, "updateSort: 运行排序");

        dbRequest(new ObservableOnSubscribe<Void>() {
            @Override
            public void subscribe(ObservableEmitter<Void> emitter) throws Exception {
                for (int i = 0; i < sortList.size(); i++) {
                    JsonEntity entity = sortList.get(i);
                    entity.setPosition(i);
                    urlTypeDao.update(entity);
                }
                if(isClose){
                    isFinish.postValue(true);
                }
            }
        });
    }

    public void updateUrl(JsonEntity entity) {

        dbRequest(new ObservableOnSubscribe<Void>() {
            @Override
            public void subscribe(ObservableEmitter<Void> emitter) throws Exception {
                urlTypeDao.update(entity);
                getAllUrlType();
            }
        });
    }
}
