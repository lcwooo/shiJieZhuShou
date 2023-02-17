package video.videoassistant.mainPage;

import android.Manifest;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.android.cast.dlna.dmc.DLNACastManager;
import com.arialyy.annotations.Download;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.task.DownloadTask;
import com.azhon.basic.base.BaseFragment;
import com.azhon.basic.utils.TimeUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.XXPermissions;
import com.yanzhenjie.andserver.AndServer;
import com.yanzhenjie.andserver.Server;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import video.videoassistant.R;
import video.videoassistant.base.BaseActivity;
import video.videoassistant.cloudPage.CloudFragment;
import video.videoassistant.databinding.ActivityMainBinding;
import video.videoassistant.indexPage.IndexFragment;
import video.videoassistant.me.MeFragment;
import video.videoassistant.net.ApiService;
import video.videoassistant.store.StoreFragment;
import video.videoassistant.util.Constant;
import video.videoassistant.util.PreferencesUtils;
import video.videoassistant.util.UiUtil;

public class MainActivity extends BaseActivity<MainModel, ActivityMainBinding> implements ViewPager.OnPageChangeListener, BottomNavigationView.OnNavigationItemSelectedListener {


    private MainAdapter mainAdapter;
    private List<BaseFragment> fragments = new ArrayList<>();
    private static final String TAG = "MainActivity";

    @Override
    protected MainModel initViewModel() {
        return new ViewModelProvider(this).get(MainModel.class);
    }

    @Override
    protected int onCreate() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        startWeb();
        initPage();
    }

    private void startWeb() {
        try {
            Server server = AndServer.webServer(this)
                    .port(8080)
                    .timeout(10, TimeUnit.SECONDS)
                    .build();
            server.startup();
        } catch (Exception e) {
            e.printStackTrace();
            UiUtil.showToastSafe("本地服务端口启动异常");
        }
    }

    private void checkPermissions() {
        XXPermissions.with(this)
                // 申请单个权限
                .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .request(new OnPermissionCallback() {

                    @Override
                    public void onGranted(List<String> permissions, boolean all) {

                    }

                    @Override
                    public void onDenied(List<String> permissions, boolean never) {
                        if (never) {
                            UiUtil.showToastSafe("被永久拒绝存储授权,APP将无法使用,请给与权限");
                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
                            XXPermissions.startPermissionActivity(context, permissions);
                            return;
                        }
                        if (permissions.contains(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            UiUtil.showToastSafe("获取权限成功");
                        }
                    }
                });
    }

    private void initPage() {
        fragments.add(new IndexFragment());
        fragments.add(new CloudFragment());
        fragments.add(new StoreFragment());
        fragments.add(new MeFragment());

        mainAdapter = new MainAdapter(getSupportFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mainAdapter.addFragment(fragments);
        dataBinding.page.setAdapter(mainAdapter);
        dataBinding.page.setOffscreenPageLimit(mainAdapter.getCount());
        dataBinding.page.addOnPageChangeListener(this);
        dataBinding.navigation.setOnNavigationItemSelectedListener(this);
    }

    public void selectPage(int page){
        dataBinding.page.setCurrentItem(page);
    }

    @Override
    protected void initData() {
        viewModel.getAdRule();


        viewModel.versionBeanData.observe(this, new Observer<RuleVersionBean>() {
            @Override
            public void onChanged(RuleVersionBean ruleVersionBean) {
                initAdGuard(ruleVersionBean);
            }
        });
    }


    private void initAdGuard(RuleVersionBean ruleVersionBean) {
        File fi = getExternalFilesDir("app");
        if(fi==null){
            UiUtil.showToastSafe("文件系统出错");
            return;
        }
        String fs = getExternalFilesDir("app").getAbsolutePath() + "/" + "adRule.txt";
        File file = new File(fs);
        if (!file.exists()) {
            downRule(ruleVersionBean, fs);
            return;
        }
        int version = PreferencesUtils.getInt(context, Constant.adRuleVersion, 0);
        if (ruleVersionBean.getVersion() <= version) {
            return;
        }

        downRule(ruleVersionBean, fs);
    }

    public void downRule(RuleVersionBean ruleVersionBean, String file) {
/*        UiUtil.showToastSafe("开始下载");
        Aria.download(this)
                .load(ruleVersionBean.getUrl())
                .setFilePath(fs)
                .create();*/
        String fs = getExternalFilesDir("app").getAbsolutePath();
        String downName = "adRule.txt";
        new Retrofit.Builder()
                .baseUrl(ApiService.URL)
                .build()
                .create(DownService.class)
                .downloadFile(ruleVersionBean.getUrl())//可以是完整的地址，也可以是baseurl后面的动态地址
                .enqueue(new FileCallBack(fs.toString(), downName) {
                    @Override
                    public void onSuccess(File file, Progress progress) {
                        if (progress.status == 5) {
                            UiUtil.showToastSafe("广告拦截库同步完成版本：" + ruleVersionBean.getVersion());
                            PreferencesUtils.putInt(context, Constant.adRuleVersion, ruleVersionBean.getVersion());
                        }
                    }

                    @Override
                    public void onProgress(Progress progress) {

                    }

                    @Override
                    public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {

                    }
                });
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                dataBinding.navigation.setSelectedItemId(R.id.index);
                break;
            case 1:
                dataBinding.navigation.setSelectedItemId(R.id.cloud);
                break;
            case 2:
                dataBinding.navigation.setSelectedItemId(R.id.store);
                break;
            case 3:
                dataBinding.navigation.setSelectedItemId(R.id.me);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.index:
                dataBinding.page.setCurrentItem(0, false);
                return true;
            case R.id.cloud:
                dataBinding.page.setCurrentItem(1, false);
                return true;
            case R.id.store:
                dataBinding.page.setCurrentItem(2, false);
                return true;
            case R.id.me:
                dataBinding.page.setCurrentItem(3, false);
                return true;
        }
        return false;

    }


    //在这里处理任务完成的状态
    @Download.onTaskComplete
    void taskComplete(DownloadTask task) {
        if (task.getKey().equals(viewModel.versionBeanData.getValue().getUrl())) {
            UiUtil.showToastSafe("广告拦截库初始化完成");
        }
    }

    @Download.onTaskFail
    void taskFail(DownloadTask task) {
        UiUtil.showToastSafe("下载出错：" + task.getKey());
    }

    //在这里处理任务执行中的状态，如进度进度条的刷新
    @Download.onTaskRunning
    protected void running(DownloadTask task) {
        Log.i(TAG, "running: " + task.getPercent());
    }


}