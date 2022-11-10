package video.videoassistant.browserPage;

import androidx.lifecycle.MutableLiveData;

import com.azhon.basic.lifecycle.BaseViewModel;

import java.util.List;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import video.videoassistant.base.BaseApplication;
import video.videoassistant.base.BaseRoom;
import video.videoassistant.me.handleManage.HandleDao;
import video.videoassistant.me.handleManage.HandleEntity;

public class BrowserModel extends BaseViewModel {

    HandleDao handleDao;
    public MutableLiveData<String> loadUrl = new MutableLiveData<>();
    public MutableLiveData<List<HandleEntity>> handleList = new MutableLiveData<>();
    //在线解析url
    public MutableLiveData<String> lineUrl = new MutableLiveData<>();
    /**
     * 播放地址状态 1:刷新 2:清理
     */
    public MutableLiveData<Integer> urlListState = new MutableLiveData<>();
    /**
     * 单个播放地址处理1:系统播放器播放2:x5播放器播放3,复制地址
     */
    public MutableLiveData<String> xiuUrl = new MutableLiveData<>();

    public BrowserModel() {
        handleDao = BaseRoom.getInstance(BaseApplication.getContext()).getHandleDao();
    }

    public void getHanleList() {
        dbRequest(new ObservableOnSubscribe<Void>() {
            @Override
            public void subscribe(ObservableEmitter<Void> emitter) throws Exception {
                handleList.postValue(handleDao.getAll());
            }
        });
    }

    public String getLoadUrl() {
        if (loadUrl == null) {
            return "";
        } else {
            return loadUrl.getValue();
        }
    }
}
