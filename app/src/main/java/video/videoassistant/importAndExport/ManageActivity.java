package video.videoassistant.importAndExport;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.jeremyliao.liveeventbus.LiveEventBus;
import com.zlylib.fileselectorlib.FileSelector;
import com.zlylib.fileselectorlib.bean.EssFile;
import com.zlylib.fileselectorlib.utils.Const;

import org.apache.httpcore.util.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import video.videoassistant.R;
import video.videoassistant.base.BaseActivity;
import video.videoassistant.databinding.ActivityManageBinding;
import video.videoassistant.util.Constant;
import video.videoassistant.util.UiUtil;

public class ManageActivity extends BaseActivity<ManageModel, ActivityManageBinding> {

    private static final String TAG = "ManageActivity";
    boolean is = false;

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


    public void showTips() {
        new AlertDialog.Builder(context)
                .setTitle("提醒")
                .setMessage("由于app需要导入导出文件所以app需要内存卡权限，请放心使用。APP本身不会抓取任何内容，只用来导入和导出txt和json文件" +
                        "内容。如果您担心您可以拒绝，拒绝后数据本地导出和导入将无法使用。")
                .setNegativeButton("去授权", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showCheck();
                    }
                })
                .setNeutralButton("拒绝使用", null).show();
    }

    private void showCheck() {

        XXPermissions.with(this)
                // 申请单个权限
                .permission(Permission.Group.STORAGE)
                .request(new OnPermissionCallback() {

                    @Override
                    public void onGranted(List<String> permissions, boolean all) {
                        UiUtil.showToastSafe("获取权限成功");
                    }

                    @Override
                    public void onDenied(List<String> permissions, boolean never) {
                        if (never) {
                            UiUtil.showToastSafe("被永久拒绝存储授权,APP将无法使用,请给与权限");
                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
                            XXPermissions.startPermissionActivity(context, permissions);
                        } else {
                            UiUtil.showToastSafe("获取权限失败");
                        }

                    }
                });
    }


    @Override
    protected void initData() {

        viewModel.exportFile();

        viewModel.json.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (!s.contains("webDy") && !s.contains("jsonDy")
                        && !s.contains("jsonJx") && !s.contains("webJx")) {
                    UiUtil.showToastSafe("解析异常,请检查您导入的数据是否规范");
                    return;
                }

                try {
                    ManagerBean bean = JSON.parseObject(s, ManagerBean.class);
                    initManagerBean(bean);
                } catch (JSONException e) {
                    UiUtil.showToastSafe("json解析异常,请检查您导入的数据是否规范");
                }
            }
        });

        viewModel.isComplete.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                UiUtil.showToastSafe("导入完成");
                LiveEventBus.get(Constant.urlChange, String.class).post("");
                LiveEventBus.get(Constant.jointChange, String.class).post("");
            }
        });

    }

    private void initManagerBean(ManagerBean bean) {
        viewModel.importData(bean);
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

    public boolean checkMyAppPermission() {
        // android6.0 API 23后需要动态申请权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int checkRs = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (checkRs == 0) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    public void exportTxt() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        EditText et = new EditText(context);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        et.setPadding(10, 10, 10, 10);
        et.setLayoutParams(layoutParams);
        et.setTextSize(13f);
        et.setHint("请输入文本内容,请输入json格式");
        et.setSingleLine(true);
        et.setBackground(getResources().getDrawable(R.drawable.shap_analysis_back));
        builder.setView(et);
        builder.setNeutralButton("取消", null);
        builder.setNegativeButton("导入资源", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (et.getText().toString().length() > 0) {
                    viewModel.json.postValue(et.getText().toString());
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    public void exportFile() {


        if (!checkMyAppPermission()) {
            showTips();
            return;
        }


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
        String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/" + Environment.DIRECTORY_DOWNLOADS + "/video.videoAssistant/dataBackup";
        File fi = new File(absolutePath);
        if (!fi.exists()) {
            fi.mkdirs();
        }
        String fs = absolutePath + "/" + time + ".txt";
        Log.i(TAG, "exportFile: " + fs);
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
            UiUtil.showToastSafe("生成备份成功,数据备份在 android/Download/video.videoAssistant/dataBackup 目录下面");
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

    public void locationFile() {


        /**
         *  设置 onlyShowFolder() 只显示文件夹 后 再设置setFileTypes（）不生效
         *  设置 onlyShowFolder() 只显示文件夹 后 默认设置了onlySelectFolder（）
         *  设置 onlySelectFolder() 只能选择文件夹 后 默认设置了isSingle（）
         *  设置 isSingle() 只能选择一个 后 再设置了setMaxCount（） 不生效
         *
         */
        FileSelector.from(this)
                // .onlyShowFolder()  //只显示文件夹
                //.onlySelectFolder()  //只能选择文件夹
                // .isSingle() // 只能选择一个
                .setMaxCount(1) //设置最大选择数
                .setFileTypes("txt", "json") //设置文件类型
                .setSortType(FileSelector.BY_NAME_ASC) //设置名字排序
                //.setSortType(FileSelector.BY_TIME_ASC) //设置时间排序
                //.setSortType(FileSelector.BY_SIZE_DESC) //设置大小排序
                //.setSortType(FileSelector.BY_EXTENSION_DESC) //设置类型排序
                .requestCode(1) //设置返回码
                .setTargetPath("/storage/emulated/0/") //设置默认目录
                .start();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (data != null) {
                ArrayList<EssFile> essFileList = data.getParcelableArrayListExtra(Const.EXTRA_RESULT_SELECTION);
                importData(essFileList.get(0).getAbsolutePath());
            }
        }
    }

    public void importData(String absolutePath) {
        if (absolutePath.endsWith(".txt") || absolutePath.endsWith(".json")) {
            String json = openText(absolutePath);
            if (TextUtils.isEmpty(json)) {
                UiUtil.showToastSafe("文件没有数据");
            } else {
                viewModel.json.postValue(json);
            }
        } else {
            UiUtil.showToastSafe("只支持txt和json文件的导入");
        }
    }


    // 从指定路径的文本文件中读取内容字符串 输入流
    public String openText(String path) {
        BufferedReader is = null;
        StringBuilder sb = new StringBuilder();
        try {
            is = new BufferedReader(new FileReader(path));
            String line = null;
            // 行读取
            while ((line = is.readLine()) != null) {
                sb.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }


}
