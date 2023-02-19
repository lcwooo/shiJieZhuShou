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
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.TbsVideo;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import video.videoassistant.R;
import video.videoassistant.base.BaseActivity;
import video.videoassistant.databinding.ActivityBrowserBinding;
import video.videoassistant.mainPage.DownService;
import video.videoassistant.mainPage.FileCallBack;
import video.videoassistant.net.ApiService;
import video.videoassistant.playPage.PlayerActivity;
import video.videoassistant.util.Constant;
import video.videoassistant.util.PreferencesUtils;
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
    private String adString = "";
    private WebMenuDialog menuDialog;


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
        dataBinding.name.setSelection(loadUrl.length());
        windowManager = getWindowManager();
        initWeb();
        initAdList();
        initProgressBar();
        dataBinding.x5.loadUrl(loadUrl);


    }

    private void initProgressBar() {
        dataBinding.progressBar.setMax(100);
        dataBinding.progressBar.setProgressDrawable(this.getResources()
                .getDrawable(R.drawable.color_progressbar));
    }

    private void initAdList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String path = getExternalFilesDir("app").getAbsolutePath();
                    File futureStudioIconFile = new File(path, "adRule.txt");
                    if (futureStudioIconFile.exists()) {
                        adString = Files.toString(futureStudioIconFile, Charsets.UTF_8);
                    }
                } catch (Exception e) {
                    UiUtil.showToastSafe(e.getMessage());
                }
            }
        }).start();

    }

    private void initWeb() {
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        WebSettings mWebSettings = dataBinding.x5.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setMediaPlaybackRequiresUserGesture(true);
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
        dataBinding.x5.addJavascriptInterface(new InJavaScriptLocalObj(), "java_obj");// js 注入回调
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
                viewModel.addHistory(webView.getUrl(),webView.getTitle());
                return false;
            }

            @Override
            public void onPageFinished(WebView webView, String s) {
                super.onPageFinished(webView, s);
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView webView, String s) {
                //Log.i(TAG, "shouldInterceptRequest: " + s);
                if ((s.contains("m3u8") || s.contains(".mp4"))
                        && !s.contains("url=") && !s.contains(".ts") && !s.contains(".js")) {
                    if (!playList.contains(s) && playList.size() < 3) {
                        //Log.i(TAG, "shouldInterceptRequest: " + s);
                        playList.add(0, s);
                    } else {
                        playList.remove(playList.size() - 1);
                        playList.add(0, s);
                    }
                    viewModel.urlListState.postValue(1);
                }

                if (isIntercept(s)) {

                    //Log.i(TAG, "shouldInterceptRequest(拦截): " + s);
                    return new WebResourceResponse("image/png", "", null);

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

            @Override
            public void onProgressChanged(WebView webView, int i) {
                super.onProgressChanged(webView, i);
                Log.i(TAG, "onProgressChanged: " + i);
                if (i == 100) {
                    dataBinding.progressBar.setVisibility(View.GONE);//加载完网页进度条消失
                } else {
                    dataBinding.progressBar.setVisibility(View.VISIBLE);//开始加载网页时显示进度条
                    dataBinding.progressBar.setProgress(i);//设置进度值
                }
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
                dataBinding.name.setSelection(s.length());
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
                if (snifferDialog != null) {
                    snifferDialog.dialog.dismiss();
                }
            }
        });


        viewModel.menuState.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                initMenu(integer);
                if (menuDialog != null) {
                    menuDialog.dismiss();
                }
            }
        });
    }

    private void initMenu(Integer integer) {
        switch (integer) {
            case 0:
                dataBinding.x5.reload();
                break;
            case 1:
                copyUrl(dataBinding.x5.getUrl());
                break;
            case 3:
                finish();
                break;
            case 4:
                addBookmark();
                break;
        }
    }

    private void addBookmark() {
        viewModel.addBookmark(dataBinding.x5.getUrl(), dataBinding.x5.getTitle());
        UiUtil.showToastSafe("已经添加");
    }

    private void initXiuUrl(String s) {
        String[] arr = s.split("===");
        Log.i(TAG, "initXiuUrl: " + arr[0] + "\n" + arr[1]);
        if (arr[0].equals("1")) {
            Intent intent = new Intent(this, PlayerActivity.class);
            intent.putExtra("url", arr[1]);
            intent.putExtra("state", 1);
            startActivity(intent);
        } else if (arr[0].equals("2")) {
            m3u8Down(arr[1]);
        } else {
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

    public void m3u8Down(String url) {
        String fs = getExternalFilesDir("playList").getAbsolutePath();
        Log.i(TAG, "m3u8Down: " + fs);
        String downName = "play.m3u8";
        new Retrofit.Builder()
                .baseUrl(ApiService.URL)
                .build()
                .create(DownService.class)
                .downloadFile(url)//可以是完整的地址，也可以是baseurl后面的动态地址
                .enqueue(new FileCallBack(fs.toString(), downName) {
                    @Override
                    public void onSuccess(File file, Progress progress) {
                        if (progress.status == 5) {
                            UiUtil.showToastSafe("下载完成");
                        }
                    }

                    @Override
                    public void onProgress(Progress progress) {

                    }

                    @Override
                    public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {
                        UiUtil.showToastSafe("下载异常:" + t.getMessage());
                    }
                });
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
            snifferDialog = new SnifferDialog(this, playList, viewModel);
        }
        snifferDialog.show();
    }

    public void goIndex() {
        dataBinding.x5.loadUrl("https://bbs.chineni.com");
    }

    public void moreSet() {

        if (menuDialog == null) {
            menuDialog = new WebMenuDialog(this, viewModel);
        }
        menuDialog.show();

    }

    private void fullScreen(View view) {
        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        WebView x5WebView = dataBinding.x5;
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (x5WebView.canGoBack()) {
                x5WebView.goBack();
                return true;
            } else {
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    public boolean isIntercept(String url) {

        String chu = "";

        if (url.contains("http://")) {
            chu = url.replace("http://", "");
        }
        if (url.contains("https://")) {
            chu = url.replace("https://", "");
        }
        if (chu.contains("/")) {
            chu = chu.substring(0, chu.indexOf("/"));
        }
        if (chu.indexOf(".") != chu.lastIndexOf(".")) {
            chu = chu.substring(chu.indexOf(".") + 1);
        }


        if (adString.contains(chu)) {
            //Log.i("haha", "isIntercept: " + url + "\n" + chu);
            String regex = "(?<=[\\|\\|]).*(" + chu + ").*?(?=\\^)";
            Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(adString);
            while (matcher.find()) {
                String group = matcher.group();
                if (url.contains(group.substring(1))) {
                    //Log.i("haha", "isIntercept(拦截): "+url);
                    return true;
                }
            }
        }

        return false;
    }

    public void go() {
        if (dataBinding.name.getText().toString().trim().isEmpty()) {
            return;
        }
        dataBinding.x5.loadUrl(dataBinding.name.getText().toString());
    }

    private class LocalAndroidObj {
        public void showSource(String msg) {
            Log.i(TAG, "showSource(获取资源): " + msg);
        }
    }


}
