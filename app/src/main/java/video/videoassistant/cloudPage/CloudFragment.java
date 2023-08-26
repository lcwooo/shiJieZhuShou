package video.videoassistant.cloudPage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.alibaba.fastjson.JSON;
import com.azhon.basic.adapter.OnItemClickListener;
import com.azhon.basic.base.BaseFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.jeremyliao.liveeventbus.LiveEventBus;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import video.videoassistant.R;
import video.videoassistant.databinding.FragmentCloudBinding;
import video.videoassistant.me.jointManage.JointEntity;
import video.videoassistant.playPage.PlayFragment;
import video.videoassistant.util.Constant;
import video.videoassistant.util.PreferencesUtils;
import video.videoassistant.util.UiUtil;

public class CloudFragment extends BaseFragment<CloudModel, FragmentCloudBinding> {


    private CloudListFragment listFragment;
    private static final String TAG = "CloudFragment";
    private TypeNameAdapter nameAdapter;
    List<JointEntity> jointList;
    //搜索出来的数据
    private List<SearchBean> searchBeans = new ArrayList<>();

    private int requestSum = 0;
    //List<JointEntity> jointEntities
    private JointEntity indexJoint;


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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initView() {
        dataBinding.setModel(viewModel);
        boolean isShow = PreferencesUtils.getBoolean(context, Constant.showList);
        if (isShow) {
            dataBinding.hide.setImageDrawable(getResources().getDrawable(R.drawable.show_list));
            dataBinding.type.setVisibility(View.VISIBLE);
        } else {
            dataBinding.hide.setImageDrawable(getResources().getDrawable(R.drawable.hide_list));
            dataBinding.type.setVisibility(View.GONE);
        }


        dataBinding.so.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    closeKeybord(dataBinding.so);
                    toSearch(dataBinding.so.getText().toString().trim());
                    //移动光标
                    dataBinding.so.setSelection(dataBinding.so.getText().toString().trim().length());
                    return true;
                }
                return false;
            }
        });

        dataBinding.ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeybord(dataBinding.so);
                toSearch(dataBinding.so.getText().toString().trim());
            }
        });

        dataBinding.deleteUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeybord(dataBinding.so);
                dataBinding.so.setText("");
                if (indexJoint != null) {
                    startJoint(indexJoint);
                }
                if (nameAdapter != null) {
                    nameAdapter.clearSum();
                    nameAdapter.selectType(0);
                }

            }
        });


        dataBinding.hideLayout.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {
                boolean isShow = PreferencesUtils.getBoolean(context, Constant.showList);
                if (isShow) {
                    dataBinding.hide.setImageDrawable(getResources().getDrawable(R.drawable.hide_list));
                    PreferencesUtils.putBoolean(context, Constant.showList, false);
                    dataBinding.type.setVisibility(View.GONE);
                } else {
                    dataBinding.hide.setImageDrawable(getResources().getDrawable(R.drawable.show_list));
                    PreferencesUtils.putBoolean(context, Constant.showList, true);
                    dataBinding.type.setVisibility(View.VISIBLE);
                }
            }
        });


    }

    public void toSearch(String s) {
        searchBeans.clear();
        dataBinding.so.setText(s);
        soTypeList(s);
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment, new SearchFragment())
                .commit();
    }


    /**
     * 关闭软键盘
     */
    public void closeKeybord(EditText mEditText) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }

    @Override
    protected void initData() {

        viewModel.getAll();

        viewModel.listJoint.observe(this, new Observer<List<JointEntity>>() {
            @Override
            public void onChanged(List<JointEntity> jointEntities) {
                initType(jointEntities);
                jointList = jointEntities;

            }
        });

        LiveEventBus.get(Constant.jointChange, String.class).observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                viewModel.getAll();
            }
        });

        viewModel.keyword.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (TextUtils.isEmpty(s)) {
                    dataBinding.deleteUsername.setVisibility(View.GONE);
                } else {
                    dataBinding.deleteUsername.setVisibility(View.VISIBLE);
                }
            }
        });

        LiveEventBus.get(Constant.soWord, String.class).observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                dataBinding.so.setText(s);
                //soTypeList(s);
                toSearch(s);
            }
        });


        viewModel.soSum.observe(this, new Observer<SoSumBean>() {
            @Override
            public void onChanged(SoSumBean soSumBean) {
                String s = soSumBean.getJson();
                requestSum--;
                int index = soSumBean.getIndex();
                SearchBean searchBean = null;
                if (s.startsWith("<?xml")) {
                    try {
                        initXms(s, index);
                        searchBean = new SearchBean(jointList.get(index).name, s, jointList.get(index).getUrl());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (s.startsWith("{")) {
                    initJson(s, index);
                    searchBean = new SearchBean(jointList.get(index).name, s, jointList.get(index).getUrl());
                } else {
                    if (UiUtil.listIsEmpty(jointList)) {
                        return;
                    }
                    UiUtil.showToastSafe(jointList.get(index).name + "：数据异常,请检查第三方数据是否正常。");
                }
                if (searchBean != null) {
                    searchBeans.add(searchBean);
                }
                if (requestSum == 0) {
                    LiveEventBus.get(Constant.searchData, List.class).post(searchBeans);
                }
            }
        });
    }

    private void initXms(String s, int index) {
        int sum = 0;
        try {
            TypeBean videoBean = null;
            InputStream inputStream = new ByteArrayInputStream(s.getBytes());
            // 创建一个xml解析的工厂
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            // 获得xml解析类的引用
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(inputStream, "UTF-8");
            // 获得事件的类型
            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if ("video".equals(parser.getName())) {
                            sum++;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if ("ty".equals(parser.getName())) {

                        }
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            UiUtil.showToastSafe("xml解析异常");
        }
        //Log.i(TAG, "initXms: " + index + "---" + sum);
        nameAdapter.setSum(index, sum);
    }

    private void initJson(String s, int index) {
        TypeListBean bean = null;
        try {
            bean = JSON.parseObject(s, TypeListBean.class);
        } catch (Exception e) {
            return;
        }
        if (UiUtil.listIsEmpty(bean.getList())) {
            return;
        }
        Log.i(TAG, "initJson: " + index + "===" + bean.getList().size());
        nameAdapter.setSum(index, bean.getList().size());
    }

    private void soTypeList(String s) {
        viewModel.cancelRequests();
        if (TextUtils.isEmpty(s)) {
            if (nameAdapter != null) {
                nameAdapter.clearSum();
            }
            return;
        }
        if (viewModel.listJoint.getValue() == null) {
            return;
        }
        List<JointEntity> list = viewModel.listJoint.getValue();
        int max = PreferencesUtils.getInt(context, Constant.searchMaxQuantity, 5);
        if (list.size() > max) {
            list = list.subList(0, max);
        }
        List<String> urlList = new ArrayList<>();
        for (JointEntity entity : list) {
            Log.i(TAG, "soTypeList: " + entity.getName());
            String url = entity.getUrl();
            if (url.contains("?")) {
                url = url.substring(0, url.indexOf("?"));
            }
            if (url.endsWith("/")) {
                url = url.substring(0, url.length() - 1);
            }
            url = url + "?ac=detail&wd=" + s;
            if (urlList.size() < 10) {
                urlList.add(url);
            }
        }
        Collections.reverse(urlList);
        requestSum = urlList.size();
        viewModel.getSoSum(urlList);
    }


    private void initType(List<JointEntity> jointEntities) {
        if (UiUtil.listIsEmpty(jointEntities)) {
            return;
        }
        indexJoint = jointEntities.get(0);
        dataBinding.type.setLayoutManager(new LinearLayoutManager(context));
        nameAdapter = new TypeNameAdapter();
        dataBinding.type.setAdapter(nameAdapter);
        nameAdapter.setNewData(jointEntities);
        startJoint(indexJoint);
        nameAdapter.getSelectListener(new TypeNameAdapter.SelectItem() {
            @Override
            public void select(JointEntity joint) {
                startJoint(joint);
            }
        });
    }


    public void startJoint(JointEntity joint) {
        listFragment = CloudListFragment.newInstance(joint.getUrl(), joint.getType(),
                dataBinding.so.getText().toString().trim());
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment, listFragment)
                .commit();
    }


}
