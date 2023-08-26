package video.videoassistant.mainPage;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.android.cast.dlna.dmc.DLNACastManager;
import com.arialyy.annotations.Download;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.task.DownloadTask;
import com.azhon.basic.base.BaseFragment;
import com.azhon.basic.utils.ActivityUtil;
import com.azhon.basic.utils.TimeUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.jeremyliao.liveeventbus.LiveEventBus;
import com.yanzhenjie.andserver.AndServer;
import com.yanzhenjie.andserver.Server;

import java.io.File;
import java.security.Permissions;
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

public class MainActivity extends BaseActivity<MainModel, ActivityMainBinding>
        implements ViewPager.OnPageChangeListener, BottomNavigationView.OnNavigationItemSelectedListener {


    private MainAdapter mainAdapter;
    private List<BaseFragment> fragments = new ArrayList<>();
    private static final String TAG = "MainActivity";
    private Server server;

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
        //checkPermissions();
        initWeb();
        initPage();
        startServer();
        initProgress();
        test();

    }

    private void initProgress() {
        viewModel.initProgress();
    }

    private void test() {

    }

    private void initWeb() {
        try {
            server = AndServer.webServer(this)
                    .port(8080)
                    .timeout(10, TimeUnit.SECONDS)
                    .listener(new Server.ServerListener() {
                        @Override
                        public void onStarted() {
                            Log.i(TAG, "startWeb(本地服务器启动成功): " + UiUtil.getIPv4Address());
                        }

                        @Override
                        public void onStopped() {
                            Log.i(TAG, "onStopped:本地服务器已经停止");
                        }

                        @Override
                        public void onException(Exception e) {
                            Log.i(TAG, "onStopped:本地服务器异常");
                        }
                    })
                    .build();
        } catch (Exception e) {
            UiUtil.showToastSafe("本地服务端口启动异常");
        }
    }

    public void startServer() {
        if (server.isRunning()) {
            // TODO The server is already up.
        } else {
            Log.i(TAG, "startServer: " + UiUtil.getIPv4Address());
            server.startup();
        }
    }

    public void stopServer() {
        if (server.isRunning()) {
            server.shutdown();
        } else {
            //server.startup();
        }
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

    public void selectPage(int page) {
        dataBinding.page.setCurrentItem(page);
    }

    @Override
    protected void initData() {
        viewModel.getAdRule();


        viewModel.versionBeanData.observe(this, new Observer<RuleVersionBean>() {
            @Override
            public void onChanged(RuleVersionBean ruleVersionBean) {
                initAdGuard(ruleVersionBean);
                initVersion(ruleVersionBean);
                LiveEventBus.get("configApp",RuleVersionBean.class).post(ruleVersionBean);
            }
        });
    }

    private void initVersion(RuleVersionBean ruleVersionBean) {
        if (ruleVersionBean.getAppVersion() > getVersionCode()) {
            showUpdateDialog(ruleVersionBean);
        }
    }


    private void showUpdateDialog(RuleVersionBean ruleVersionBean) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_update, null);
        builder.setView(view);
        TextView info = view.findViewById(R.id.tv_description);
        TextView cancel = view.findViewById(R.id.btn_cancel);
        Button update = view.findViewById(R.id.btn_update);
        info.setText(ruleVersionBean.getDescription());
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(ruleVersionBean.getUpdateUrl()));
                    startActivity(intent);
                } catch (Exception e) {
                    UiUtil.showToastSafe("没有应用可以打开，请检查设备是否安装了浏览器");
                }

            }
        });
        dialog.show();


    }


    public int getVersionCode() {
        int versionCode = 0;
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionCode = pInfo.versionCode;
            // 在此处使用versionCode
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return versionCode;

    }


    private void initAdGuard(RuleVersionBean ruleVersionBean) {
        File fi = getExternalFilesDir("app");
        if (fi == null) {
            UiUtil.showToastSafe("文件系统出错");
            return;
        }
        if (!fi.exists()) {
            fi.mkdirs();
        }
        String fs = getExternalFilesDir("app").getAbsolutePath() + "/" + "adRule.txt";
        File file = new File(fs);
        if (!file.exists()) {
            downRule(ruleVersionBean, fs);
            return;
        }

        int version = PreferencesUtils.getInt(context, Constant.adRuleVersion, 0);
        if (ruleVersionBean.getAdVersion() <= version) {
            return;
        }

        file.delete();

        downRule(ruleVersionBean, fs);
    }

    public void downRule(RuleVersionBean ruleVersionBean, String file) {
        String fs = getExternalFilesDir("app").getAbsolutePath();
        String downName = "adRule.txt";
        new Retrofit.Builder()
                .baseUrl(ApiService.URL)
                .build()
                .create(DownService.class)
                .downloadFile(ruleVersionBean.getAdUrl())//可以是完整的地址，也可以是baseurl后面的动态地址
                .enqueue(new FileCallBack(fs.toString(), downName) {
                    @Override
                    public void onSuccess(File file, Progress progress) {
                        if (progress.status == 5) {
                            UiUtil.showToastSafe("广告拦截库同步完成版本：" + ruleVersionBean.getAdVersion());
                            PreferencesUtils.putInt(context, Constant.adRuleVersion, ruleVersionBean.getAdVersion());
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


    private long exitTime = 0;

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_LONG).show();
                exitTime = System.currentTimeMillis();
            } else {
                PreferencesUtils.putString(this, "copyString", "");
                ActivityUtil.getInstance().finishAllActivity();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}