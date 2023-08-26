package video.videoassistant.playPage;

import android.content.Intent;
import android.net.Uri;
import android.os.CountDownTimer;
import android.util.Log;

import androidx.lifecycle.ViewModelProvider;

import com.alibaba.fastjson.JSON;
import com.google.common.eventbus.EventBus;
import com.jeremyliao.liveeventbus.LiveEventBus;

import video.videoassistant.R;
import video.videoassistant.base.BaseActivity;
import video.videoassistant.browserPage.BrowserModel;
import video.videoassistant.databinding.AcitivityPlayerBinding;
import video.videoassistant.util.Constant;
import video.videoassistant.util.UiUtil;

public class PlayerActivity extends BaseActivity<PlayModel, AcitivityPlayerBinding> {
    private static final String TAG = "PlayerActivity";
    String url;

    public void sharePlay() {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Check out this website!");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "https://www.233dy.top/jiexi/?url=" + url);
            startActivity(Intent.createChooser(shareIntent, "Share link via"));
        } catch (Exception e) {
            UiUtil.showToastSafe("没有应用可以打开");
        }
    }

    public void copyPlayUrl() {
        copyUrl(url);
    }


    public void openFill() {
/*        LiveEventBus.get(Constant.fullScreen, String.class)
                .post("");*/
        UiUtil.showToastSafe("请在视频内打开");
    }

    public void changePlay() {

    }

    public void otherPlay() {

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.setDataAndType(Uri.parse(url), "video/mp4");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(Intent.createChooser(intent, "Open video with"));
        } catch (Exception e) {
            UiUtil.showToastSafe("没有应用可以打开");
        }


    }

    public void touPing() {

    }

    @Override
    protected PlayModel initViewModel() {
        return new ViewModelProvider(this).get(PlayModel.class);
    }

    @Override
    protected int onCreate() {
        return R.layout.acitivity_player;
    }

    @Override
    protected void initView() {
        dataBinding.setView(this);
        url = getIntent().getStringExtra("url");
        Log.i(TAG, "onChanged: " + url);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment, PlayFragment.getInstance(url, 1))
                .commit();
    }

    @Override
    protected void initData() {

    }
}
