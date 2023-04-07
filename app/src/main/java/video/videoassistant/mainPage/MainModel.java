package video.videoassistant.mainPage;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.azhon.basic.lifecycle.BaseViewModel;
import com.azhon.basic.lifecycle.ResultListener;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import video.videoassistant.base.BaseApplication;
import video.videoassistant.base.BaseRoom;
import video.videoassistant.net.Api;
import video.videoassistant.playPage.PlayBean;
import video.videoassistant.playPage.roomCollect.RememberDao;
import video.videoassistant.playPage.roomCollect.RememberEntity;

public class MainModel extends BaseViewModel {

    public MutableLiveData<RuleVersionBean> versionBeanData = new MutableLiveData<>();
    RememberDao rememberDao;

    public MainModel() {
        rememberDao = BaseRoom.getInstance(BaseApplication.getContext()).getRememberDao();
    }


    public void getAdRule() {
        Flowable<RuleVersionBean> api = Api.getApi().getVersionBean("https://www.233dy.top/public/config.json");

        request(api, new ResultListener<RuleVersionBean>() {
            @Override
            public void onSucceed(RuleVersionBean data) {
                versionBeanData.postValue(data);
            }

            @Override
            public void onFail(String t) {

            }
        });
    }

    public void initProgress() {
        dbRequest(new ObservableOnSubscribe<Void>() {
            @Override
            public void subscribe(ObservableEmitter<Void> emitter) throws Exception {
                List<RememberEntity> all = rememberDao.getAll();
                if (all.size() > 300) {
                    int a = all.size() - 300;
                    for (int i = 0; i < a; i++) {
                        rememberDao.delete(all.get(i));
                    }
                }

            }
        });
    }
}
