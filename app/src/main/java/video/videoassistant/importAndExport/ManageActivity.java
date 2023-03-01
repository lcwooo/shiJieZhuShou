package video.videoassistant.importAndExport;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.lifecycle.ViewModelProvider;

import org.apache.httpcore.util.TextUtils;

import java.util.ArrayList;
import java.util.List;

import video.videoassistant.R;
import video.videoassistant.base.BaseActivity;
import video.videoassistant.databinding.ActivityManageBinding;
import video.videoassistant.util.UiUtil;

public class ManageActivity extends BaseActivity<ManageModel, ActivityManageBinding> {
    @Override
    protected ManageModel initViewModel() {
        return new ViewModelProvider(this).get(ManageModel.class);
    }

    @Override
    protected int onCreate() {
        return R.layout.activity_manage;
    }

    @Override
    protected void initView() {
        dataBinding.setView(this);
        initTittle("资源的导入和导出");
    }

    @Override
    protected void initData() {

    }

    public void urlImport() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        EditText et = new EditText(context);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        et.setPadding(10, 10, 10, 10);
        et.setLayoutParams(layoutParams);
        et.setTextSize(13f);
        et.setHint("请输入url链接地址");
        et.setSingleLine(true);
        et.setBackground(getResources().getDrawable(R.drawable.shap_analysis_back));
        builder.setView(et);
        builder.setNeutralButton("取消", null);
        builder.setNegativeButton("导入资源", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                importUrl(et.getText().toString());
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void importUrl(String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        if (!text.contains("http")) {
            UiUtil.showToastSafe("网址不正确,必须包含http");
            return;
        }
        UiUtil.showToastSafe(text);
    }

    public void exportFile() {


        viewModel.exportFile();
    }
}
