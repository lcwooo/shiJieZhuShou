package video.videoassistant.cloudPage;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.azhon.basic.adapter.OnItemClickListener;
import com.azhon.basic.base.BaseFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.jingbin.library.ByRecyclerView;
import video.videoassistant.R;
import video.videoassistant.databinding.FragmentCloudListBinding;
import video.videoassistant.util.UiUtil;

public class CloudListFragment extends BaseFragment<CloudModel, FragmentCloudListBinding> implements ByRecyclerView.OnRefreshListener, ByRecyclerView.OnLoadMoreListener {

    String url;
    String type;
    Map<String, String> map = new HashMap<>();
    private RecommendMovieAdapter recommendMovieAdapter;
    private static final String TAG = "CloudListFragment";
    private List<TypeBean> typeBeanList = new ArrayList<>();
    //页码 pg
    int page = 1;
    //搜索的数据 wd=搜索关键字
    private String keyword = "";
    //类别 t=类别ID
    private String soType = "";
    //类别 year
    private String soYear = "";

    public static CloudListFragment newInstance(String url, String type) {
        Bundle args = new Bundle();
        args.putString("url", url);
        args.putString("type", type);
        CloudListFragment fragment = new CloudListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected CloudModel initViewModel() {
        return new ViewModelProvider(this).get(CloudModel.class);
    }

    @Override
    protected void showError(Object obj) {

    }

    @Override
    protected int onCreate() {
        return R.layout.fragment_cloud_list;
    }

    @Override
    protected void initView() {
        dataBinding.setView(this);
        url = getArguments().getString("url");
        type = getArguments().getString("type");
        if (url.contains("?")) {
            url = url.substring(0, url.indexOf("?"));
        }
        map.put("ac", "list");

        viewModel.getAllType(url, map);


    }

    @Override
    protected void initData() {

        viewModel.jsonData.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s.startsWith("<?xml")) {
                    initXms(s);
                } else if (s.startsWith("{")) {
                    initJson(s);
                } else {
                    UiUtil.showToastSafe("接口类型不正确,只支持苹果cms格式接口。");
                }
            }
        });

        viewModel.typeData.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                if (s.startsWith("<?xml")) {
                    initAllXmlType(s);
                } else if (s.startsWith("{")) {
                    initAllJsonType(s);
                } else {
                    UiUtil.showToastSafe("接口类型不正确,只支持苹果cms格式接口。");
                }
                map.clear();
                map.put("ac", "detail");
                viewModel.getData(url, map,false);
            }
        });

        dataBinding.so.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    closeKeybord(dataBinding.so);
                    page = 1;
                    keyword = dataBinding.so.getText().toString();
                    toGo();
                    return true;
                }
                return false;
            }
        });

        dataBinding.ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeKeybord(dataBinding.so);
                page = 1;
                keyword = dataBinding.so.getText().toString();
                toGo();
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

    private void initAllXmlType(String s) {

    }

    private void initAllJsonType(String s) {
        Log.i(TAG, "initAllJsonType: " + s);
        TypeListBean bean = JSON.parseObject(s, TypeListBean.class);
        bean.getTypeBeanList().add(0, new TypeBean("", "全部分类"));
        typeBeanList = bean.getTypeBeanList();
    }


    private void initJson(String s) {
        ListMovieBean bean = JSON.parseObject(s, ListMovieBean.class);
        if(UiUtil.listIsEmpty(bean.getMovieBeanList())){
            UiUtil.showToastSafe("没有数据");
        }

        if (page == 1) {
            initList(bean.getMovieBeanList());
            dataBinding.recyclerView.setRefreshing(false);
        } else {
            recommendMovieAdapter.addData(bean.getMovieBeanList());
            dataBinding.recyclerView.loadMoreComplete();
        }
    }

    private void initList(List<MovieBean> list) {
        dataBinding.recyclerView.setRefreshHeaderView(new NeteaseRefreshHeaderView(context));
        dataBinding.recyclerView.setLoadingMoreView(new NeteaseLoadMoreView(context));
        dataBinding.recyclerView.setLayoutManager(new GridLayoutManager(context, 3));
        recommendMovieAdapter = new RecommendMovieAdapter();
        dataBinding.recyclerView.setAdapter(recommendMovieAdapter);
        recommendMovieAdapter.setNewData(list);
        recommendMovieAdapter.setOnItemListener(new OnItemClickListener<MovieBean>() {
            @Override
            public void onItemClick(MovieBean movieBean, int position) {
                //initPlay(movieBean);
            }

            @Override
            public boolean onItemLongClick(MovieBean movieBean, int position) {
                return false;
            }
        });

        dataBinding.recyclerView.setOnRefreshListener(this);
        dataBinding.recyclerView.setOnLoadMoreListener(this);
    }

    private void initXms(String s) {
    }

    public void showType() {
        if (UiUtil.listIsEmpty(typeBeanList)) {
            UiUtil.showToastSafe("没有获取到分类");
            return;
        }
        CloudDialog cloudDialog = new CloudDialog(context, typeBeanList);
        cloudDialog.show();
        cloudDialog.getTypeListener(new TypeListener() {
            @Override
            public void type(String typeId, String typeName) {
                dataBinding.type.setText("分类(" + typeName + ")");
                if (TextUtils.isEmpty(typeId)) {
                    soType = "";
                } else {
                    soType = typeId;
                }
                page = 1;
                keyword = "";
                dataBinding.so.setText("");
                toGo();
            }

            @Override
            public void year(String year) {
                if (year.equals("全部年份")) {
                    soYear = "";
                } else {
                    soYear = year;
                }
                page = 1;
                keyword = "";
                dataBinding.so.setText("");
                toGo();
            }
        });
    }

    public void toGo() {
        map.clear();
        map.put("ac", "detail");
        map.put("t", soType);
        map.put("pg", page + "");
        map.put("wd", keyword);
        map.put("year", soYear);
        viewModel.getData(url, map,true);
    }

    @Override
    public void onRefresh() {
        page = 1;
        map.clear();
        dataBinding.so.setText("");
        map.put("ac", "detail");
        viewModel.getData(url, map,true);
    }

    @Override
    public void onLoadMore() {
        page++;
        map.put("pg", page + "");
        viewModel.getData(url, map,true);

    }
}
