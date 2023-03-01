package video.videoassistant.importAndExport;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.lifecycle.ViewModelProvider;

import com.alibaba.fastjson.JSON;
import com.google.android.exoplayer2.text.tx3g.Tx3gDecoder;

import org.apache.httpcore.util.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import video.videoassistant.R;
import video.videoassistant.base.BaseActivity;
import video.videoassistant.databinding.ActivityManageBinding;
import video.videoassistant.util.UiUtil;

public class ManageActivity extends BaseActivity<ManageModel, ActivityManageBinding> {

    private static final String TAG = "ManageActivity";

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
        viewModel.exportFile();
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

        if (!text.endsWith(".txt") && !text.endsWith(".json")) {
            UiUtil.showToastSafe("格式错误,只支持txt文件和json文件的导入");
            return;
        }

        viewModel.getUrlData(text);
    }

    public void exportFile() {
        ManagerBean managerBean = viewModel.managerBeanMutable.getValue();
        if (managerBean == null) {
            UiUtil.showToastSafe("数据库出错");
            return;
        }
        String txt = JSON.toJSONString(managerBean);
        if (TextUtils.isEmpty(txt) || txt.length() < 8) {
            UiUtil.showToastSafe("无法备份，没有数据");
            return;
        }


        String time = UiUtil.getTime();


        File fi = getExternalFilesDir("dataBackup");
        if (fi == null) {
            UiUtil.showToastSafe("文件系统出错");
            return;
        }
        if (!fi.exists()) {
            fi.mkdirs();
        }
        String fs = getExternalFilesDir("dataBackup").getAbsolutePath() + "/" + time + ".txt";

        try {
            File file = new File(fs);
            if (!file.exists()) {
                file.createNewFile();
            } else {
                file.delete();
            }
            FileOutputStream outputStream = new FileOutputStream(fs);
            OutputStreamWriter writer = new OutputStreamWriter(
                    outputStream, "UTF-8");
            UiUtil.showToastSafe("生成备份成功");
            try {
                writer.write(txt);
            } finally {
                writer.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void exportLocation() {

    }

    public void openLocation() {
        try {
            File fi = getExternalFilesDir("dataBackup");
            File[] list = fi.listFiles();
            for (File file : list) {
                Log.i(TAG, "openLocation: " + file.toString()

                );
            }

        } catch (Exception e) {
            UiUtil.showToastSafe("没有应用可以打开");
            throw new RuntimeException(e);

        }
    }

    public void copyLocation() {
        ManagerBean managerBean = viewModel.managerBeanMutable.getValue();
        if (managerBean == null) {
            UiUtil.showToastSafe("数据库出错");
            return;
        }
        String txt = JSON.toJSONString(managerBean);
        if (TextUtils.isEmpty(txt) || txt.length() < 8) {
            UiUtil.showToastSafe("没有数据");
            return;
        }
        copyUrl(JSON.toJSONString(managerBean));
    }
}
