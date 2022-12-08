package video.videoassistant.playPage;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import video.videoassistant.R;
import video.videoassistant.databinding.FragmentX5Binding;
import video.videoassistant.util.Constant;

public class X5PlayFragment extends BaseFragment<PlayModel, FragmentX5Binding> {

    private static X5PlayFragment playFragment;
    private static final String TAG = "X5PlayFragment";
    private List<String> playArr = new ArrayList<>();
    private static final String mHomeUrl = "file:///android_asset/homePage.html";




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
                if ((s.contains("m3u8") || s.contains(".mp4"))) {
                    if (!playArr.contains(s) && playArr.size() < 1) {
                        Log.i(TAG, "shouldInterceptRequest(播放地址): " + s);
                        playArr.add(s);
                        checkM3u8();
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
                if (i == 100 && dataBinding.web.getProgress() == 100) {
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
        if (url.contains(".mp4") || url.contains("233dy")) {
            return;
        }


        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            String fs = getActivity().getExternalFilesDir("playList").getAbsolutePath() + "/webPlay.m3u8";
            InputStream input = conn.getInputStream();
            String str = "";
            if (conn.getResponseCode() == 200) {
                int index;
                byte[] bytes = new byte[1024];
                FileOutputStream downloadFile = new FileOutputStream(fs);
                while ((index = input.read(bytes)) != -1) {
                    str += new String(bytes, 0, index);
                    downloadFile.write(bytes, 0, index);
                    downloadFile.flush();
                }
                input.close();
                downloadFile.close();
                if (str.contains("https://") || str.contains("http://")) {
                    LiveEventBus.get(Constant.dlnaUrl, String.class).post("ok");
                }else {
                    LiveEventBus.get(Constant.dlnaUrl, String.class).post("no");
                }
                Log.i(TAG, "dowmM3U8a: 下载完成" + fs);
            }
        } catch (IOException e) {
            e.printStackTrace();
            LiveEventBus.get(Constant.dlnaUrl, String.class).post("no");
        }

    }


    @Override
    protected void initData() {

        LiveEventBus.get(Constant.webUrlGo, String.class)
                .observe(this, new Observer<String>() {
                    @Override
                    public void onChanged(String s) {
                        showDialog("正在解析...", true);
                        LiveEventBus.get(Constant.dlnaUrl, String.class).post("no");
                        dataBinding.web.loadUrl(s);
                    }
                });
    }


}
