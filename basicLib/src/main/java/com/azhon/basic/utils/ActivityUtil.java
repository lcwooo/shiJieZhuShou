package com.azhon.basic.utils;

import android.app.Activity;

import java.util.Stack;

/**
 * 项目名:    TODO-MVVM
 * 包名       com.azhon.basic.utils
 * 文件名:    ActivityUtil
 * 创建时间:  2019-03-29 on 17:13
 * 描述:     TODO 管理所有Activity的实例
 *
 * @author 阿钟
 */

public class ActivityUtil {

    private static Stack<Activity> stack;
    private static ActivityUtil manager;

    /**
     * 获取实例
     */
    public static synchronized ActivityUtil getInstance() {
        if (manager == null) {
            manager = new ActivityUtil();
            stack = new Stack<>();
        }
        return manager;
    }

    /**
     * 得到前台Activity
     */
    public synchronized Activity getActivity() {
        try {
            return stack.get(stack.size() - 1);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 添加Activity
     */
    public synchronized void addActivity(Activity activity) {
        stack.add(activity);
    }

    /**
     * 移除Activity
     */
    public synchronized void removeActivity(Activity activity) {
        stack.remove(activity);
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        for (Activity activity : stack) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
                return;
            }
        }
    }


    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            activity.finish();
            stack.remove(activity);
        }
    }

    public static Stack<Activity> getStack() {
        return stack;
    }

    /**
     * 是否存在Activity
     */
    public boolean containsActivity(Class<?> cls) {
        for (Activity activity : stack) {
            if (activity.getClass().equals(cls)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        for (Activity activity : stack) {
            if (activity != null) {
                activity.finish();
            }
        }
        stack.clear();
    }

    public void homeClear() {
        for (Activity activity : stack) {
            if (!activity.getLocalClassName().contains("MainActivity")) {
                activity.finish();
            }
        }
    }
}
