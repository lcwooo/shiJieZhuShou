package video.videoassistant.cloudPage;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextUtils;
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

import com.azhon.basic.adapter.OnItemClickListener;
import com.azhon.basic.base.BaseFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.jeremyliao.liveeventbus.LiveEventBus;

import java.util.ArrayList;
import java.util.List;

import video.videoassistant.R;
import video.videoassistant.databinding.FragmentCloudBinding;
import video.videoassistant.me.jointManage.JointEntity;
import video.videoassistant.playPage.PlayFragment;
import video.videoassistant.util.Constant;
import video.videoassistant.util.UiUtil;

public class CloudFragment extends BaseFragment<CloudModel, FragmentCloudBinding> {


    private CloudListFragment listFragment;

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
                if(TextUtils.isEmpty(s)){
                    dataBinding.deleteUsername.setVisibility(View.GONE);
                }else {
                    dataBinding.deleteUsername.setVisibility(View.VISIBLE);
                }
            }
        });

        LiveEventBus.get(Constant.soWord, String.class).observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                dataBinding.so.setText(s);
            }
        });
    }

    private void initType(List<JointEntity> jointEntities) {
        if(UiUtil.listIsEmpty(jointEntities)){
            return;
        }
        dataBinding.type.setLayoutManager(new LinearLayoutManager(context));
        TypeNameAdapter nameAdapter = new TypeNameAdapter();
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
