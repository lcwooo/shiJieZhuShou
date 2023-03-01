package video.videoassistant.bookmarkAndHistory;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

import video.videoassistant.R;
import video.videoassistant.base.BaseActivity;
import video.videoassistant.databinding.AcitivyMarkBinding;

public class BookmarkHistoryActivity extends BaseActivity<Model, AcitivyMarkBinding> {


    public List<MarkFragment> list = new ArrayList<>();
    private List<String> tittle = new ArrayList<>();
    private int activeSize = 14;
    private int normalSize = 14;
    private int activeColor = Color.parseColor("#ff678f");
    private int normalColor = Color.parseColor("#666666");
    private TabLayoutMediator mediator;
    @Override
    protected Model initViewModel() {
        return new ViewModelProvider(this).get(Model.class);
    }

    @Override
    protected int onCreate() {
        return R.layout.acitivy_mark;
    }

    @Override
    protected void initView() {
        tittle.add("书签");
        tittle.add("历史记录");
        ViewPager2 viewPager = dataBinding.viewPager;
        TabLayout tabLayout = dataBinding.tabLayout;
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        list.add(MarkFragment.newInstance(1));
        list.add(MarkFragment.newInstance(2));
        viewPager.registerOnPageChangeCallback(changeCallback);
        MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(this);
        viewPager.setAdapter(adapter);
        initSelect(tabLayout, viewPager);
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
                tabView.setText(tittle.get(position));
                tabView.setTextSize(normalSize);
                tabView.setTextColor(colorStateList);
                tab.setCustomView(tabView);
            }
        });
        //要执行这一句才是真正将两者绑定起来
        mediator.attach();

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

    @Override
    protected void initData() {

    }

    public class MyFragmentPagerAdapter extends FragmentStateAdapter {

        public MyFragmentPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @Override
        public Fragment createFragment(int position) {
            return list.get(position);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }
}
