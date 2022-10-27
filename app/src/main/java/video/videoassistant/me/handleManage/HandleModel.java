package video.videoassistant.me.handleManage;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.azhon.basic.lifecycle.BaseViewModel;

import java.util.List;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import video.videoassistant.base.BaseApplication;
import video.videoassistant.base.BaseRoom;
import video.videoassistant.me.jsonManage.JsonDao;
import video.videoassistant.util.UiUtil;

public class HandleModel extends BaseViewModel {

    private static final String TAG = "UrlManageModel";
    HandleDao urlTypeDao;
    public MutableLiveData<List<HandleEntity>> urlList = new MutableLiveData<>();
    public MutableLiveData<HandleEntity> addType = new MutableLiveData<>();
    public MutableLiveData<Boolean> isSort = new MutableLiveData<>();
    public MutableLiveData<Boolean> isFinish = new MutableLiveData<>();

    public HandleModel() {
        urlTypeDao = BaseRoom.getInstance(BaseApplication.getContext()).getHandleDao();
    }

    public void getAllUrlType() {
        dbRequest(new ObservableOnSubscribe<Void>() {
            @Override
            public void subscribe(ObservableEmitter<Void> emitter) throws Exception {
                urlList.postValue(urlTypeDao.getAll());
            }
        });
    }

    public void deleteUrlType(HandleEntity entity) {

        dbRequest(new ObservableOnSubscribe<Void>() {
            @Override
            public void subscribe(ObservableEmitter<Void> emitter) throws Exception {
                urlTypeDao.delete(entity);
                getAllUrlType();
            }
        });

    }

    public void addUrlType(HandleEntity entity) {



        dbRequest(new ObservableOnSubscribe<Void>() {
            @Override
            public void subscribe(ObservableEmitter<Void> emitter) throws Exception {
                List<HandleEntity> list = urlTypeDao.getUrlList(entity.url);
                if (UiUtil.listIsEmpty(list)) {
                    urlTypeDao.insert(entity);
                    addType.postValue(entity);
                } else {
                    UiUtil.showToastSafe("已存在该网址");
                }
            }
        });
    }

    public void updateSort(List<HandleEntity> sortList,boolean isClose) {

        Log.i(TAG, "updateSort: 运行排序");

        dbRequest(new ObservableOnSubscribe<Void>() {
            @Override
            public void subscribe(ObservableEmitter<Void> emitter) throws Exception {
                for (int i = 0; i < sortList.size(); i++) {
                    HandleEntity entity = sortList.get(i);
                    entity.setPosition(i);
                    urlTypeDao.update(entity);
                }
                if(isClose){
                    isFinish.postValue(true);
                }
            }
        });
    }

    public void updateUrl(HandleEntity entity) {

        dbRequest(new ObservableOnSubscribe<Void>() {
            @Override
            public void subscribe(ObservableEmitter<Void> emitter) throws Exception {
                urlTypeDao.update(entity);
                getAllUrlType();
            }
        });
    }
}
