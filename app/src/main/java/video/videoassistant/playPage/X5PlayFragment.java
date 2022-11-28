package video.videoassistant.playPage;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.databinding.adapters.SeekBarBindingAdapter;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.azhon.basic.base.BaseFragment;
import com.azhon.basic.lifecycle.ResultListener;
import com.jeremyliao.liveeventbus.LiveEventBus;
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import video.videoassistant.R;
import video.videoassistant.databinding.FragmentX5Binding;
import video.videoassistant.mainPage.DownService;
import video.videoassistant.mainPage.FileCallBack;
import video.videoassistant.net.Api;
import video.videoassistant.net.ApiService;
import video.videoassistant.util.Constant;
import video.videoassistant.util.UiUtil;

public class X5PlayFragment extends BaseFragment<PlayModel, FragmentX5Binding> {

    private WindowManager windowManager;
    private View fullScreenLayer;
    private static final String TAG = "X5PlayFragment";
    private List<String> playArr = new ArrayList<>();

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
        dataBinding.web.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView webView, String s, Bitmap bitmap) {
                super.onPageStarted(webView, s, bitmap);
                playArr.clear();
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView webView, String s) {
                //Log.i(TAG, "shouldInterceptRequest: " + s);
                if ((s.contains("m3u8") || s.contains(".mp4"))
                        && !s.contains("url=") && !s.contains(".ts")) {
                    Log.i(TAG, "shouldInterceptRequest(播放地址): " + s);
                    if (!playArr.contains(s) && playArr.size() < 1) {
                        playArr.add(s);
                        checkM3u8();
                    }

                }
                return super.shouldInterceptRequest(webView, s);
            }

            @Override
            public void onPageFinished(WebView webView, String s) {
                super.onPageFinished(webView, s);
            }
        });
        dataBinding.web.setWebChromeClient(new WebChromeClient() {


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
            //onProgressChanged


            @Override
            public void onProgressChanged(WebView webView, int i) {
                super.onProgressChanged(webView, i);
            }
        });
    }

    private void checkM3u8() {
        String url = playArr.get(0);
        if(url.contains(".mp4")){
            return;
        }
        Log.i(TAG, "checkM3u8: " + url);
        String fs = getActivity().getExternalFilesDir("playList").getAbsolutePath();
        Log.i(TAG, "m3u8Down: " + fs);
        String downName = "x5Play.m3u8";

        Flowable<String> api = Api.getApi().checkUrl(url);
        viewModel.request(api, new ResultListener<String>() {
            @Override
            public void onSucceed(String data) {
              UiUtil.showToastSafe(data);
            }

            @Override
            public void onFail(String t) {
                UiUtil.showToastSafe(t);
                Log.i(TAG, "onFail: "+t);
            }
        });



        /*OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.readTimeout(5000, TimeUnit.MILLISECONDS);
        builder.protocols(Collections.singletonList(Protocol.HTTP_1_1));
        builder.followRedirects(true);
        new Retrofit.Builder().client(builder.build())
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
                        Log.i(TAG, "onFailure: " + t.getMessage());
                    }
                });*/
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
                        dataBinding.web.loadUrl(s);
                    }
                });
    }


}
