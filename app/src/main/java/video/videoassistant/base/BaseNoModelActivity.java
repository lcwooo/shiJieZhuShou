package video.videoassistant.base;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.azhon.basic.navigationbar.DefaultNavigationBar;
import com.azhon.basic.utils.ActivityUtil;
import com.azhon.basic.view.LoadingDialog;

import video.videoassistant.util.UiUtil;


/**
 * 项目名:    TODO-MVVM
 * 包名       com.azhon.basic.base
 * 文件名:    BaseNoModelActivity
 * 创建时间:  2019-03-28 on 10:28
 * 描述:     TODO 不需要ViewModel的页面基类
 *
 * @author 阿钟
 */

public abstract class BaseNoModelActivity<DB extends ViewDataBinding> extends AppCompatActivity {

    protected DB dataBinding;
    protected Context context;
    private LoadingDialog loadingDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        ActivityUtil.getInstance().addActivity(this);
        int layoutId = onCreate();
        try {
            setContentView(layoutId);
        } catch (Exception e) {

        }
        dataBinding = initDataBinding(layoutId);
        initView();
        initData();
    }




    @Override
    protected void onResume() {
        super.onResume();
    }


    /**
     * 初始化要加载的布局资源ID
     * 此函数优先执行于onCreate()可以做window操作
     */
    protected abstract int onCreate();


    /**
     * 初始化DataBinding
     */
    protected DB initDataBinding(@LayoutRes int layoutId) {
        return DataBindingUtil.setContentView(this, layoutId);
    }

    /**
     * 初始化视图
     */
    protected abstract void initView();

    /**
     * 初始化数据
     */
    protected abstract void initData();

    /**
     * 显示用户等待框
     *
     * @param msg 提示信息
     */
    protected void showDialog(String msg, boolean isCan) {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.setLoadingMsg(msg);
        } else {
            loadingDialog = new LoadingDialog(context);
            loadingDialog.setLoadingMsg(msg);
            loadingDialog.setCancelable(isCan);
            loadingDialog.show();
        }
    }

    /**
     * 隐藏等待框
     */
    protected void dismissDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dataBinding != null) {
            dataBinding.unbind();
        }
        ActivityUtil.getInstance().removeActivity(this);
    }


    public void toActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }

    public void initTittle(String tittle) {
        new DefaultNavigationBar.Builder(this)
                .setTitle(tittle)
                .builder();
    }

    public void initTittle(String tittle, String right, View.OnClickListener clickListener){

        new DefaultNavigationBar.Builder(this)
                .setTitle(tittle)
                .setRightText(right)
                .setRightClickListener(clickListener)
                .builder();

    }

    public void copyUrl(String url) {
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setText(url);
        UiUtil.showToastSafe("已复制");
    }




}
