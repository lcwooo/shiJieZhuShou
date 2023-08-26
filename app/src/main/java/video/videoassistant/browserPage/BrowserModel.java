package video.videoassistant.browserPage;

import androidx.lifecycle.MutableLiveData;

import com.azhon.basic.lifecycle.BaseViewModel;

import java.util.List;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import video.videoassistant.base.BaseApplication;
import video.videoassistant.base.BaseRoom;
import video.videoassistant.browserPage.browserRoom.BookmarkDao;
import video.videoassistant.browserPage.browserRoom.BookmarkEntity;
import video.videoassistant.browserPage.browserRoom.HistoryDao;
import video.videoassistant.browserPage.browserRoom.HistoryEntity;
import video.videoassistant.me.handleManage.HandleDao;
import video.videoassistant.me.handleManage.HandleEntity;
import video.videoassistant.util.UiUtil;

public class BrowserModel extends BaseViewModel {

    HandleDao handleDao;
    BookmarkDao bookmarkDao;

    HistoryDao historyDao;
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

    //输新
    public MutableLiveData<Integer> menuState = new MutableLiveData<>();

    public MutableLiveData<Boolean> clearXiu = new MutableLiveData<>();

    public BrowserModel() {
        handleDao = BaseRoom.getInstance(BaseApplication.getContext()).getHandleDao();
        bookmarkDao = BaseRoom.getInstance(BaseApplication.getContext()).getBookmarkDao();
        historyDao = BaseRoom.getInstance(BaseApplication.getContext()).getHistoryDao();
    }

    public void getHanleList() {
        dbRequest(new ObservableOnSubscribe<Void>() {
            @Override
            public void subscribe(ObservableEmitter<Void> emitter) throws Exception {
                handleList.postValue(handleDao.getAll());
            }
        });
    }

    public void setMenuState(int a) {
        menuState.postValue(a);
    }


    public String getLoadUrl() {
        if (loadUrl == null) {
            return "";
        } else {
            return loadUrl.getValue();
        }
    }

    public void addBookmark(String url, String title) {
        dbRequest(new ObservableOnSubscribe<Void>() {
            @Override
            public void subscribe(ObservableEmitter<Void> emitter) throws Exception {
                List<BookmarkEntity> all = bookmarkDao.getAll();
                if (all.size() > 80) {
                    UiUtil.showToastSafe("已经超过80条上限,请删除一些再试");
                    return;
                }
                BookmarkEntity bookmarkEntity = new BookmarkEntity();
                bookmarkEntity.setName(title);
                bookmarkEntity.setUrl(url);
                bookmarkDao.insert(bookmarkEntity);
            }
        });
    }

    public void addHistory(String url, String title) {

        dbRequest(new ObservableOnSubscribe<Void>() {
            @Override
            public void subscribe(ObservableEmitter<Void> emitter) throws Exception {
                List<HistoryEntity> all = historyDao.getAll();
                if (all.size() > 50) {
                    historyDao.deleteOne(all.get(all.size() - 1).getUrl());
                }
                HistoryEntity historyEntity = new HistoryEntity();
                historyEntity.setName(title);
                historyEntity.setUrl(url);
                historyDao.insert(historyEntity);
            }
        });
    }
}
