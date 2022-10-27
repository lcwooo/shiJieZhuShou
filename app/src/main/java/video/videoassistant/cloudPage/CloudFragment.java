package video.videoassistant.cloudPage;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.azhon.basic.base.BaseFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

import video.videoassistant.R;
import video.videoassistant.databinding.FragmentCloudBinding;
import video.videoassistant.me.jointManage.JointEntity;

public class CloudFragment extends BaseFragment<CloudModel, FragmentCloudBinding> {

    private List<BaseFragment> pages = new ArrayList<>();
    private List<String> titles = new ArrayList<>();
    private int activeSize = 18;
    private int normalSize = 14;
    private TabLayoutMediator mediator;
    private int activeColor = Color.parseColor("#3E3838");
    private int normalColor = Color.parseColor("#666666");

    @Override
    protected CloudModel initViewModel() {
        return new ViewModelProvider(this).get(CloudModel.class);
    }

    @Override
    protected void showError(Object obj) {

    }

    @Override
    protected int onCreate() {
        return R.layout.fragment_cloud;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

        viewModel.getAll();

        viewModel.listJoint.observe(this, new Observer<List<JointEntity>>() {
            @Override
            public void onChanged(List<JointEntity> jointEntities) {
                titles.clear();
                pages.clear();
                initPage(jointEntities);
            }
        });
    }

    private void initPage(List<JointEntity> jointEntities) {
        ViewPager2 viewPager = dataBinding.viewPager;
        TabLayout tabLayout = dataBinding.tabLayout;
        tabLayout.setSelectedTabIndicatorHeight(0);
        viewPager.setOffscreenPageLimit(3);
        for (JointEntity bean : jointEntities) {
            pages.add(CloudListFragment.newInstance(bean.getUrl(),bean.getType()));
            titles.add(bean.getName());
        }
        viewPager.registerOnPageChangeCallback(changeCallback);
        MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(getActivity());
        viewPager.setAdapter(adapter);
        initSelect(tabLayout, viewPager);
    }

    private ViewPager2.OnPageChangeCallback changeCallback =
            new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    //可以来设置选中时tab的大小
                    int tabCount = dataBinding.tabLayout.getTabCount();
                    for (int i = 0; i < tabCount; i++) {
                        TabLayout.Tab tab = dataBinding.tabLayout.getTabAt(i);
                        TextView tabView = (TextView) tab.getCustomView();
                        tabView.setGravity(Gravity.CENTER);
                        if (tab.getPosition() == position) {
                            tabView.setTextSize(activeSize);
                            tabView.setTypeface(Typeface.DEFAULT_BOLD);
                        } else {
                            tabView.setTextSize(normalSize);
                            tabView.setTypeface(Typeface.DEFAULT);
                        }
                    }
                }
            };

    public class MyFragmentPagerAdapter extends FragmentStateAdapter {

        public MyFragmentPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @Override
        public Fragment createFragment(int position) {
            return pages.get(position);
        }

        @Override
        public int getItemCount() {
            return pages.size();
        }
    }


    private void initSelect(TabLayout tabLayout, ViewPager2 viewPager) {

        mediator = new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                //这里可以自定义TabView
                TextView tabView = new TextView(context);
                tabView.setGravity(Gravity.CENTER);
                int[][] states = new int[2][];
                states[0] = new int[]{android.R.attr.state_selected};
                states[1] = new int[]{};
                int[] colors = new int[]{activeColor, normalColor};
                ColorStateList colorStateList = new ColorStateList(states, colors);
                tabView.setText(titles.get(position));
                tabView.setTextSize(normalSize);
                tabView.setTextColor(colorStateList);
                tab.setCustomView(tabView);
            }
        });
        //要执行这一句才是真正将两者绑定起来
        mediator.attach();
    }
}
