package video.videoassistant.util;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.azhon.basic.utils.ActivityUtil;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import video.videoassistant.base.BaseApplication;


public class UiUtil {


    /**
     * 在主线程执行runnable
     */
    public static boolean post(Runnable runnable) {
        return getHandler().post(runnable);
    }

    /**
     * 获取主线程的handler
     */
    public static Handler getHandler() {
        return BaseApplication.getMainThreadHandler();
    }

    /**
     * 对toast的简易封装。线程安全，可以在非UI线程调用。
     */
    public static void showToastSafe(final String str) {
        if (isRunInMainThread()) {
            showToast(str);
        } else {
            post(new Runnable() {
                @Override
                public void run() {
                    showToast(str);
                }
            });
        }
    }

    // 判断当前的线程是不是在主线程
    public static boolean isRunInMainThread() {
        return android.os.Process.myTid() == getMainThreadId();
    }

    private static void showToast(String str) {
        Activity a = ActivityUtil.getInstance().getActivity();
        try {
            if (a != null) {
                Toast.makeText(a, str, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {

        }

    }


    public static boolean listIsEmpty(List<?> list) {
        if (list == null || list.size() == 0) {
            return true;
        }
        return false;
    }

    public static long getMainThreadId() {
        return BaseApplication.getMainThreadId();
    }

    public static String getTime() {

        long time = System.currentTimeMillis();//long now = android.os.SystemClock.uptimeMillis();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date d1 = new Date(time);

        String t1 = format.format(d1);

        return t1;

    }


    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int weight(Context mContext) {
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        return width;
    }


    public static String getHttpUrl(String url) {
        if (!url.contains("http")) {
            url = "http://" + url;
        }

        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }

        return url;

    }




}
