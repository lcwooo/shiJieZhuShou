package video.videoassistant.me;

import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.azhon.basic.base.BaseFragment;
import com.jeremyliao.liveeventbus.LiveEventBus;

import java.util.List;
import java.util.Random;

import video.videoassistant.R;
import video.videoassistant.about.AboutActivity;
import video.videoassistant.bookmarkAndHistory.BookmarkHistoryActivity;
import video.videoassistant.browserPage.BrowserActivity;
import video.videoassistant.collectPage.CollectActivity;
import video.videoassistant.databinding.FragmentMeBinding;
import video.videoassistant.importAndExport.ManageActivity;
import video.videoassistant.mainPage.OtherBean;
import video.videoassistant.mainPage.RuleVersionBean;
import video.videoassistant.me.handleManage.HandleActivity;
import video.videoassistant.me.handleManage.HandleAdapter;
import video.videoassistant.me.jointManage.JointManageActivity;
import video.videoassistant.me.jsonManage.JsonActivity;
import video.videoassistant.me.urlManage.UrlManageActivity;
import video.videoassistant.util.Constant;
import video.videoassistant.util.PreferencesUtils;
import video.videoassistant.util.UiUtil;


public class MeFragment extends BaseFragment<MeModel, FragmentMeBinding> {
    private static final String TAG = "MeFragment";

    @Override
    protected MeModel initViewModel() {
        return new ViewModelProvider(this).get(MeModel.class);
    }

    @Override
    protected void showError(Object obj) {

    }

    @Override
    protected int onCreate() {
        return R.layout.fragment_me;
    }

    @Override
    protected void initView() {
        dataBinding.setView(this);
    }

    @Override
    protected void initData() {
        LiveEventBus.get("configApp", RuleVersionBean.class).observe(this, new Observer<RuleVersionBean>() {
            @Override
            public void onChanged(RuleVersionBean ruleVersionBean) {

                if (UiUtil.listIsEmpty(ruleVersionBean.getOtherBeans())) {
                    return;
                }
                initOther(ruleVersionBean.getOtherBeans());
            }
        });
    }

    private void initOther(List<OtherBean> otherBeans) {
        //dataBinding.add.removeAllViews();
        for (OtherBean bean : otherBeans) {
            View view = View.inflate(context, R.layout.me_other, null);
            TextView tittle = view.findViewById(R.id.title);
            Log.i(TAG, "initOther: " + bean.getTittle());
            tittle.setText(bean.getTittle());
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickOther(bean);
                }
            });
            dataBinding.add.addView(view);
        }
    }

    private void clickOther(OtherBean bean) {
        switch (bean.getType()) {
            case 1:
                Intent intent = new Intent(context, BrowserActivity.class);
                intent.putExtra("url", bean.getData());
                context.startActivity(intent);
                break;

            case 2:
                joinGroupTips(bean);
                break;

            case 3:
                try {
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(bean.getData()));
                    startActivity(i);
                } catch (Exception e) {
                    UiUtil.showToastSafe("没有应用可以打开，请检查设备是否安装了浏览器");
                }
                break;
        }
    }

    private void joinGroupTips(OtherBean bean) {
        String[] arr = bean.getData().split("&");
        new AlertDialog.Builder(context)
                .setTitle("提醒")
                .setMessage("有问题可以群里反馈，有时间就会回复。不要在里面发违法内容。" + "\n\n" + "加群验证码:" + arr[1])
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("加入QQ群", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // 在此处添加确认操作
                        copyUrl(arr[1]);
                        joinQQGroup(arr[0]);
                    }
                })
                .setNegativeButton("", null).show();


    }


    public void joinQQGroup(String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26jump_from%3Dwebapi%26k%3D" + key));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivity(intent);
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            UiUtil.showToastSafe("请先安装QQ");
        }
    }


    public void urlManage() {
        toActivity(UrlManageActivity.class);
    }

    public void jointManage() {
        toActivity(JointManageActivity.class);
    }

    public void jsonManage() {
        toActivity(JsonActivity.class);
    }

    public void handleManage() {
        toActivity(HandleActivity.class);
    }

    public void manager() {
        toActivity(ManageActivity.class);
    }

    public void collectManager(int state) {
        Intent intent = new Intent(context, CollectActivity.class);
        intent.putExtra("page", state);
        startActivity(intent);
    }

    public void shuqian() {
        toActivity(BookmarkHistoryActivity.class);
    }

    public void aboutApp() {
        toActivity(AboutActivity.class);
    }

    public void maxRequest() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("选择最大请求量(不宜过大)");

        final NumberPicker numberPicker = new NumberPicker(getActivity());
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(15);
        // 设置 NumberPicker 的初始值
        numberPicker.setValue(5);
        builder.setView(numberPicker);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int selectedNum = numberPicker.getValue();
                PreferencesUtils.putInt(context, Constant.searchMaxQuantity, selectedNum);
            }
        });

        builder.setNegativeButton("取消", null);
        builder.show();
    }

    public void copyUrl(String url) {
        ClipboardManager cm = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setText(url);
        UiUtil.showToastSafe("已复制验证码");
    }
}
