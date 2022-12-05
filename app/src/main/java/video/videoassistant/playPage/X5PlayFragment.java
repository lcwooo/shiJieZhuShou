package video.videoassistant.playPage;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.azhon.basic.base.BaseFragment;
import com.jeremyliao.liveeventbus.LiveEventBus;
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import video.videoassistant.R;
import video.videoassistant.databinding.FragmentX5Binding;
import video.videoassistant.mainPage.DownService;
import video.videoassistant.mainPage.FileCallBack;
import video.videoassistant.net.Api;
import video.videoassistant.net.ApiService;
import video.videoassistant.util.Constant;

public class X5PlayFragment extends BaseFragment<PlayModel, FragmentX5Binding> {

    private static X5PlayFragment playFragment;
    private static final String TAG = "X5PlayFragment";
    private List<String> playArr = new ArrayList<>();
    private static final String mHomeUrl = "file:///android_asset/homePage.html";


    public static X5PlayFragment getInstance(String url) {
        Bundle args = new Bundle();
        args.putString("url", url);
        if (playFragment == null) {
            playFragment = new X5PlayFragment();
        } else {
            LiveEventBus.get(Constant.playAddress, String.class).post(url);
        }
        playFragment.setArguments(args);
        return playFragment;
    }


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
        initWeb();
        dataBinding.web.loadUrl(mHomeUrl);
        if (getArguments() != null) {
            String url = getArguments().getString("url");
            if (!TextUtils.isEmpty(url)) {
                dataBinding.web.loadUrl(url);
            }
        }

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
        dataBinding.web.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView webView, String s, Bitmap bitmap) {
                super.onPageStarted(webView, s, bitmap);
                playArr.clear();
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView webView, String s) {
                Log.i(TAG, "shouldInterceptRequest: " + s);
                if ((s.contains("m3u8") || s.contains(".mp4"))
                        ) {
                    if (!playArr.contains(s) && playArr.size() < 1) {
                        Log.i(TAG, "shouldInterceptRequest(播放地址): " + s);
                        playArr.add(s);
                        //checkM3u8();
                        stopLoad(s);
                    }

                }
                return super.shouldInterceptRequest(webView, s);
            }

            @Override
            public void onPageFinished(WebView webView, String s) {
                super.onPageFinished(webView, s);
                if (dataBinding.web.getProgress() == 100) {
                    Log.i(TAG, "onPageFinished: 加载完成");
                }
            }
        });
        dataBinding.web.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onShowCustomView(View view, IX5WebChromeClient.CustomViewCallback customViewCallback) {

            }

            @Override
            public void onHideCustomView() {

            }


            @Override
            public void onProgressChanged(WebView webView, int i) {
                super.onProgressChanged(webView, i);
                if(i==100 && dataBinding.web.getProgress()==100){
                    Log.i(TAG, "onProgressChanged: 加载完成");
                }
            }
        });
    }

    private void stopLoad(String s) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dataBinding.web.loadUrl(mHomeUrl);
                LiveEventBus.get(Constant.playAddress, String.class).post(s);
                dismissDialog();
            }
        });
    }


    private void checkM3u8() {
        String url = playArr.get(0);
        if (url.contains(".mp4")) {
            return;
        }
        Log.i(TAG, "checkM3u8: " + url);
        String fs = getActivity().getExternalFilesDir("playList").getAbsolutePath();
        Log.i(TAG, "m3u8Down: " + fs);
        String downName = "x5Play.m3u8";
        new Retrofit.Builder().client(new Api().setClient())
                .baseUrl(ApiService.URL)
                .build()
                .create(DownService.class)
                .downloadFile(url)//可以是完整的地址，也可以是baseurl后面的动态地址
                .enqueue(new FileCallBack(fs.toString(), downName) {
                    @Override
                    public void onSuccess(File file, Progress progress) {
                        if (progress.status == 5) {
                            Log.i(TAG, "onSuccess: 下载完成");
                        }
                    }

                    @Override
                    public void onProgress(Progress progress) {

                    }

                    @Override
                    public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {
                        Log.i(TAG, "onFailure: " + t.getMessage());
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

        LiveEventBus.get(Constant.webUrlGo, String.class)
                .observe(this, new Observer<String>() {
                    @Override
                    public void onChanged(String s) {
                        showDialog("正在解析...",true);
                        dataBinding.web.loadUrl(s);
                    }
                });
    }


}
