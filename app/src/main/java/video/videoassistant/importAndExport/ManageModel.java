package video.videoassistant.importAndExport;

import com.azhon.basic.lifecycle.BaseViewModel;

import java.util.List;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import video.videoassistant.base.BaseApplication;
import video.videoassistant.base.BaseRoom;
import video.videoassistant.me.handleManage.HandleDao;
import video.videoassistant.me.jointManage.JointDao;
import video.videoassistant.me.jointManage.JointEntity;
import video.videoassistant.me.jsonManage.JsonDao;
import video.videoassistant.me.urlManage.CollectionUrlDao;

public class ManageModel extends BaseViewModel {
    private JsonDao jsonDao;
    private HandleDao handleDao;
    private JointDao jointDao;

    private CollectionUrlDao urlDao;

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
                List<JointEntity> jointList = jointDao.getAll();
            }
        });
    }
}
