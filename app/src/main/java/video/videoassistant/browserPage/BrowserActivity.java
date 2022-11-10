package video.videoassistant.browserPage;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.lights.LightState;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.TbsVideo;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.util.ArrayList;
import java.util.List;

import video.videoassistant.R;
import video.videoassistant.base.BaseActivity;
import video.videoassistant.databinding.ActivityBrowserBinding;
import video.videoassistant.playPage.PlayerActivity;
import video.videoassistant.util.UiUtil;

import static com.tencent.smtt.sdk.WebView.setWebContentsDebuggingEnabled;

public class BrowserActivity extends BaseActivity<BrowserModel, ActivityBrowserBinding> {

    private String loadUrl;
    private WindowManager windowManager;
    private View fullScreenLayer;
    private HandleDialog handleDialog;
    private static final String TAG = "BrowserActivity";
    public List<String> playList = new ArrayList<>();
    private SnifferDialog snifferDialog;


    @Override
    protected BrowserModel initViewModel() {
        return new ViewModelProvider(this).get(BrowserModel.class);
    }

    @Override
    protected int onCreate() {
        return R.layout.activity_browser;
    }

    @Override
    protected void initView() {
        dataBinding.setView(this);
        loadUrl = getIntent().getStringExtra("url");
        if (TextUtils.isEmpty(loadUrl)) {
            return;
        }
        dataBinding.name.setText(loadUrl);
        windowManager = getWindowManager();
        initWeb();
        dataBinding.x5.loadUrl(loadUrl);
    }

    private void initWeb() {
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        WebSettings mWebSettings = dataBinding.x5.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setMediaPlaybackRequiresUserGesture(false);
        // 支持屏幕缩放
        mWebSettings.setSupportZoom(true);
        // 设置内置的缩放控件。若为false，则该WebView不可缩放
        mWebSettings.setBuiltInZoomControls(true);
        // 不显示webview缩放按钮
        mWebSettings.setDisplayZoomControls(false);
        // 设置自适应屏幕宽度
        mWebSettings.setUseWideViewPort(true);
        mWebSettings.setLoadWithOverviewMode(true);
        mWebSettings.setAppCacheEnabled(true);
        mWebSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        // 设置缓存模式
        mWebSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        // 允许android调用javascript
        mWebSettings.setDomStorageEnabled(true);
        // 解决图片不显示
        mWebSettings.setBlockNetworkImage(false);
        // 支持自动加载图片
        mWebSettings.setLoadsImagesAutomatically(true);
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        mWebSettings.setDatabaseEnabled(true);
        mWebSettings.setGeolocationEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            // 解决跨域问题
            mWebSettings.setAllowUniversalAccessFromFileURLs(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWebSettings.setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setWebContentsDebuggingEnabled(true);
        }
        dataBinding.x5.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView webView, String s, Bitmap bitmap) {
                super.onPageStarted(webView, s, bitmap);
                if (!s.equals(viewModel.getLoadUrl())) {
                    viewModel.urlListState.setValue(2);
                    viewModel.loadUrl.setValue(s);
                }
                //Log.i(TAG, "onPageStarted: " + s);
            }


            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String s) {
                if (s.contains("://") && !s.contains("http")) {
                    return true;
                }
                return false;
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView webView, String s) {
                if ((s.contains(".m3u8") || s.contains(".mp4"))
                        && !s.contains("url=") && !s.contains(".ts")) {
                    if (!playList.contains(s)) {
                        Log.i(TAG, "shouldInterceptRequest: " + s);
                        playList.add(s);
                    }
                    viewModel.urlListState.postValue(1);
                }
                return super.shouldInterceptRequest(webView, s);
            }
        });
        dataBinding.x5.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onShowCustomView(View view, IX5WebChromeClient.CustomViewCallback customViewCallback) {
                windowManager.addView(view, new WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_APPLICATION));
                fullScreen(view);
                fullScreenLayer = view;
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
            }

            @Override
            public void onHideCustomView() {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
                windowManager.removeViewImmediate(fullScreenLayer);
                fullScreenLayer = null;
            }
        });
    }

    @Override
    protected void initData() {

        viewModel.getHanleList();

        viewModel.loadUrl.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                dataBinding.name.setText(s);
            }
        });

        viewModel.lineUrl.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Log.i(TAG, "onChanged: " + dataBinding.x5.getUrl() + "\n" + UiUtil.handleUrl(dataBinding.x5.getUrl()));
                //dataBinding.x5.loadUrl(s + UiUtil.handleUrl(dataBinding.x5.getUrl()));
            }
        });

        viewModel.urlListState.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer state) {
                if (state == 1) {
                    refreshPlayList();
                } else if (state == 2) {
                    playList.clear();
                    dataBinding.sum.setVisibility(View.GONE);
                }
            }
        });

        viewModel.xiuUrl.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                initXiuUrl(s);
            }
        });
    }

    private void initXiuUrl(String s) {
        String[] arr = s.split("-");
        Log.i(TAG, "initXiuUrl: "+arr[0]+"\n"+arr[1]);
        if(arr[0].equals("1")){
            Intent intent = new Intent(this, PlayerActivity.class);
            intent.putExtra("url",arr[1]);
            startActivity(intent);
        }else if(arr[0].equals("2")){
            x5Play(arr[1]);
        }else {
            copyUrl(arr[1]);
        }
    }


    public void x5Play(String url) {

        if (TbsVideo.canUseTbsPlayer(this)) {
            Bundle data = new Bundle();
            //true表示标准全屏，false表示X5全屏；不设置默认false，
            data.putBoolean("standardFullScreen", false);
            //false：关闭小窗；true：开启小窗；不设置默认true，
            data.putBoolean("supportLiteWnd", false);
            //1：以页面内开始播放，2：以全屏开始播放；不设置默认：1
            data.putInt("DefaultVideoScreen", 2);
            data.putInt("screenMode", 102);
            //直接调用播放接口，传入视频流的url
            TbsVideo.openVideo(this, url, data);
        } else {
            UiUtil.showToastSafe("x5播放器调用失败");
        }
    }

    public void copyUrl(String url) {
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setText(url);
        UiUtil.showToastSafe("已复制");
    }

    private void refreshPlayList() {
        if (UiUtil.listIsEmpty(playList)) {
            dataBinding.sum.setVisibility(View.GONE);
            return;
        }
        dataBinding.sum.setVisibility(View.VISIBLE);
        dataBinding.sum.setText(playList.size() + "");
    }

    public void goBack() {
        if (dataBinding.x5.canGoBack()) {
            dataBinding.x5.goBack();
        } else {
            finish();
        }

    }

    public void jiexi() {
        if (handleDialog == null) {
            handleDialog = new HandleDialog(this, viewModel.handleList.getValue(), viewModel);
        }
        handleDialog.show();

    }

    public void xiu() {
        if (UiUtil.listIsEmpty(playList)) {
            return;
        }
        if (snifferDialog == null) {
            snifferDialog = new SnifferDialog(this, playList,viewModel);
        }
        snifferDialog.show();
    }

    public void goIndex() {
        dataBinding.x5.loadUrl("https://bbs.chineni.com");
    }

    public void moreSet() {

    }

    private void fullScreen(View view) {
        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }


}
