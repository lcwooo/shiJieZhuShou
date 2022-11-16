package video.videoassistant.playPage;

import android.os.CountDownTimer;
import android.util.Log;

import androidx.lifecycle.ViewModelProvider;

import com.alibaba.fastjson.JSON;
import com.jeremyliao.liveeventbus.LiveEventBus;

import video.videoassistant.R;
import video.videoassistant.base.BaseActivity;
import video.videoassistant.browserPage.BrowserModel;
import video.videoassistant.databinding.AcitivityPlayerBinding;
import video.videoassistant.util.Constant;

public class PlayerActivity extends BaseActivity<PlayModel, AcitivityPlayerBinding> {
    private static final String TAG = "PlayerActivity";

    public void sharePlay(){

    }

    public void copyPlayUrl(){

    }


    public void openFill(){

    }

    public void changePlay(){

    }

    public void otherPlay() {

    }

    public void touPing(){

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
        String url = getIntent().getStringExtra("url");
        Log.i(TAG, "onChanged: "+url);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment, PlayFragment.getInstance(url))
                .commit();
    }

    @Override
    protected void initData() {

    }
}
