package video.videoassistant.me;

import android.content.Intent;

import androidx.lifecycle.ViewModelProvider;

import com.azhon.basic.base.BaseFragment;

import video.videoassistant.R;
import video.videoassistant.bookmarkAndHistory.BookmarkHistoryActivity;
import video.videoassistant.collectPage.CollectActivity;
import video.videoassistant.databinding.FragmentMeBinding;
import video.videoassistant.importAndExport.ManageActivity;
import video.videoassistant.me.handleManage.HandleActivity;
import video.videoassistant.me.handleManage.HandleAdapter;
import video.videoassistant.me.jointManage.JointManageActivity;
import video.videoassistant.me.jsonManage.JsonActivity;
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

    public void jsonManage(){
        toActivity(JsonActivity.class);
    }

    public void handleManage(){
        toActivity(HandleActivity.class);
    }

    public void manager(){
        toActivity(ManageActivity.class);
    }

    public void collectManager(int state){
        Intent intent =  new Intent(context, CollectActivity.class);
        intent.putExtra("page",state);
        startActivity(intent);
    }

    public void shuqian(){
        toActivity(BookmarkHistoryActivity.class);
    }
}
