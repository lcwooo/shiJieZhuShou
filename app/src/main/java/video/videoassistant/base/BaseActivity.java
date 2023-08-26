package video.videoassistant.base;

import android.content.ClipboardManager;
import android.view.KeyEvent;

import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.Observer;

import com.azhon.basic.bean.DialogBean;
import com.azhon.basic.lifecycle.BaseViewModel;

import video.videoassistant.util.UiUtil;


/**
 * 项目名:    TODO-MVVM
 * 包名       com.azhon.basic.base
 * 文件名:    BaseActivity
 * 创建时间:  2019-03-27 on 10:46
 * 描述:     TODO ViewModel、ViewDataBinding都需要的基类
 *
 * @author 阿钟
 */

public abstract class BaseActivity<VM extends BaseViewModel, DB extends ViewDataBinding>
        extends BaseNoModelActivity<DB> {

    protected VM viewModel;
    ClipboardManager manager;

    @Override
    protected DB initDataBinding(int layoutId) {
        DB db = super.initDataBinding(layoutId);
        /**
         * 将这两个初始化函数插在{@link com.chineni.movies.base.BaseActivity#initDataBinding}
         */
        viewModel = initViewModel();
        initFinish();
        initObserve();
        return db;
    }

    private void initFinish() {
        viewModel.finish.observe(this, new Observer<Void>() {
            @Override
            public void onChanged(Void aVoid) {
                finish();
            }
        });
    }

    /**
     * 初始化ViewModel
     */
    protected abstract VM initViewModel();

    /**
     * 监听当前ViewModel中 showDialog和error的值
     */
    private void initObserve() {
        if (viewModel == null) return;
        viewModel.getShowDialog(this, new Observer<DialogBean>() {
            @Override
            public void onChanged(DialogBean bean) {
                if (bean.isShow()) {
                    showDialog(bean.getMsg(), bean.isCan());
                } else {
                    dismissDialog();
                }
            }
        });
        viewModel.getError(this, new Observer<Object>() {
            @Override
            public void onChanged(Object obj) {
                showError(obj);
            }
        });
    }

    private void showError(Object obj) {
        UiUtil.showToastSafe(obj.toString());
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }




}
