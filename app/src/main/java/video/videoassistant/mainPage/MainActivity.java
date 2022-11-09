package video.videoassistant.mainPage;

import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.azhon.basic.base.BaseFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
        //initAdGuard();
    }

    private void initAdGuard() {
        String fs = getExternalFilesDir("app").getAbsolutePath();
        Log.i(TAG, "initAdGuard: "+fs);
        String downName = "adRule.txt";
        new Retrofit.Builder()
                .baseUrl(ApiService.URL)
                .build()
                .create(DownService.class)
                .downloadFile(Constant.adGuard)//可以是完整的地址，也可以是baseurl后面的动态地址
                .enqueue(new FileCallBack(fs.toString(), downName) {
                    @Override
                    public void onSuccess(File file, Progress progress) {
                        if (progress.status == 5) {
                            UiUtil.showToastSafe("下载完成");
                        }
                    }

                    @Override
                    public void onProgress(Progress progress) {
                        Log.i(TAG, "onProgress: " + progress.filePath);
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
}