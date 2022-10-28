package video.videoassistant.base;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import xyz.doikki.videoplayer.BuildConfig;
import xyz.doikki.videoplayer.ijk.IjkPlayerFactory;
import xyz.doikki.videoplayer.player.VideoViewConfig;
import xyz.doikki.videoplayer.player.VideoViewManager;


public class BaseApplication extends Application {

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


    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        application = this;
        mMainThreadHandler = new Handler();
        mMainThreadId = android.os.Process.myTid();
        Log.i("GroupingFragment", "onCreate: " + mMainThreadId);
        initPlay(IjkPlayerFactory.create());
    }

    private void initPlay(IjkPlayerFactory ijkPlayerFactory) {
        VideoViewManager.setConfig(VideoViewConfig.newBuilder()
                //使用使用IjkPlayer解码
                .setLogEnabled(BuildConfig.DEBUG)
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


}
