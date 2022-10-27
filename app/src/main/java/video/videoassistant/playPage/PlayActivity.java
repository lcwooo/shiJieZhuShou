package video.videoassistant.playPage;

import androidx.lifecycle.ViewModelProvider;

import video.videoassistant.R;
import video.videoassistant.base.BaseActivity;
import video.videoassistant.databinding.ActivityPlayBinding;
import xyz.doikki.videocontroller.StandardVideoController;
import xyz.doikki.videoplayer.exo.ExoMediaPlayerFactory;

public class PlayActivity extends BaseActivity<PlayModel, ActivityPlayBinding> {

    @Override
    protected PlayModel initViewModel() {
        return new ViewModelProvider(this).get(PlayModel.class);
    }

    @Override
    protected int onCreate() {
        return R.layout.activity_play;
    }

    @Override
    protected void initView() {

        initPlay();
    }

    private void initPlay() {
        dataBinding.player.setUrl("https://hnzy.bfvvs.com/play/penR36le/index.m3u8"); //设置视频地址
        StandardVideoController controller = new StandardVideoController(this);
        controller.addControlComponent(new TitleView(context));
        dataBinding.player.setPlayerFactory(ExoMediaPlayerFactory.create());
        controller.addDefaultControlComponent("", false);
        dataBinding.player.setVideoController(controller); //设置控制器
        dataBinding.player.start(); //开始播放，不调用则不自动播放
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onPause() {
        super.onPause();
        dataBinding.player.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        dataBinding.player.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dataBinding.player.release();
    }


    @Override
    public void onBackPressed() {
        if (!dataBinding.player.onBackPressed()) {
            super.onBackPressed();
        }
    }
}
