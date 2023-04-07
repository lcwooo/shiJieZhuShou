package video.videoassistant.base;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import androidx.multidex.MultiDexApplication;


import com.tencent.smtt.export.external.TbsCoreSettings;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsListener;
import com.tencent.smtt.sdk.WebView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import video.videoassistant.me.handleManage.HandleEntity;
import video.videoassistant.me.jsonManage.JsonEntity;
import video.videoassistant.util.UiUtil;
import xyz.doikki.videoplayer.BuildConfig;
import xyz.doikki.videoplayer.ijk.IjkPlayerFactory;
import xyz.doikki.videoplayer.player.VideoViewConfig;
import xyz.doikki.videoplayer.player.VideoViewManager;


public class BaseApplication extends MultiDexApplication {

    private static final String TAG = "BaseApplication";
    private static Context context;
    /**
     * 主线程Handler
     */
    private static Handler mMainThreadHandler;
    /**
     * 主线程ID
     */
    private static int mMainThreadId = -1;

    private static BaseApplication application;

    //解析
    List<JsonEntity> jsonEntities;
    List<HandleEntity> handleEntities;

    private boolean saveProgress;


    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        application = this;
        mMainThreadHandler = new Handler();
        mMainThreadId = android.os.Process.myTid();
        initPlay(IjkPlayerFactory.create());
        initX5();
    }

    private void initX5() {
        if (!startX5WebProcessPreinitService()) {
            return;
        }
        QbSdk.setDownloadWithoutWifi(true);
        //QbSdk.setCoreMinVersion(QbSdk.CORE_VER_ENABLE_202112);

        // 在调用TBS初始化、创建WebView之前进行如下配置
        HashMap map = new HashMap();
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER, true);
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE, true);
        QbSdk.initTbsSettings(map);

        QbSdk.setTbsListener(new TbsListener() {

            /**
             * @param stateCode 用户可处理错误码请参考{@link com.tencent.smtt.sdk.TbsCommonCode}
             */
            @Override
            public void onDownloadFinish(int stateCode) {
                Log.i(TAG, "onDownloadFinished: " + stateCode);
            }

            /**
             * @param stateCode 用户可处理错误码请参考{@link com.tencent.smtt.sdk.TbsCommonCode}
             */
            @Override
            public void onInstallFinish(int stateCode) {
                Log.i(TAG, "onInstallFinished: " + stateCode);
            }

            /**
             * 首次安装应用，会触发内核下载，此时会有内核下载的进度回调。
             * @param progress 0 - 100
             */
            @Override
            public void onDownloadProgress(int progress) {
                Log.i(TAG, "Core Downloading: " + progress);
            }
        });

        /* 此过程包括X5内核的下载、预初始化，接入方不需要接管处理x5的初始化流程，希望无感接入 */
        QbSdk.initX5Environment(this, new QbSdk.PreInitCallback() {
            @Override
            public void onCoreInitFinished() {
                // 内核初始化完成，可能为系统内核，也可能为系统内核
                Log.i(TAG, "onCoreInitFinished: 内核初始化完成");
            }

            /**
             * 预初始化结束
             * 由于X5内核体积较大，需要依赖wifi网络下发，所以当内核不存在的时候，默认会回调false，此时将会使用系统内核代替
             * 内核下发请求发起有24小时间隔，卸载重装、调整系统时间24小时后都可重置
             * 调试阶段建议通过 WebView 访问 debugtbs.qq.com -> 安装线上内核 解决
             * @param isX5 是否使用X5内核
             */
            @Override
            public void onViewInitFinished(boolean isX5) {
                UiUtil.showToastSafe("是否x5："+isX5);
                // hint: you can use QbSdk.getX5CoreLoadHelp(context) anytime to get help.
            }
        });

    }

    private void initPlay(IjkPlayerFactory ijkPlayerFactory) {
        VideoViewManager.setConfig(VideoViewConfig.newBuilder()
                //使用使用IjkPlayer解码
                //.setLogEnabled(BuildConfig.DEBUG)
                //使用ExoPlayer解码
                .setPlayerFactory(ijkPlayerFactory)
                //使用MediaPlayer解码
                .build());
    }


    public static Context getContext() {
        return context;
    }


    /**
     * 获取主线程的handler
     */
    public static Handler getMainThreadHandler() {
        return mMainThreadHandler;
    }

    /**
     * 获取主线程ID
     */
    public static int getMainThreadId() {
        return mMainThreadId;
    }


    public static BaseApplication getInstance() {
        return application;
    }


    private boolean startX5WebProcessPreinitService() {
        String currentProcessName = QbSdk.getCurrentProcessName(this);
        // 设置多进程数据目录隔离，不设置的话系统内核多个进程使用WebView会crash，X5下可能ANR
        WebView.setDataDirectorySuffix(QbSdk.getCurrentProcessName(this));
        Log.i(TAG, currentProcessName);
        if (currentProcessName.equals(this.getPackageName())) {
            this.startService(new Intent(this, X5ProcessInitService.class));
            return true;
        }
        return false;
    }

    public List<JsonEntity> getJsonEntities() {
        return jsonEntities;
    }

    public void setJsonEntities(List<JsonEntity> jsonEntities) {
        this.jsonEntities = jsonEntities;
    }

    public List<HandleEntity> getHandleEntities() {
        return handleEntities;
    }

    public void setHandleEntities(List<HandleEntity> handleEntities) {
        this.handleEntities = handleEntities;
    }

    public boolean isSaveProgress() {
        return saveProgress;
    }

    public void setSaveProgress(boolean saveProgress) {
        this.saveProgress = saveProgress;
    }
}
