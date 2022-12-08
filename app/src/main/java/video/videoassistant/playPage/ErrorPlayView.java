package video.videoassistant.playPage;


import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.jeremyliao.liveeventbus.LiveEventBus;

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


/**
 * 播放出错提示界面
 * Created by dueeeke on 2017/4/13.
 */
public class ErrorPlayView extends LinearLayout implements IControlComponent {

    private float mDownX;
    private float mDownY;
    private TextView errorJson;
    private TextView errorWeb;
    private String playUrl;

    private ControlWrapper mControlWrapper;

    public ErrorPlayView(Context context) {
        this(context, null);
    }

    public ErrorPlayView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ErrorPlayView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    {

        setVisibility(GONE);
        LayoutInflater.from(getContext()).inflate(R.layout.error_view, this, true);
        errorJson = findViewById(R.id.error_json);
        errorWeb = findViewById(R.id.error_web);
        initData();
        findViewById(R.id.status_btn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setVisibility(GONE);
                mControlWrapper.replay(false);
            }
        });



        errorWeb.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(playUrl)) {
                    return;
                }
                if (playUrl.contains(".m3u8") || playUrl.contains(".mp4")) {
                    UiUtil.showToastSafe("直链播放，无需解析");
                    return;
                }
                initWebView(errorWeb);
            }
        });


        errorJson.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                UiUtil.showToastSafe("json");
                if (TextUtils.isEmpty(playUrl)) {
                    return;
                }
                if (playUrl.contains(".m3u8") || playUrl.contains(".mp4")) {
                    UiUtil.showToastSafe("直链播放，无需解析");
                    return;
                }
                initJsonView(errorJson);
            }
        });

        setClickable(true);
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

    private void initData() {
        LiveEventBus.get(Constant.playUrl, String.class).observe((LifecycleOwner) getContext(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                playUrl = s;
                if(s.contains(".m3u8") || s.contains(".mp4")){
                    errorWeb.setVisibility(GONE);
                    errorJson.setVisibility(GONE);
                }else {
                    errorJson.setVisibility(VISIBLE);
                    errorWeb.setVisibility(VISIBLE);
                }
            }
        });
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

    }

    @Override
    public void onPlayStateChanged(int playState) {
        if (playState == VideoView.STATE_ERROR) {
            bringToFront();
            setVisibility(VISIBLE);
        } else if (playState == VideoView.STATE_IDLE) {
            setVisibility(GONE);
        }
    }

    @Override
    public void onPlayerStateChanged(int playerState) {

    }

    @Override
    public void setProgress(int duration, int position) {

    }

    @Override
    public void onLockStateChanged(boolean isLock) {

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = ev.getX();
                mDownY = ev.getY();
                // True if the child does not want the parent to intercept touch events.
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                float absDeltaX = Math.abs(ev.getX() - mDownX);
                float absDeltaY = Math.abs(ev.getY() - mDownY);
                if (absDeltaX > ViewConfiguration.get(getContext()).getScaledTouchSlop() ||
                        absDeltaY > ViewConfiguration.get(getContext()).getScaledTouchSlop()) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }
}
