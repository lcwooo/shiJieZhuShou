package video.videoassistant.playPage;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.activity.OnBackPressedCallback;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.azhon.basic.base.BaseFragment;
import com.jeremyliao.liveeventbus.LiveEventBus;

import video.videoassistant.R;
import video.videoassistant.base.BaseApplication;
import video.videoassistant.databinding.FragmentPlayBinding;
import video.videoassistant.util.Constant;

import video.videoassistant.util.PreferencesUtils;
import video.videoassistant.util.UiUtil;
import xyz.doikki.videocontroller.component.GestureView;
import xyz.doikki.videoplayer.player.BaseVideoView;
import xyz.doikki.videoplayer.player.VideoView;

public class PlayFragment extends BaseFragment<PlayModel, FragmentPlayBinding> {

    public static PlayFragment playFragment;
    public String playUrl;
    private static final String TAG = "PlayFragment";
    TitleView titleView;

    //视频信息 解析播放地址 时长
    private String jieUrl;
    private long duration;
    private long betweenTime = 120000;

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


    public static PlayFragment getInstance(String url, int state) {
        Bundle args = new Bundle();
        args.putString("url", url);
        args.putInt("state", state);
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
        titleView = new TitleView(context);
        controller.addControlComponent(titleView);
        RightControlView rightControlView = new RightControlView(context);
        controller.addControlComponent(rightControlView);
        titleView.getMovieSet(new MovieSet() {
            @Override
            public void moreSet() {
                rightControlView.setHide(false);
            }
        });

        int state = 0;
        if (getArguments() != null) {
            state = getArguments().getInt("state", 0);
        }
        PlayBottomView vodControlView = new PlayBottomView(context);
        vodControlView.setHideBottom(state);
        controller.addControlComponent(vodControlView);
        controller.addDefaultControlComponent(false);
        dataBinding.player.setVideoController(controller); //设置控制器
        dataBinding.player.addOnStateChangeListener(new BaseVideoView.OnStateChangeListener() {
            @Override
            public void onPlayerStateChanged(int playerState) {

            }

            @Override
            public void onPlayStateChanged(int playState) {
                switch (playState) {
                    case VideoView.STATE_ERROR:
                        Log.i(TAG, "onPlayStateChanged: 错误");
                        break;
                    case VideoView.STATE_PLAYING:
                        Log.i(TAG, "onPlayStateChanged: 播放成功");
                        playSuccess();
                        break;
                    case VideoView.STATE_START_ABORT:
                        Log.i(TAG, "onPlayStateChanged: 播放终止");
                        break;
                    case VideoView.STATE_PAUSED:
                        saveProgress();
                        Log.i(TAG, "onPlayStateChanged: 播放暂停");
                        break;
                }
            }
        });
    }


    private void playSuccess() {
        viewModel.checkProgress(jieUrl);
        duration = dataBinding.player.getDuration();
        int[] arr = dataBinding.player.getVideoSize();
        PlayInfoBean bean = new PlayInfoBean();
        bean.setUrl(playUrl);
        bean.setInfo(arr[0] + "x" + arr[1]);
        LiveEventBus.get(Constant.playAddressInfo, PlayInfoBean.class)
                .post(bean);
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

        LiveEventBus.get(Constant.fullScreen, String.class)
                .observe(this, new Observer<String>() {
                    @Override
                    public void onChanged(String s) {
                        dataBinding.player.startFullScreen();
                    }
                });

        LiveEventBus.get("movieName", String.class).observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                titleView.setTitle(s);
            }
        });

        viewModel.playProgress.observe(this, new Observer<Long>() {
            @Override
            public void onChanged(Long aLong) {
                UiUtil.showToastSafe("记忆播放");
                dataBinding.player.seekTo(aLong);
            }
        });

    }

    public void play(String url) {
        if(dataBinding.player.isPlaying()){
            saveProgress();
        }
        jieUrl = PreferencesUtils.getString(context, Constant.urlIng, "");
        playUrl = url;
        dataBinding.player.release();
        dataBinding.player.setUrl(url);
        dataBinding.player.start();

    }

    private void saveProgress() {
        if(!BaseApplication.getInstance().isSaveProgress()){
            return;
        }
        //180000
        Log.i(TAG, "play: 保存播放进度" + "\n" + "上个播放视频的时长:" + duration);
        //获取当前播放的时间
        long time = dataBinding.player.getCurrentPosition();
        Log.i(TAG, "saveProgress: " + time);
        if (time < betweenTime || time > (duration - betweenTime)) {
            return;
        }

        viewModel.saveProgress(jieUrl, time);

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
