package video.videoassistant.playPage;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.jeremyliao.liveeventbus.LiveEventBus;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import video.videoassistant.R;
import video.videoassistant.base.BaseApplication;
import video.videoassistant.me.handleManage.HandleEntity;
import video.videoassistant.me.jsonManage.JsonEntity;
import video.videoassistant.util.Constant;
import video.videoassistant.util.UiUtil;
import xyz.doikki.videoplayer.controller.ControlWrapper;
import xyz.doikki.videoplayer.controller.IControlComponent;
import xyz.doikki.videoplayer.player.VideoView;
import xyz.doikki.videoplayer.util.PlayerUtils;

import static xyz.doikki.videoplayer.util.PlayerUtils.stringForTime;

public class PlayBottomView extends FrameLayout implements IControlComponent, View.OnClickListener, SeekBar.OnSeekBarChangeListener {


    protected ControlWrapper mControlWrapper;
    private TextView mTotalTime, mCurrTime, next, up, json, website, rate, push;
    private ImageView mFullScreen;
    private LinearLayout mBottomContainer;
    private SeekBar mVideoProgress;
    private ProgressBar mBottomProgress;
    private ImageView mPlayButton;
    private TextView speed;
    private static final String TAG = "VodControlView";
    private boolean mIsDragging;
    private boolean mIsShowBottomProgress = true;


    public PlayBottomView(@NonNull Context context) {
        super(context);
    }

    public PlayBottomView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PlayBottomView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    {
        initData();
        setVisibility(GONE);
        LayoutInflater.from(getContext()).inflate(getLayoutId(), this, true);
        mFullScreen = findViewById(R.id.fullscreen);
        mFullScreen.setOnClickListener(this);
        speed = findViewById(R.id.speed);
        mBottomContainer = findViewById(R.id.bottom_container);
        mVideoProgress = findViewById(R.id.seekBar);
        mVideoProgress.setOnSeekBarChangeListener(this);
        mTotalTime = findViewById(R.id.total_time);
        mCurrTime = findViewById(R.id.curr_time);
        mPlayButton = findViewById(R.id.iv_play);
        mPlayButton.setOnClickListener(this);
        mBottomProgress = findViewById(R.id.bottom_progress);
        speed.setOnClickListener(this);
        //5.1以下系统SeekBar高度需要设置成WRAP_CONTENT
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            mVideoProgress.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }

        next = findViewById(R.id.next);
        up = findViewById(R.id.up);
        json = findViewById(R.id.json);
        website = findViewById(R.id.website);
        rate = findViewById(R.id.rate);
        push = findViewById(R.id.push);

