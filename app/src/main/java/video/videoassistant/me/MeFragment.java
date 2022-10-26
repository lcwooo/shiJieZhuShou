package video.videoassistant.me;

import androidx.lifecycle.ViewModelProvider;

import com.azhon.basic.base.BaseFragment;

import video.videoassistant.R;
import video.videoassistant.databinding.FragmentMeBinding;
import video.videoassistant.me.jointManage.JointManageActivity;
import video.videoassistant.me.urlManage.UrlManageActivity;


public class MeFragment extends BaseFragment<MeModel, FragmentMeBinding> {
    @Override
    protected MeModel initViewModel() {
        return new ViewModelProvider(this).get(MeModel.class);
    }

    @Override
    protected void showError(Object obj) {

    }

    @Override
    protected int onCreate() {
        return R.layout.fragment_me;
    }

    @Override
    protected void initView() {
        dataBinding.setView(this);
    }

    @Override
    protected void initData() {

    }

    public void urlManage(){
        toActivity(UrlManageActivity.class);
    }

    public void jointManage(){
        toActivity(JointManageActivity.class);
    }
}
