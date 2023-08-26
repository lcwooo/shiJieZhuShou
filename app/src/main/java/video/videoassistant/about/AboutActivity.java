package video.videoassistant.about;

import android.content.Intent;
import android.net.Uri;
import android.view.View;

import video.videoassistant.BuildConfig;
import video.videoassistant.R;
import video.videoassistant.base.BaseActivity;
import video.videoassistant.base.BaseNoModelActivity;
import video.videoassistant.databinding.ActivityAboutBinding;
import video.videoassistant.util.UiUtil;

public class AboutActivity extends BaseNoModelActivity<ActivityAboutBinding> {
    @Override
    protected int onCreate() {
        return R.layout.activity_about;
    }

    @Override
    protected void initView() {
        initTittle("关于APP");

        dataBinding.version.setText("软件版本："+ BuildConfig.VERSION_NAME);
        dataBinding.address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/lcwooo/shiJieZhuShou"));
                    startActivity(intent);
                } catch (Exception e) {
                    UiUtil.showToastSafe("没有应用可以打开，请检查设备是否安装了浏览器");
                }
            }
        });
    }

    @Override
    protected void initData() {

    }
}
