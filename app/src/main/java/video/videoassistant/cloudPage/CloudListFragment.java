package video.videoassistant.cloudPage;

import android.content.Context;
import android.content.Intent;
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

import com.alibaba.fastjson.JSON;
import com.azhon.basic.adapter.OnItemClickListener;
import com.azhon.basic.base.BaseFragment;
import com.azhon.basic.base.BaseLazyFragment;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.jingbin.library.ByRecyclerView;
import video.videoassistant.R;
import video.videoassistant.databinding.FragmentCloudListBinding;
import video.videoassistant.playPage.PlayActivity;
import video.videoassistant.util.UiUtil;

public class CloudListFragment extends BaseFragment<CloudModel, FragmentCloudListBinding> implements ByRecyclerView.OnRefreshListener, ByRecyclerView.OnLoadMoreListener {

    String url;
    String type;
    Map<String, String> map = new HashMap<>();
    private RecommendMovieAdapter recommendMovieAdapter;
    private static final String TAG = "CloudListFragment";
    private List<TypeBean> typeBeanList = new ArrayList<>();
    List<XmlMovieBean> moviesList = new ArrayList<>();
    //页码 pg
    int page = 1;
    //搜索的数据 wd=搜索关键字
    private String keyword = "";
    //类别 t=类别ID
    private String soType = "";
    //类别 year
    private String soYear = "";
    private XmlAdapter xmlAdapter;

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
                    try {
                        initXms(s);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
                    try {
                        initAllXmlType(s);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (s.startsWith("{")) {
                    initAllJsonType(s);
                } else {
                    UiUtil.showToastSafe("接口数据类型不正确,只支持苹果cms格式接口。");
                }
                map.clear();
                String url = getArguments().getString("url");
                if (!url.contains("ac=")) {
                    map.put("ac", "detail");
                }

                viewModel.getData(url, map, false);
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

    private void initAllXmlType(String s) throws Exception {

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
                    if ("ty".equals(parser.getName())) {
                        videoBean = new TypeBean();
                        String id = String.valueOf(parser.getAttributeValue(0));
                        videoBean.setTypeName(parser.nextText());
                        videoBean.setTypeId(id);
                        typeBeanList.add(videoBean);
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if ("ty".equals(parser.getName())) {

                    }
                    break;
            }
            eventType = parser.next();
        }
    }

    private void initAllJsonType(String s) {
        Log.i(TAG, "initAllJsonType: " + s);
        TypeListBean bean = JSON.parseObject(s, TypeListBean.class);
        bean.getTypeBeanList().add(0, new TypeBean("", "全部分类"));
        typeBeanList = bean.getTypeBeanList();
    }


    private void initList(List<MovieBean> list) {
        dataBinding.recyclerView.setRefreshHeaderView(new NeteaseRefreshHeaderView(context));
        dataBinding.recyclerView.setLoadingMoreView(new NeteaseLoadMoreView(context));
        dataBinding.recyclerView.setLayoutManager(new GridLayoutManager(context, 3));
        dataBinding.recyclerView.setRefreshEnabled(true);
        recommendMovieAdapter = new RecommendMovieAdapter();
        dataBinding.recyclerView.setAdapter(recommendMovieAdapter);
        recommendMovieAdapter.setNewData(list);
        recommendMovieAdapter.setOnItemListener(new OnItemClickListener<MovieBean>() {
            @Override
            public void onItemClick(MovieBean movieBean, int position) {
                changeJson(movieBean);
            }

            @Override
            public boolean onItemLongClick(MovieBean movieBean, int position) {
                return false;
            }
        });

        dataBinding.recyclerView.setOnRefreshListener(this);
        dataBinding.recyclerView.setOnLoadMoreListener(this);
    }

    private void changeJson(MovieBean movieBean) {
        XmlMovieBean bean = new XmlMovieBean();
        bean.setActor(movieBean.getVodActor());
        bean.setArea(movieBean.getVodArea());
        bean.setActor(movieBean.getVodActor());
        bean.setDirector(movieBean.getVodDirector());
        bean.setPic(movieBean.getVodPic());
        bean.setName(movieBean.getVodName());
        bean.setYear(movieBean.getVodYear());
        bean.setLang(movieBean.getVodLang());
        bean.setNote(movieBean.getVodRemarks());
        bean.setInfo(movieBean.getVodContent());
        List<MovieItemBean> list = new ArrayList<>();
        if (!movieBean.getVodPlayFrom().contains("$$$")) {
            MovieItemBean itemBean = new MovieItemBean();
            itemBean.setFrom(movieBean.getVodPlayFrom());
            itemBean.setPlayUrl(movieBean.getVodPlayUrl());
            list.add(itemBean);
        } else {
            List<String> type = Arrays.asList(movieBean.getVodPlayFrom().split("\\$\\$\\$"));
            List<String> address = Arrays.asList(movieBean.getVodPlayUrl().split("\\$\\$\\$"));
            for (int i = 0; i < type.size(); i++) {
                MovieItemBean be = new MovieItemBean();
                be.setFrom(type.get(i));
                be.setPlayUrl(address.get(i));
                list.add(be);
            }

        }
        bean.setMovieItemBeans(list);
        Intent intent = new Intent(context, PlayActivity.class);
        intent.putExtra("json", JSON.toJSONString(bean));
        startActivity(intent);
    }

    private void initXms(String s) throws Exception {

        if (page == 1) {
            moviesList.clear();
        }

        List<XmlMovieBean> movieList = new ArrayList<>();
        List<MovieItemBean> movieItemBeans = null;
        XmlMovieBean movieBean = null;
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
                        movieBean = new XmlMovieBean();
                    } else if ("name".equals(parser.getName())) {
                        movieBean.setName(parser.nextText());
                    } else if ("pic".equals(parser.getName())) {
                        movieBean.setPic(parser.nextText());
                    } else if ("type".equals(parser.getName())) {
                        movieBean.setType(parser.nextText());
                    } else if ("lang".equals(parser.getName())) {
                        movieBean.setLang(parser.nextText());
                    } else if ("area".equals(parser.getName())) {
                        movieBean.setArea(parser.nextText());
                    } else if ("year".equals(parser.getName())) {
                        movieBean.setYear(parser.nextText());
                    } else if ("note".equals(parser.getName())) {
                        movieBean.setNote(parser.nextText());
                    } else if ("actor".equals(parser.getName())) {
                        movieBean.setActor(parser.nextText());
                    } else if ("director".equals(parser.getName())) {
                        movieBean.setDirector(parser.nextText());
                    } else if ("dl".equals(parser.getName())) {
                        movieItemBeans = new ArrayList<>();
                    } else if ("dd".equals(parser.getName())) {
                        MovieItemBean itemBean = new MovieItemBean();
                        itemBean.setFrom(parser.getAttributeValue(0));
                        itemBean.setPlayUrl(parser.nextText());
                        movieItemBeans.add(itemBean);

                    } else if ("des".equals(parser.getName())) {
                        movieBean.setInfo(parser.nextText());
                    }
                    Log.i(TAG, "initXms: " + parser.getName());
                    break;
                case XmlPullParser.END_TAG:
                    if ("video".equals(parser.getName())) {
                        movieBean.setMovieItemBeans(movieItemBeans);
                        movieList.add(movieBean);
                        movieBean = null;
                    }
                    break;
            }
            eventType = parser.next();
        }


        if (page == 1) {
            dataBinding.recyclerView.setRefreshing(false);
            moviesList.addAll(movieList);
            initAdapter();
        } else {
            xmlAdapter.addData(movieList);
            dataBinding.recyclerView.loadMoreComplete();
        }
    }

    private void initAdapter() {
        dataBinding.recyclerView.setRefreshHeaderView(new NeteaseRefreshHeaderView(context));
        dataBinding.recyclerView.setLoadingMoreView(new NeteaseLoadMoreView(context));
        dataBinding.recyclerView.setLayoutManager(new GridLayoutManager(context, 3));
        xmlAdapter = new XmlAdapter();
        dataBinding.recyclerView.setAdapter(xmlAdapter);
        xmlAdapter.setNewData(moviesList);
        xmlAdapter.setOnItemListener(new OnItemClickListener<XmlMovieBean>() {
            @Override
            public void onItemClick(XmlMovieBean xmlMovieBean, int position) {
                Intent intent = new Intent(context, PlayActivity.class);
                intent.putExtra("json", JSON.toJSONString(xmlMovieBean));
                startActivity(intent);
            }

            @Override
            public boolean onItemLongClick(XmlMovieBean xmlMovieBean, int position) {
                return false;
            }
        });
        dataBinding.recyclerView.setOnRefreshListener(this);
        dataBinding.recyclerView.setOnLoadMoreListener(this);
    }

    private void initJson(String s) {
        ListMovieBean bean = JSON.parseObject(s, ListMovieBean.class);
        if (UiUtil.listIsEmpty(bean.getMovieBeanList())) {
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
        String url = getArguments().getString("url");
        if (!url.contains("ac=")) {
            map.put("ac", "detail");
        }
        map.put("t", soType);
        map.put("pg", page + "");
        map.put("wd", keyword);
        map.put("year", soYear);
        viewModel.getData(url, map, true);
    }

    @Override
    public void onRefresh() {
        page = 1;
        map.clear();
        dataBinding.so.setText("");
        String url = getArguments().getString("url");
        if (!url.contains("ac=")) {
            map.put("ac", "detail");
        }
        viewModel.getData(url, map, true);
    }

    @Override
    public void onLoadMore() {
        page++;
        map.put("pg", page + "");
        viewModel.getData(url, map, true);

    }


}
