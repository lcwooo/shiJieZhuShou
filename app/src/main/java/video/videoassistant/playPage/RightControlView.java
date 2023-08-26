package video.videoassistant.playPage;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.Field;

import video.videoassistant.R;
import xyz.doikki.videoplayer.controller.ControlWrapper;
import xyz.doikki.videoplayer.controller.IControlComponent;
import xyz.doikki.videoplayer.exo.ExoMediaPlayerFactory;
import xyz.doikki.videoplayer.ijk.IjkPlayerFactory;
import xyz.doikki.videoplayer.player.AndroidMediaPlayerFactory;
import xyz.doikki.videoplayer.player.VideoView;
import xyz.doikki.videoplayer.player.VideoViewConfig;
import xyz.doikki.videoplayer.player.VideoViewManager;


public class RightControlView extends FrameLayout implements IControlComponent {

    private ControlWrapper mControlWrapper;
    View view;
    LinearLayout addView;
    RadioGroup speed;
    RadioGroup scale;
    TextView playType;
    private int mCurPlayState;
    private static final String TAG = "RightControlView";

    public RightControlView(@NonNull Context context) {
        super(context);
    }

    public RightControlView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RightControlView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    {
        setVisibility(GONE);
        LayoutInflater.from(getContext()).inflate(R.layout.dkplayer_layout_right, this, true);
        view = findViewById(R.id.view);
        addView = findViewById(R.id.add);
        speed = findViewById(R.id.speed);
        playType = findViewById(R.id.play_type);
        speed.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.x) {
                    mControlWrapper.setSpeed(0.5f);
                } else if (checkedId == R.id.xx) {
                    mControlWrapper.setSpeed(1.0f);
                } else if (checkedId == R.id.xxs) {
                    mControlWrapper.setSpeed(1.25f);
                } else if (checkedId == R.id.xxx) {
                    mControlWrapper.setSpeed(1.5f);
                } else if (checkedId == R.id.xxxx) {
                    mControlWrapper.setSpeed(2.0f);
                }
            }
        });
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setVisibility(GONE);
            }
        });
        scale = findViewById(R.id.scale);
        scale.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.scale_default) {
                    mControlWrapper.setScreenScaleType(VideoView.SCREEN_SCALE_DEFAULT);
                } else if (checkedId == R.id.scale_fill) {
                    mControlWrapper.setScreenScaleType(VideoView.SCREEN_SCALE_MATCH_PARENT);
                } else if (checkedId == R.id.scale_169) {
                    mControlWrapper.setScreenScaleType(VideoView.SCREEN_SCALE_16_9);
                } else if (checkedId == R.id.scale_34) {
                    mControlWrapper.setScreenScaleType(VideoView.SCREEN_SCALE_4_3);
                }else if(checkedId == R.id.fill){
                    mControlWrapper.setScreenScaleType(VideoView.SCREEN_SCALE_CENTER_CROP);
                }
            }
        });


    }

    protected String getDebugString(int playState) {

        return getCurrentPlayer() + "\n"
                + "分辨率: " + mControlWrapper.getVideoSize()[0] + "x" + mControlWrapper.getVideoSize()[1];
    }

    protected String getCurrentPlayer() {
        String player;
        Object playerFactory = getCurrentPlayerFactory();
        if (playerFactory instanceof ExoMediaPlayerFactory) {
            player = "ExoPlayer";
        } else if (playerFactory instanceof IjkPlayerFactory) {
            player = "IjkPlayer";
        } else if (playerFactory instanceof AndroidMediaPlayerFactory) {
            player = "MediaPlayer";
        } else {
            player = "unknown";
        }
        return String.format("播放器: %s ", player);
    }

    @Override
    public void attach(@NonNull ControlWrapper controlWrapper) {
        mControlWrapper = controlWrapper;
    }

    @Override
    public View getView() {
        return this;
    }

    public void addControlView(View view) {
        addView.addView(view);
    }

    @Override
    public void onVisibilityChanged(boolean isVisible, Animation anim) {

    }

    public void setHide(boolean b) {
        if (b) {
            setVisibility(GONE);
        } else {
            setVisibility(VISIBLE);
        }
    }

    @Override
    public void onPlayStateChanged(int playState) {
        playType.setText(getDebugString(playState));
        switch (playState) {
            case VideoView.STATE_IDLE:
            case VideoView.STATE_START_ABORT:
            case VideoView.STATE_PREPARING:
            case VideoView.STATE_PREPARED:
            case VideoView.STATE_ERROR:
            case VideoView.STATE_PLAYBACK_COMPLETED:
                setVisibility(GONE);
                break;
        }
    }

    @Override
    public void onPlayerStateChanged(int playerState) {
        if (playerState == VideoView.PLAYER_FULL_SCREEN) {
            if (mControlWrapper.isShowing() && !mControlWrapper.isLocked()) {
                //setVisibility(VISIBLE);
            }
        } else {
            setVisibility(GONE);
        }
    }

    @Override
    public void setProgress(int duration, int position) {

    }

    @Override
    public void onLockStateChanged(boolean isLocked) {
        if (isLocked) {
            setVisibility(GONE);
        } else {
            //setVisibility(VISIBLE);
        }
    }

    public String getFpx() {
        if (mControlWrapper != null) {
            return mControlWrapper.getVideoSize()[0] + "=" + mControlWrapper.getVideoSize()[1];
        } else {
            return "0";
        }
    }

    public long getTime(){
        return mControlWrapper.getDuration();
    }

    /**
     * 获取当前的播放核心
     */
    public static Object getCurrentPlayerFactory() {
        VideoViewConfig config = VideoViewManager.getConfig();
        Object playerFactory = null;
        try {
            Field mPlayerFactoryField = config.getClass().getDeclaredField("mPlayerFactory");
            mPlayerFactoryField.setAccessible(true);
            playerFactory = mPlayerFactoryField.get(config);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return playerFactory;
    }


}
