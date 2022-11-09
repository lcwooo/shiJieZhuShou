package video.videoassistant.store;

import androidx.lifecycle.MutableLiveData;

import com.azhon.basic.lifecycle.BaseViewModel;

import java.util.List;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import video.videoassistant.base.BaseApplication;
import video.videoassistant.base.BaseRoom;
import video.videoassistant.me.urlManage.CollectionUrlDao;
import video.videoassistant.me.urlManage.CollectionUrlEntity;

public class StoreModel extends BaseViewModel {

    CollectionUrlDao urlTypeDao;
    public MutableLiveData<List<CollectionUrlEntity>> urlList = new MutableLiveData<>();

    public StoreModel() {
        urlTypeDao = BaseRoom.getInstance(BaseApplication.getContext()).urlTypeDao();
    }

    public void getAll() {
        dbRequest(new ObservableOnSubscribe<Void>() {
            @Override
            public void subscribe(ObservableEmitter<Void> emitter) throws Exception {
                urlList.postValue(urlTypeDao.getAll());
            }
        });
    }
}
