package video.videoassistant.indexPage;

import androidx.lifecycle.MutableLiveData;

import com.azhon.basic.lifecycle.BaseViewModel;

import java.util.List;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import video.videoassistant.base.BaseApplication;
import video.videoassistant.base.BaseRoom;
import video.videoassistant.playPage.roomCollect.CollectDao;
import video.videoassistant.playPage.roomCollect.CollectEntity;

public class IndexModel extends BaseViewModel {

    CollectDao collectDao;
    public MutableLiveData<String> keyword = new MutableLiveData<>();
    public MutableLiveData<List<CollectEntity>> collectList = new MutableLiveData<>();

    public IndexModel() {
        collectDao = BaseRoom.getInstance(BaseApplication.getContext()).getCollectDao();
    }

    public void getCollect() {

        dbRequest(new ObservableOnSubscribe<Void>() {
            @Override
            public void subscribe(ObservableEmitter<Void> emitter) throws Exception {
                List<CollectEntity> list =  collectDao.getAll();
                collectList.postValue(list);
            }
        });
    }
}
