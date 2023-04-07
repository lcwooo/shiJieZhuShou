package video.videoassistant.cloudPage;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
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
import video.videoassistant.util.UiUtil;

public class CloudFragment extends BaseFragment<CloudModel, FragmentCloudBinding> {


    private CloudListFragment listFragment;
    private static final String TAG = "CloudFragment";
    private TypeNameAdapter nameAdapter;

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
        dataBinding.setModel(viewModel);
        dataBinding.so.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    closeKeybord(dataBinding.so);
                    LiveEventBus.get(Constant.soWord, String.class)
                            .post(dataBinding.so.getText().toString().trim());
                    return true;
                }
                return false;
            }
        });

        dataBinding.ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeybord(dataBinding.so);
                LiveEventBus.get(Constant.soWord, String.class)
                        .post(dataBinding.so.getText().toString().trim());
            }
        });

        dataBinding.deleteUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeybord(dataBinding.so);
                dataBinding.so.setText("");
                LiveEventBus.get(Constant.soWord, String.class)
                        .post(dataBinding.so.getText().toString().trim());
            }
        });
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
                soTypeList(s);
            }
        });

        viewModel.soSum.observe(this, new Observer<SoSumBean>() {
            @Override
            public void onChanged(SoSumBean soSumBean) {
                String s = soSumBean.getJson();
                int index = soSumBean.getIndex();
                if (TextUtils.isEmpty(s)) {
                    return;
                }
                if (s.startsWith("<?xml")) {
                    try {
                        initXms(s, index);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (s.startsWith("{")) {
                    initJson(s, index);
                } else {
                    UiUtil.showToastSafe("接口类型不正确,只支持苹果cms格式接口。");
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
        Log.i(TAG, "initXms: " + index + "---" + sum);
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
        if(TextUtils.isEmpty(s)){
            if(nameAdapter!=null){
                nameAdapter.clearSum();
            }
            return;
        }
        if (viewModel.listJoint.getValue() == null) {
            return;
        }
        List<JointEntity> list = viewModel.listJoint.getValue();
        List<String> urlList = new ArrayList<>();
        for (JointEntity entity : list) {
            Log.i(TAG, "soTypeList: "+entity.getName());
            String url = entity.getUrl();
            if (url.contains("?")) {
                url = url.substring(0, url.indexOf("?"));
            }
            if (url.endsWith("/")) {
                url = url.substring(0, url.length() - 1);
            }
            url = url + "?ac=list&wd=" + s;
            if(urlList.size()<10){
                urlList.add(url);
            }
        }
        Collections.reverse(urlList);
        viewModel.getSoSum(urlList);
    }


    private void initType(List<JointEntity> jointEntities) {
        if (UiUtil.listIsEmpty(jointEntities)) {
            return;
        }
        dataBinding.type.setLayoutManager(new LinearLayoutManager(context));
        nameAdapter = new TypeNameAdapter();
        dataBinding.type.setAdapter(nameAdapter);
        nameAdapter.setNewData(jointEntities);
        listFragment = CloudListFragment.newInstance(jointEntities.get(0).url,
                jointEntities.get(0).getType(), dataBinding.so.getText().toString().trim());
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment, listFragment)
                .commit();
        nameAdapter.getSelectListener(new TypeNameAdapter.SelectItem() {
            @Override
            public void select(JointEntity joint) {
                listFragment = CloudListFragment.newInstance(joint.getUrl(), joint.getType(),
                        dataBinding.so.getText().toString().trim());
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment, listFragment)
                        .commit();
            }
        });
    }


}
