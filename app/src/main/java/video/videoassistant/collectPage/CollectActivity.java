package video.videoassistant.collectPage;

import video.videoassistant.R;
import video.videoassistant.base.BaseNoModelActivity;
import video.videoassistant.databinding.ActivityCollectBinding;
import video.videoassistant.playPage.X5PlayFragment;

public class CollectActivity extends BaseNoModelActivity<ActivityCollectBinding> {
    @Override
    protected int onCreate() {
        return R.layout.activity_collect;
    }

    @Override
    protected void initView() {
        int state = getIntent().getIntExtra("page",0);
        if(state==0){
            getSupportFragmentManager().beginTransaction().replace(R.id.page, new MovieCollectFragment())
                    .commit();
        }
    }

    @Override
    protected void initData() {

    }
}
