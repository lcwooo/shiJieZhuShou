package video.videoassistant.me.jointManage;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

import video.videoassistant.R;
import video.videoassistant.base.BaseActivity;
import video.videoassistant.databinding.ActivityJointBinding;
import video.videoassistant.util.UiUtil;

public class JointManageActivity extends BaseActivity<JointModel, ActivityJointBinding> {
    private DialogAddJoint addJoint;

    @Override
    protected JointModel initViewModel() {
        return new ViewModelProvider(this).get(JointModel.class);
    }

    @Override
    protected int onCreate() {
        return R.layout.activity_joint;
    }

    @Override
    protected void initView() {
        dataBinding.setView(this);
    }

    @Override
    protected void initData() {

        viewModel.getAll();

        viewModel.listJoint.observe(this, new Observer<List<JointEntity>>() {
            @Override
            public void onChanged(List<JointEntity> jointEntities) {
                UiUtil.showToastSafe(jointEntities.size() + "");
            }
        });

    }

    public void back() {

    }

    public void addUrlTypeDialog() {
        addJoint = new DialogAddJoint(this);
        addJoint.show();
        addJoint.addUrl(new DialogAddJoint.AddUrlListener() {
            @Override
            public void addUrl(JointEntity entity) {
                viewModel.checkUrl(entity);
            }

            @Override
            public void editUrl(JointEntity entity) {

            }
        });
    }
}
