package video.videoassistant.me.urlManage;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.azhon.basic.lifecycle.BaseViewModel;


import java.util.List;

import io.reactivex.CompletableEmitter;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import video.videoassistant.base.BaseApplication;
import video.videoassistant.base.BaseRoom;
import video.videoassistant.util.UiUtil;

public class UrlManageModel extends BaseViewModel {

    private static final String TAG = "UrlManageModel";
    CollectionUrlDao urlTypeDao;
    public MutableLiveData<List<CollectionUrlEntity>> urlList = new MutableLiveData<>();
    public MutableLiveData<CollectionUrlEntity> addType = new MutableLiveData<>();
    public MutableLiveData<Boolean> isSort = new MutableLiveData<>();
    public MutableLiveData<Boolean> isFinish = new MutableLiveData<>();

    public UrlManageModel() {
        urlTypeDao = BaseRoom.getInstance(BaseApplication.getContext()).urlTypeDao();
    }

    public void getAllUrlType() {
        dbRequest(new ObservableOnSubscribe<Void>() {
            @Override
            public void subscribe(ObservableEmitter<Void> emitter) throws Exception {
                urlList.postValue(urlTypeDao.getAll());
            }
        });
    }

    public void deleteUrlType(CollectionUrlEntity entity) {

        dbRequest(new ObservableOnSubscribe<Void>() {
            @Override
            public void subscribe(ObservableEmitter<Void> emitter) throws Exception {
                urlTypeDao.delete(entity);
                getAllUrlType();
            }
        });

    }

    public void addUrlType(CollectionUrlEntity entity) {



        dbRequest(new ObservableOnSubscribe<Void>() {
            @Override
            public void subscribe(ObservableEmitter<Void> emitter) throws Exception {
                List<CollectionUrlEntity> list = urlTypeDao.getUrlList(entity.url);
                if (UiUtil.listIsEmpty(list)) {
                    urlTypeDao.insert(entity);
                    addType.postValue(entity);
                } else {
                    UiUtil.showToastSafe("已存在该网址");
                }
            }
        });
    }

    public void updateSort(List<CollectionUrlEntity> sortList,boolean isClose) {

        Log.i(TAG, "updateSort: 运行排序");

        dbRequest(new ObservableOnSubscribe<Void>() {
            @Override
            public void subscribe(ObservableEmitter<Void> emitter) throws Exception {
                for (int i = 0; i < sortList.size(); i++) {
                    CollectionUrlEntity entity = sortList.get(i);
                    entity.setPosition(i);
                    urlTypeDao.update(entity);
                }
                if(isClose){
                    isFinish.postValue(true);
                }
            }
        });
    }

    public void updateUrl(CollectionUrlEntity entity) {

        dbRequest(new ObservableOnSubscribe<Void>() {
            @Override
            public void subscribe(ObservableEmitter<Void> emitter) throws Exception {
                urlTypeDao.update(entity);
                getAllUrlType();
            }
        });
    }

    public void clearData() {
        dbRequest(new ObservableOnSubscribe<Void>() {
            @Override
            public void subscribe(ObservableEmitter<Void> emitter) throws Exception {
                urlTypeDao.deleteAll();
                getAllUrlType();
            }
        });
    }
}
