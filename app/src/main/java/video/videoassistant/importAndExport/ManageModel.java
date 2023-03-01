package video.videoassistant.importAndExport;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.azhon.basic.lifecycle.BaseViewModel;
import com.azhon.basic.lifecycle.ResultListener;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import video.videoassistant.base.BaseApplication;
import video.videoassistant.base.BaseRoom;
import video.videoassistant.me.handleManage.HandleDao;
import video.videoassistant.me.handleManage.HandleEntity;
import video.videoassistant.me.jointManage.JointDao;
import video.videoassistant.me.jointManage.JointEntity;
import video.videoassistant.me.jsonManage.JsonDao;
import video.videoassistant.me.jsonManage.JsonEntity;
import video.videoassistant.me.urlManage.CollectionUrlDao;
import video.videoassistant.me.urlManage.CollectionUrlEntity;
import video.videoassistant.net.Api;
import video.videoassistant.util.UiUtil;

public class ManageModel extends BaseViewModel {

    private static final String TAG = "ManageModel";
    private JsonDao jsonDao;
    private HandleDao handleDao;
    private JointDao jointDao;

    private CollectionUrlDao urlDao;

    public MutableLiveData<ManagerBean> managerBeanMutable = new MutableLiveData<>();

    public ManageModel() {
        jsonDao = BaseRoom.getInstance(BaseApplication.getContext()).getJsonDao();
        handleDao = BaseRoom.getInstance(BaseApplication.getContext()).getHandleDao();
        jointDao = BaseRoom.getInstance(BaseApplication.getContext()).getJointDao();
        urlDao = BaseRoom.getInstance(BaseApplication.getContext()).urlTypeDao();
    }

    public void exportFile() {

        dbRequest(new ObservableOnSubscribe<Void>() {
            @Override
            public void subscribe(ObservableEmitter<Void> emitter) throws Exception {
                ManagerBean managerBean = new ManagerBean();
                List<String> jsonDy = new ArrayList<>();
                List<String> webDy = new ArrayList<>();
                List<String> jsonJx = new ArrayList<>();
                List<String> webJx = new ArrayList<>();
                List<JointEntity> jointList = jointDao.getAll();
                List<HandleEntity> handleEntities = handleDao.getAll();
                List<JsonEntity> jsonEntities = jsonDao.getAll();
                List<CollectionUrlEntity> urlEntities = urlDao.getAll();

                for (JointEntity entity : jointList) {
                    String s = entity.getName() + "||" + entity.getUrl();
                    jsonDy.add(s);
                }

                for (HandleEntity entity : handleEntities) {
                    String s = entity.getName() + "||" + entity.getUrl();
                    webJx.add(s);
                }

                for (JsonEntity entity : jsonEntities) {
                    String s = entity.getName() + "||" + entity.getUrl();
                    jsonJx.add(s);
                }

                for (CollectionUrlEntity entity : urlEntities) {
                    String s = entity.getName() + "||" + entity.getUrl();
                    webDy.add(s);
                }

                managerBean.setJointList(jsonDy);
                managerBean.setWebList(webDy);
                managerBean.setJsonJx(jsonJx);
                managerBean.setWebJx(webJx);

                managerBeanMutable.postValue(managerBean);

            }
        });
    }

    public void getUrlData(String url) {

        showDialog.setValue(true);

        Flowable<String> api = Api.getApi().checkUrl(url);
        request(api, new ResultListener<String>() {
            @Override
            public void onSucceed(String data) {
                showDialog.setValue(false);
                UiUtil.showToastSafe(data);
            }

            @Override
            public void onFail(String t) {
                showDialog.setValue(false);
                UiUtil.showToastSafe("获取数据失败:" + t);
            }
        });
    }
}
