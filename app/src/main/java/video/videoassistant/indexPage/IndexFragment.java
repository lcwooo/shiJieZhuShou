package video.videoassistant.indexPage;

import android.content.Context;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.alibaba.fastjson.JSON;
import com.azhon.basic.base.BaseFragment;
import com.jeremyliao.liveeventbus.LiveEventBus;

import java.util.ArrayList;
import java.util.List;

import video.videoassistant.R;
import video.videoassistant.cloudPage.XmlMovieBean;
import video.videoassistant.databinding.FragmentIndexBinding;
import video.videoassistant.generated.callback.OnClickListener;
import video.videoassistant.mainPage.MainActivity;
import video.videoassistant.playPage.roomCollect.CollectEntity;
import video.videoassistant.util.Constant;
import video.videoassistant.util.UiUtil;


public class IndexFragment extends BaseFragment<IndexModel, FragmentIndexBinding> {
    @Override
    protected IndexModel initViewModel() {
        return new ViewModelProvider(this).get(IndexModel.class);
    }

    @Override
    protected void showError(Object obj) {

    }

    @Override
    protected int onCreate() {
        return R.layout.fragment_index;
    }

    @Override
    protected void initView() {
        dataBinding.setModel(viewModel);
        dataBinding.setView(this);

        viewModel.getCollect();

        dataBinding.deleteUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataBinding.word.setText("");
            }
        });

        viewModel.collectList.observe(this, new Observer<List<CollectEntity>>() {
            @Override
            public void onChanged(List<CollectEntity> collectEntities) {
                initCollectList(collectEntities);
            }
        });

        LiveEventBus.get(Constant.refreshCollectMovie, String.class).observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                viewModel.getCollect();
            }
        });


    }

    private void initCollectList(List<CollectEntity> collectEntities) {
        RelativeLayout.LayoutParams layoutParams =
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        if (UiUtil.listIsEmpty(collectEntities)) {
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            layoutParams.setMargins(20, 20, 20, 20);
            dataBinding.more.setVisibility(View.GONE);
        } else {
            dataBinding.more.setVisibility(View.VISIBLE);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            layoutParams.setMargins(20, 20, 20, 20);
            List<XmlMovieBean> beanList = new ArrayList<>();
            for (CollectEntity entity : collectEntities) {
                if (beanList.size() <= 8) {
                    XmlMovieBean bean = JSON.parseObject(entity.getJson(), XmlMovieBean.class);
                    beanList.add(bean);
                } else {
                    break;
                }
            }
            initCollectRecyc(beanList);
        }
        dataBinding.layout.setLayoutParams(layoutParams);

    }

    private void initCollectRecyc(List<XmlMovieBean> beanList) {
        dataBinding.recycCollect.setNestedScrollingEnabled(false);
        dataBinding.recycCollect.setLayoutManager(new GridLayoutManager(context, 3));
        CollectMovieAdapter collectMovieAdapter = new CollectMovieAdapter();
        dataBinding.recycCollect.setAdapter(collectMovieAdapter);
        collectMovieAdapter.setNewData(beanList);
    }

    @Override
    protected void initData() {
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

        dataBinding.word.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    closeKeybord(dataBinding.word);
                    return true;
                }
                return false;
            }
        });
    }

    public void so() {
        if (TextUtils.isEmpty(dataBinding.word.getText().toString())) {
            return;
        }
        LiveEventBus.get(Constant.soWord, String.class)
                .post(dataBinding.word.getText().toString().trim());
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).selectPage(1);
        }
    }

    public void closeKeybord(EditText mEditText) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }
}
