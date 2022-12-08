package video.videoassistant.playPage;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.activity.OnBackPressedCallback;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.azhon.basic.base.BaseFragment;
import com.jeremyliao.liveeventbus.LiveEventBus;

import video.videoassistant.R;
import video.videoassistant.databinding.FragmentPlayBinding;
import video.videoassistant.util.Constant;

import xyz.doikki.videoplayer.player.BaseVideoView;
import xyz.doikki.videoplayer.player.VideoView;

public class PlayFragment extends BaseFragment<PlayModel, FragmentPlayBinding> {

    public static PlayFragment playFragment;
    public String playUrl;

    @Override
    protected PlayModel initViewModel() {
        return new ViewModelProvider(this).get(PlayModel.class);
    }


    public static PlayFragment getInstance(String url) {
        Bundle args = new Bundle();
        args.putString("url", url);
        if (playFragment == null) {
            playFragment = new PlayFragment();
        } else {
            LiveEventBus.get(Constant.playAddress, String.class).post(url);
        }
        playFragment.setArguments(args);
        return playFragment;
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
        if (getArguments() != null) {
            String url = getArguments().getString("url");
            if (!TextUtils.isEmpty(url)) {
                play(url);
            }
        }
    }

    private void initPlay() {
        StandardVideoController controller = new StandardVideoController(context);
        TitleView titleView = new TitleView(context);
        controller.addControlComponent(titleView);
        controller.addControlComponent(new ErrorPlayView(context));//错误界面
        RightControlView rightControlView = new RightControlView(context);
        controller.addControlComponent(rightControlView);
        titleView.getMovieSet(new MovieSet() {
            @Override
            public void moreSet() {
                rightControlView.setHide(false);
            }
        });
        PlayBottomView vodControlView = new PlayBottomView(context);
        controller.addControlComponent(vodControlView);
        controller.addDefaultControlComponent(false);
        dataBinding.player.setVideoController(controller); //设置控制器
        //dataBinding.player.start(); //开始播放，不调用则不自动播放
        dataBinding.player.addOnStateChangeListener(new BaseVideoView.OnStateChangeListener() {
            @Override
            public void onPlayerStateChanged(int playerState) {

            }

            @Override
            public void onPlayStateChanged(int playState) {
                switch (playState) {
                    case VideoView.STATE_ERROR:

                        break;
                    case VideoView.STATE_PLAYING:

                        break;
                }
            }
        });
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
        playUrl = url;
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

    public String getPlayUrl() {
        return playUrl;
    }
}
