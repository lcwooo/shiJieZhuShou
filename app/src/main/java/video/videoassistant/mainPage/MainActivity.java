package video.videoassistant.mainPage;

import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.azhon.basic.base.BaseFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
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
        initPage();
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

    @Override
    protected void initData() {
        viewModel.getAdRule();

        test();

        viewModel.versionBeanData.observe(this, new Observer<RuleVersionBean>() {
            @Override
            public void onChanged(RuleVersionBean ruleVersionBean) {
                initAdGuard(ruleVersionBean);
            }
        });
    }

    public void test() {
        String data = "||tamaraboccatelli.com.br^\n" +
                "||tamarispolska.pl.com^\n" +
                "||cc.tamarixeledone.com^\n" +
                "||tamayaservicios.cn^";
        String regx = "(?<=[\\$|\\#]\\{)[\\s\\S]*?(?=\\})";
        String a = "pl.com";
        String regex = "(?<=[\\|\\|]).*(" + a +
                ").*?(?=\\^)";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(data);
        while (matcher.find()) {
            String group = matcher.group();
            Log.i(TAG, "test: " + group);
        }


/*        String str = "sfsfkkvvnn${a1}f尼斯hi放松放松#{a2}fsfsf快速反击${a3} sfsfsfsdfjs士大夫十分 #{a4}ffafsjj sfsf";
        List<String> list = getParmNames(str);
        for (String s : list) {
            Log.i(TAG, "test: " + s);
        }*/
    }

    public static List<String> getParmNames(String sentence) {
        List<String> list = new ArrayList<>();
        //String regx = "(?<=[\\$|\\#]\\{)[\\s\\S]*?(?=\\})";
        String regx = "(?<=[\\$|\\#]\\{)[\\s\\S]*?(?=\\})";
        Pattern pattern = Pattern.compile(regx, Pattern.CASE_INSENSITIVE);
        Matcher m = pattern.matcher(sentence);
        while (m.find()) {
            String searchStr = m.group(0);
            list.add(searchStr);
        }
        return list;

    }


    private void initAdGuard(RuleVersionBean ruleVersionBean) {
        int version = PreferencesUtils.getInt(context, Constant.adRuleVersion, 0);
        if (ruleVersionBean.getVersion() <= version) {
            return;
        }

        String fs = getExternalFilesDir("app").getAbsolutePath();
        Log.i(TAG, "initAdGuard: " + fs);
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
                            initAdList();
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

    public void initAdList() {

    }
}