        next.setOnClickListener(this);
        up.setOnClickListener(this::onClick);
        json.setOnClickListener(this::onClick);
        website.setOnClickListener(this::onClick);
        rate.setOnClickListener(this::onClick);
        push.setOnClickListener(this::onClick);
    }


    private void initData() {

        LiveEventBus.get(Constant.playUrl, String.class).observe((LifecycleOwner) getContext(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s.contains(".m3u8")) {
                    isM3u8(true);
                } else {
                    isM3u8(false);
                }
            }
        });
    }


    public void isM3u8(boolean is) {
        if (is) {
            next.setVisibility(VISIBLE);
            up.setVisibility(VISIBLE);
            json.setVisibility(GONE);
            website.setVisibility(GONE);
            rate.setVisibility(VISIBLE);
            push.setVisibility(VISIBLE);
        } else {
            next.setVisibility(VISIBLE);
            up.setVisibility(VISIBLE);
            json.setVisibility(VISIBLE);
            website.setVisibility(VISIBLE);
            rate.setVisibility(VISIBLE);
            push.setVisibility(VISIBLE);
        }
    }

    protected int getLayoutId() {
        return R.layout.view_play_bottom;
    }

    /**
     * 是否显示底部进度条，默认显示
     */
    public void showBottomProgress(boolean isShow) {
        mIsShowBottomProgress = isShow;
    }

    @Override
    public void attach(@NonNull ControlWrapper controlWrapper) {
        mControlWrapper = controlWrapper;
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void onVisibilityChanged(boolean isVisible, Animation anim) {
        if (isVisible) {
            Log.i(TAG, "onVisibilityChanged: " + isVisible);
            mBottomContainer.setVisibility(VISIBLE);
            if (anim != null) {
                mBottomContainer.startAnimation(anim);
            }
            if (mIsShowBottomProgress) {
                mBottomProgress.setVisibility(GONE);
            }
        } else {
            Log.i(TAG, "onVisibilityChanged: " + isVisible);
            mBottomContainer.setVisibility(GONE);
            if (anim != null) {
                mBottomContainer.startAnimation(anim);
            }
            if (mIsShowBottomProgress) {
                mBottomProgress.setVisibility(VISIBLE);
                AlphaAnimation animation = new AlphaAnimation(0f, 1f);
                animation.setDuration(300);
                mBottomProgress.startAnimation(animation);
            }
        }
    }

    @Override
    public void onPlayStateChanged(int playState) {
        switch (playState) {
            case VideoView.STATE_IDLE:
            case VideoView.STATE_PLAYBACK_COMPLETED:
                setVisibility(GONE);
                mBottomProgress.setProgress(0);
                mBottomProgress.setSecondaryProgress(0);
                mVideoProgress.setProgress(0);
                mVideoProgress.setSecondaryProgress(0);
                break;
            case VideoView.STATE_START_ABORT:
            case VideoView.STATE_PREPARING:
            case VideoView.STATE_PREPARED:
            case VideoView.STATE_ERROR:
                setVisibility(GONE);
                break;
            case VideoView.STATE_PLAYING:
                mPlayButton.setSelected(true);
                if (mIsShowBottomProgress) {
                    if (mControlWrapper.isShowing()) {
                        mBottomProgress.setVisibility(GONE);
                        mBottomContainer.setVisibility(VISIBLE);
                    } else {
                        mBottomContainer.setVisibility(GONE);
                        mBottomProgress.setVisibility(VISIBLE);
                    }
                } else {
                    mBottomContainer.setVisibility(GONE);
                }
                setVisibility(VISIBLE);
                //开始刷新进度
                mControlWrapper.startProgress();
                break;
            case VideoView.STATE_PAUSED:
                mPlayButton.setSelected(false);
                break;
            case VideoView.STATE_BUFFERING:
            case VideoView.STATE_BUFFERED:
                mPlayButton.setSelected(mControlWrapper.isPlaying());
                break;
        }
    }

    @Override
    public void onPlayerStateChanged(int playerState) {
        switch (playerState) {
            case VideoView.PLAYER_NORMAL:
                mFullScreen.setSelected(false);
                break;
            case VideoView.PLAYER_FULL_SCREEN:
                mFullScreen.setSelected(true);
                break;
        }

        Activity activity = PlayerUtils.scanForActivity(getContext());
        if (activity != null && mControlWrapper.hasCutout()) {
            int orientation = activity.getRequestedOrientation();
            int cutoutHeight = mControlWrapper.getCutoutHeight();
            if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                mBottomContainer.setPadding(0, 0, 0, 0);
                mBottomProgress.setPadding(0, 0, 0, 0);
            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                mBottomContainer.setPadding(cutoutHeight, 0, 0, 0);
                mBottomProgress.setPadding(cutoutHeight, 0, 0, 0);
            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                mBottomContainer.setPadding(0, 0, cutoutHeight, 0);
                mBottomProgress.setPadding(0, 0, cutoutHeight, 0);
            }
        }
    }

    @Override
    public void setProgress(int duration, int position) {
        if (mIsDragging) {
            return;
        }

        if (mVideoProgress != null) {
            if (duration > 0) {
                mVideoProgress.setEnabled(true);
                int pos = (int) (position * 1.0 / duration * mVideoProgress.getMax());
                mVideoProgress.setProgress(pos);
                mBottomProgress.setProgress(pos);
            } else {
                mVideoProgress.setEnabled(false);
            }
            int percent = mControlWrapper.getBufferedPercentage();
            if (percent >= 95) { //解决缓冲进度不能100%问题
                mVideoProgress.setSecondaryProgress(mVideoProgress.getMax());
                mBottomProgress.setSecondaryProgress(mBottomProgress.getMax());
            } else {
                mVideoProgress.setSecondaryProgress(percent * 10);
                mBottomProgress.setSecondaryProgress(percent * 10);
            }
        }

        if (mTotalTime != null)
            mTotalTime.setText(stringForTime(duration));
        if (mCurrTime != null)
            mCurrTime.setText(stringForTime(position));
    }

    public String getTime() {
        return mCurrTime.getText().toString();
    }

    @Override
    public void onLockStateChanged(boolean isLocked) {
        onVisibilityChanged(!isLocked, null);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.fullscreen) {
            toggleFullScreen();
        } else if (id == R.id.iv_play) {
            mControlWrapper.togglePlay();
        } else if (id == R.id.speed) {
            initSpeedView();
        } else if (id == R.id.next) {
            setState(1);
        } else if (id == R.id.up) {
            setState(2);
        } else if (id == R.id.json) {
            initJsonView(json);
        } else if (id == R.id.website) {
            initWebView(website);
        } else if (id == R.id.rate) {
            initSpeedView();
        } else if (id == R.id.push) {
            setState(6);
        }
    }

    private void initWebView(TextView website) {
        if (UiUtil.listIsEmpty(BaseApplication.getInstance().getHandleEntities())) {
            UiUtil.showToastSafe("您没有添加任何json解析接口");
            return;
        }
        List<HandleEntity> list = BaseApplication.getInstance().getHandleEntities();
        PopupMenu popupMenu = new PopupMenu(getContext(), website);
        android.view.Menu menu_more = popupMenu.getMenu();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            menu_more.add(android.view.Menu.NONE, android.view.Menu.FIRST + i, i,
                    UiUtil.getMaxLength(list.get(i).getName(), 4));

        }

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int i = item.getItemId();
                LiveEventBus.get(Constant.selectJiexi, Object.class)
                        .post(list.get(i - 1));
                return true;
            }
        });

        popupMenu.show();
    }

    private void initJsonView(View view) {
        if (UiUtil.listIsEmpty(BaseApplication.getInstance().getJsonEntities())) {
            UiUtil.showToastSafe("您没有添加任何json解析接口");
            return;
        }
        List<JsonEntity> list = BaseApplication.getInstance().getJsonEntities();
        PopupMenu popupMenu = new PopupMenu(getContext(), view);
        android.view.Menu menu_more = popupMenu.getMenu();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            menu_more.add(android.view.Menu.NONE, android.view.Menu.FIRST + i, i,
                    UiUtil.getMaxLength(list.get(i).getName(), 4));

        }

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int i = item.getItemId();
                LiveEventBus.get(Constant.selectJiexi, Object.class)
                        .post(list.get(i - 1));
                return true;
            }
        });

        popupMenu.show();
    }

    private void setState(int state) {
        mControlWrapper.stopFadeOut();
        LiveEventBus.get(Constant.playState, Integer.class).post(state);
        mControlWrapper.startFadeOut();
    }

    private void initSpeedView() {
        PopupMenu popupMenu = new PopupMenu(getContext(), rate);
        popupMenu.getMenuInflater().inflate(R.menu.menu_sign_a, popupMenu.getMenu());
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.s) {
                    mControlWrapper.setSpeed((float) 0.5);
                } else if (item.getItemId() == R.id.x) {
                    mControlWrapper.setSpeed((float) 1);
                } else if (item.getItemId() == R.id.xs) {
                    mControlWrapper.setSpeed((float) 1.5);
                } else if (item.getItemId() == R.id.xx) {
                    mControlWrapper.setSpeed((float) 2);
                } else if (item.getItemId() == R.id.xss) {
                    mControlWrapper.setSpeed((float) 1.25);
                } else if (item.getItemId() == R.id.xsss) {
                    mControlWrapper.setSpeed((float) 1.75);
                }

                if (rate != null) {
                    if (mControlWrapper.getSpeed() == 1.0) {
                        rate.setText("正常");
                    } else {
                        rate.setText("x" + String.valueOf(mControlWrapper.getSpeed()));
                    }
                }

                return true;
            }
        });
    }

    /**
     * 横竖屏切换
     */
    private void toggleFullScreen() {
        Activity activity = PlayerUtils.scanForActivity(getContext());
        if (mControlWrapper.isPlaying()) {
            int[] arr = mControlWrapper.getVideoSize();
            if (arr[1] > arr[0]) {
                mControlWrapper.toggleFullScreen();
            } else {
                mControlWrapper.toggleFullScreen(activity);
            }
        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mIsDragging = true;
        mControlWrapper.stopProgress();
        mControlWrapper.stopFadeOut();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        long duration = mControlWrapper.getDuration();
        long newPosition = (duration * seekBar.getProgress()) / mVideoProgress.getMax();
        mControlWrapper.seekTo((int) newPosition);
        mIsDragging = false;
        mControlWrapper.startProgress();
        mControlWrapper.startFadeOut();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (!fromUser) {
            return;
        }

        long duration = mControlWrapper.getDuration();
        long newPosition = (duration * progress) / mVideoProgress.getMax();
        if (mCurrTime != null)
            mCurrTime.setText(stringForTime((int) newPosition));
    }
}
