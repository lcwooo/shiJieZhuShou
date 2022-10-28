package video.videoassistant.playPage;

import android.content.pm.ActivityInfo;
import android.view.View;
import android.view.WindowManager;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.azhon.basic.base.BaseFragment;
import com.jeremyliao.liveeventbus.LiveEventBus;
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;

import video.videoassistant.R;
import video.videoassistant.databinding.FragmentX5Binding;
import video.videoassistant.util.Constant;

public class X5PlayFragment extends BaseFragment<PlayModel, FragmentX5Binding> {

    private WindowManager windowManager;
    private View fullScreenLayer;
    @Override
    protected PlayModel initViewModel() {
        return new ViewModelProvider(this).get(PlayModel.class);
    }

    @Override
    protected void showError(Object obj) {

    }

    @Override
    protected int onCreate() {
        return R.layout.fragment_x5;
    }

    @Override
    protected void initView() {
        windowManager = getActivity().getWindowManager();
        initWeb();
    }

    private void initWeb() {
        WebSettings webSetting = dataBinding.web.getSettings();
        webSetting.setJavaScriptEnabled(true);
        webSetting.setAllowFileAccess(true);
        webSetting.setSupportZoom(true);
        webSetting.setDatabaseEnabled(true);
        webSetting.setAppCacheEnabled(true);
        webSetting.setMediaPlaybackRequiresUserGesture(false);
        webSetting.setAllowFileAccess(true);
        webSetting.setDomStorageEnabled(true);
        dataBinding.web.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onShowCustomView(View view, IX5WebChromeClient.CustomViewCallback customViewCallback) {
                windowManager.addView(view, new WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_APPLICATION));
                fullScreen(view);
                fullScreenLayer = view;
                getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                if (getActivity().getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
            }

            @Override
            public void onHideCustomView() {
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                if (getActivity().getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
                windowManager.removeViewImmediate(fullScreenLayer);
                fullScreenLayer = null;
            }
        });
    }

    private void fullScreen(View view) {
        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    @Override
    protected void initData() {

        LiveEventBus.get(Constant.playAddress,String.class)
                .observe(this, new Observer<String>() {
                    @Override
                    public void onChanged(String s) {
                        dataBinding.web.loadUrl(s);
                    }
                });
    }


}
