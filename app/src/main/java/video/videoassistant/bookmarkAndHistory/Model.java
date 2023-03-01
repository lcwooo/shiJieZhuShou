package video.videoassistant.bookmarkAndHistory;

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

public class Model extends BaseViewModel {

    public MutableLiveData<List<BookmarkEntity>> listBookmark = new MutableLiveData<>();
    public MutableLiveData<List<HistoryEntity>> listHistory = new MutableLiveData<>();

    BookmarkDao bookmarkDao;

    HistoryDao historyDao;

    public Model() {

        bookmarkDao = BaseRoom.getInstance(BaseApplication.getContext()).getBookmarkDao();
        historyDao = BaseRoom.getInstance(BaseApplication.getContext()).getHistoryDao();
    }

    public void getBookmark() {
        dbRequest(new ObservableOnSubscribe<Void>() {
            @Override
            public void subscribe(ObservableEmitter<Void> emitter) throws Exception {
                List<BookmarkEntity> list = bookmarkDao.getAll();
                listBookmark.postValue(list);
            }
        });
    }

    public void deleteBook(BookmarkEntity bookmark) {
        dbRequest(new ObservableOnSubscribe<Void>() {
            @Override
            public void subscribe(ObservableEmitter<Void> emitter) throws Exception {
                bookmarkDao.delete(bookmark);
            }
        });
    }

    public void clearBookmark() {
        dbRequest(new ObservableOnSubscribe<Void>() {
            @Override
            public void subscribe(ObservableEmitter<Void> emitter) throws Exception {
                bookmarkDao.deleteAll();
                getBookmark();
            }
        });
    }

    public void clearHistory() {
        dbRequest(new ObservableOnSubscribe<Void>() {
            @Override
            public void subscribe(ObservableEmitter<Void> emitter) throws Exception {
                historyDao.deleteAll();
                getHistory();
            }
        });
    }

    public void getHistory() {
        dbRequest(new ObservableOnSubscribe<Void>() {
            @Override
            public void subscribe(ObservableEmitter<Void> emitter) throws Exception {
                List<HistoryEntity> list = historyDao.getAll();
                listHistory.postValue(list);
            }
        });
    }

    public void deleteHistory(HistoryEntity bookmark) {
        dbRequest(new ObservableOnSubscribe<Void>() {
            @Override
            public void subscribe(ObservableEmitter<Void> emitter) throws Exception {
                historyDao.delete(bookmark);
            }
        });
    }
}
