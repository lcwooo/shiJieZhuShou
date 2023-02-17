package video.videoassistant.collectPage;

import androidx.lifecycle.MutableLiveData;

import com.azhon.basic.lifecycle.BaseViewModel;

import java.util.List;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import video.videoassistant.base.BaseApplication;
import video.videoassistant.base.BaseRoom;
import video.videoassistant.cloudPage.XmlMovieBean;
import video.videoassistant.playPage.roomCollect.CollectDao;
import video.videoassistant.playPage.roomCollect.CollectEntity;

public class CollectModel extends BaseViewModel {

    CollectDao collectDao;

    public MutableLiveData<List<CollectEntity>> collectList = new MutableLiveData<>();


    public MutableLiveData<Boolean> closePage = new MutableLiveData<>();
    public MutableLiveData<Boolean> clearData = new MutableLiveData<>();

    public MutableLiveData<XmlMovieBean> deleteOk = new MutableLiveData<>();

    public CollectModel() {
        collectDao = BaseRoom.getInstance(BaseApplication.getContext()).getCollectDao();
    }
    public void closePage(){
        closePage.postValue(true);
    }

    public void clearData(){
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
                List<CollectEntity> list =  collectDao.getAll();
                collectList.postValue(list);
            }
        });
    }

    public void deleteMovie(XmlMovieBean xmlMovieBean) {
        dbRequest(new ObservableOnSubscribe<Void>() {
            @Override
            public void subscribe(ObservableEmitter<Void> emitter) throws Exception {
               collectDao.deleteOne(xmlMovieBean.getBiao());
                deleteOk.postValue(xmlMovieBean);
            }
        });
    }
}
