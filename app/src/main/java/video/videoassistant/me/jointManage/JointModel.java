package video.videoassistant.me.jointManage;

import androidx.lifecycle.MutableLiveData;

import com.azhon.basic.lifecycle.BaseViewModel;
import com.azhon.basic.lifecycle.ResultListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import video.videoassistant.base.BaseApplication;
import video.videoassistant.base.BaseRoom;
import video.videoassistant.net.Api;
import video.videoassistant.util.UiUtil;

public class JointModel extends BaseViewModel {

    private JointDao jointDao;
    public MutableLiveData<List<JointEntity>> listJoint = new MutableLiveData<>();

    public JointModel() {
        jointDao = BaseRoom.getInstance(BaseApplication.getContext()).getJointDao();
    }

    public void checkUrl(JointEntity entity) {
        showDialog.setValue(true);
        Flowable<String> api = Api.getApi().checkUrl(entity.url);
        request(api, new ResultListener<String>() {
            @Override
            public void onSucceed(String data) {
                showDialog.setValue(false);
                initUrl(entity, data);
            }

            @Override
            public void onFail(String t) {
                showDialog.setValue(false);
                UiUtil.showToastSafe(t);
            }
        });
    }

    public void initUrl(JointEntity entity, String json) {
        if (json.startsWith("<?xml")) {
            entity.setType("xml");
            addJoint(entity);
        } else if (json.startsWith("{")) {
            entity.setType("json");
            addJoint(entity);
        } else {
            UiUtil.showToastSafe("接口类型不正确,只支持苹果cms格式接口。");
        }
    }

    public void addJoint(JointEntity entity) {

        dbRequest(new ObservableOnSubscribe<Void>() {
            @Override
            public void subscribe(@NotNull ObservableEmitter<Void> emitter) throws Exception {
                jointDao.insert(entity);
                getAll();
            }
        });
    }

    public void getAll() {
        dbRequest(new ObservableOnSubscribe<Void>() {
            @Override
            public void subscribe(@NotNull ObservableEmitter<Void> emitter) throws Exception {
                List<JointEntity> list =  jointDao.getAll();
                listJoint.postValue(list);
            }
        });
    }
}
