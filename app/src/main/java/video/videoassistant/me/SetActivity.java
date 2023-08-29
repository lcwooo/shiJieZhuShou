package video.videoassistant.me;

import android.app.AlertDialog;
import android.content.DialogInterface;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import video.videoassistant.R;
import video.videoassistant.base.BaseActivity;
import video.videoassistant.databinding.ActivitySetBinding;
import video.videoassistant.util.Constant;
import video.videoassistant.util.PreferencesUtils;
import video.videoassistant.util.UiUtil;

public class SetActivity extends BaseActivity<MeModel, ActivitySetBinding> {


    @Override
    protected MeModel initViewModel() {
        return new ViewModelProvider(this).get(MeModel.class);
    }

    @Override
    protected int onCreate() {
        return R.layout.activity_set;
    }

    @Override
    protected void initView() {
        dataBinding.setModel(viewModel);
        String host = PreferencesUtils.getString(context, Constant.hostUrl, "");
        if (!host.isEmpty()) {
            viewModel.editTextValue = host;
        }

        viewModel.imageViewClicked.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    showTips();
                }
            }
        });

        viewModel.hostSave.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (viewModel.editTextValue == null || viewModel.editTextValue.trim().isEmpty()) {
                    UiUtil.showToastSafe("你已设置为空,将不再收到APP更新和广告拦截库更新");
                    PreferencesUtils.putString(context, Constant.hostUrl, "");
                    return;
                }

                if (!isUrl(viewModel.editTextValue)) {
                    UiUtil.showToastSafe("请输入正确的网址");
                    return;
                }
                PreferencesUtils.putString(context, Constant.hostUrl, viewModel.editTextValue);
                UiUtil.showToastSafe("保存成功" + viewModel.editTextValue);
            }
        });
    }

    private void showTips() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("这个包含广告拦截库的更新，以及APP的更新，请不要随意修改，否则可能会导致APP无法使用！如果您不想收到APP更新请清空数据保存即可。")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击确定按钮后的逻辑处理
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();


    }

    @Override
    protected void initData() {

    }

    //判断是否是网址
    public static boolean isUrl(String url) {
        if (url == null || url.trim().length() == 0) {
            return false;
        }
        return url.matches("^(http|https)://.*$");
    }
}
