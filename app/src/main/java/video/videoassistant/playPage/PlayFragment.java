package video.videoassistant.playPage;

import android.content.pm.ActivityInfo;

import androidx.activity.OnBackPressedCallback;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.azhon.basic.base.BaseFragment;
import com.jeremyliao.liveeventbus.LiveEventBus;

import video.videoassistant.R;
import video.videoassistant.databinding.FragmentPlayBinding;
import video.videoassistant.util.Constant;
import xyz.doikki.videocontroller.StandardVideoController;

public class PlayFragment extends BaseFragment<PlayModel, FragmentPlayBinding> {
    @Override
    protected PlayModel initViewModel() {
        return new ViewModelProvider(this).get(PlayModel.class);
    }

    @Override
    protected void showError(Object obj) {

    }

    @Override
    protected int onCreate() {
        return R.layout.fragment_play;
    }

    @Override
    protected void initView() {
        requireActivity().getOnBackPressedDispatcher()
                .addCallback(getViewLifecycleOwner(), backPressed);
        initPlay();
    }

    private void initPlay() {
        //dataBinding.player.setUrl("https://hnzy.bfvvs.com/play/penR36le/index.m3u8"); //设置视频地址
        StandardVideoController controller = new StandardVideoController(context);
        TitleView titleView = new TitleView(context);
        controller.addControlComponent(titleView);
        RightControlView rightControlView = new RightControlView(context);
        controller.addControlComponent(rightControlView);
        titleView.getMovieSet(new MovieSet() {
            @Override
            public void moreSet() {
                rightControlView.setHide(false);
            }
        });
        VodControlView vodControlView = new VodControlView(context);
        controller.addControlComponent(vodControlView);
        controller.addDefaultControlComponent("", false);
        dataBinding.player.setVideoController(controller); //设置控制器
        //dataBinding.player.start(); //开始播放，不调用则不自动播放
    }

    @Override
    protected void initData() {

        LiveEventBus.get(Constant.playAddress, String.class)
                .observe(this, new Observer<String>() {
                    @Override
                    public void onChanged(String s) {
                        play(s);
                    }
                });

    }

    public void play(String url) {
        if (dataBinding.player != null) {
            dataBinding.player.release();
            dataBinding.player.setUrl(url);
            dataBinding.player.start();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        dataBinding.player.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        dataBinding.player.resume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dataBinding.player.release();
    }


    private final OnBackPressedCallback backPressed = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            if (dataBinding.player.isFullScreen()) {
                dataBinding.player.stopFullScreen();
                if (getActivity().getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
            } else {
                getActivity().finish();
            }
        }
    };
}
