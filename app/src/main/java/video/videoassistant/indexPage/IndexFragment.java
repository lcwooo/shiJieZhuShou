package video.videoassistant.indexPage;

import android.content.Context;
import android.content.Intent;
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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson.JSON;
import com.azhon.basic.adapter.OnItemClickListener;
import com.azhon.basic.base.BaseFragment;
import com.jeremyliao.liveeventbus.LiveEventBus;

import java.util.ArrayList;
import java.util.List;

import video.videoassistant.R;
import video.videoassistant.bookmarkAndHistory.BookmarkHistoryActivity;
import video.videoassistant.browserPage.browserRoom.BookmarkEntity;
import video.videoassistant.cloudPage.XmlMovieBean;
import video.videoassistant.collectPage.CollectActivity;
import video.videoassistant.databinding.FragmentIndexBinding;
import video.videoassistant.mainPage.MainActivity;
import video.videoassistant.playPage.PlayActivity;
import video.videoassistant.playPage.roomCollect.CollectEntity;
import video.videoassistant.util.Constant;
import video.videoassistant.util.UiUtil;


public class IndexFragment extends BaseFragment<IndexModel, FragmentIndexBinding> {

    public String clickUrl;

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
        viewModel.getBook();

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
        viewModel.bookList.observe(this, new Observer<List<BookmarkEntity>>() {
            @Override
            public void onChanged(List<BookmarkEntity> bookmarkEntities) {
                initBookMark(bookmarkEntities);
            }
        });

    }

    private void initBookMark(List<BookmarkEntity> bookmarkEntities) {
        List<BookmarkEntity> list = new ArrayList<>();
        for (BookmarkEntity entity : bookmarkEntities){
            if(list.size()<10){
                list.add(entity);
            }else {
                break;
            }
        }
        initBookRecyc(list);
    }

    private void initBookRecyc(List<BookmarkEntity> list) {
        dataBinding.recycUrl.setNestedScrollingEnabled(false);
        dataBinding.recycUrl.setLayoutManager(new LinearLayoutManager(context));
        UrlAdapter urlAdapter = new UrlAdapter();
        dataBinding.recycUrl.setAdapter(urlAdapter);
        urlAdapter.setNewData(list);
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.getBook();
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
            List<CollectEntity> newList = new ArrayList<>();
            for (CollectEntity entity : collectEntities) {
                if (newList.size() <= 8) {
                    newList.add(entity);
                } else {
                    break;
                }
            }
            initCollectRecyc(newList);
        }
        dataBinding.layout.setLayoutParams(layoutParams);

    }

    private void initCollectRecyc(List<CollectEntity> beanList) {
        dataBinding.recycCollect.setNestedScrollingEnabled(false);
        dataBinding.recycCollect.setLayoutManager(new GridLayoutManager(context, 3));
        CollectMovieAdapter collectMovieAdapter = new CollectMovieAdapter();
        dataBinding.recycCollect.setAdapter(collectMovieAdapter);
        collectMovieAdapter.setNewData(beanList);
        collectMovieAdapter.setOnItemListener(new OnItemClickListener<CollectEntity>() {
            @Override
            public void onItemClick(CollectEntity collectEntity, int position) {
                clickUrl = collectEntity.getUrl();
                viewModel.loadMovie(collectEntity.getUrl());
            }

            @Override
            public boolean onItemLongClick(CollectEntity collectEntity, int position) {
                return false;
            }
        });

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

        viewModel.jsonData.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s.startsWith("<?xml")) {
                    List<XmlMovieBean> beanList = MovieUtils.xml(s);
                    if (UiUtil.listIsEmpty(beanList)) {
                        UiUtil.showToastSafe("获取第三方网站数据出错");
                        return;
                    }
                    toPlay(beanList.get(0));

                } else if (s.startsWith("{")) {
                    List<XmlMovieBean> beanList = MovieUtils.initJson(s);
                    if (UiUtil.listIsEmpty(beanList)) {
                        UiUtil.showToastSafe("获取第三方网站数据出错");
                        return;
                    }
                    toPlay(beanList.get(0));
                } else {
                    UiUtil.showToastSafe("接口类型不正确,只支持苹果cms格式接口。");
                }
            }
        });
    }

    public void toPlay(XmlMovieBean bean) {
        Intent intent = new Intent(context, PlayActivity.class);
        String jsonUrl = clickUrl + "?ac=detail&ids=" + bean.getId();
        intent.putExtra("url", jsonUrl);
        intent.putExtra("json", JSON.toJSONString(bean));
        startActivity(intent);
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

    public void movieList() {
        Intent intent = new Intent(context, CollectActivity.class);
        intent.putExtra("page", 0);
        startActivity(intent);
    }

    public void openBook(){
        toActivity(BookmarkHistoryActivity.class);
    }
}
