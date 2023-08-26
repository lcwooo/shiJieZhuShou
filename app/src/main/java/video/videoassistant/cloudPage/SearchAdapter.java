package video.videoassistant.cloudPage;

import android.content.Intent;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;

import com.alibaba.fastjson.JSON;
import com.azhon.basic.adapter.BaseDBRVAdapter;
import com.azhon.basic.adapter.OnItemClickListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import video.videoassistant.BR;
import video.videoassistant.R;
import video.videoassistant.databinding.AdapterSearchBinding;
import video.videoassistant.me.jsonManage.JsonAdapter;
import video.videoassistant.playPage.PlayActivity;
import video.videoassistant.util.Constant;
import video.videoassistant.util.PreferencesUtils;
import video.videoassistant.util.UiUtil;

public class SearchAdapter extends BaseDBRVAdapter<SearchBean, AdapterSearchBinding> {

    private static final String TAG = "SearchAdapter";

    public SearchAdapter() {
        super(R.layout.adapter_search, BR.bean);
    }

    @Override
    protected void initData(AdapterSearchBinding binding, SearchBean searchBean, int position) {
        binding.requestFrom.setText(searchBean.getFrom());
        String json = searchBean.getData();
        String url = searchBean.getUrl();

        if (XmlDataUtil.dataType(json).equals(XmlDataUtil.xml)) {
            List<XmlMovieBean> xmlMovieBeans = XmlDataUtil.initXms(json);
            if (UiUtil.listIsEmpty(xmlMovieBeans)) {
                binding.requestFrom.setVisibility(View.GONE);
                return;
            }
            binding.recyclerView.setLayoutManager(new GridLayoutManager(context, 3));
            XmlAdapter xmlAdapter = new XmlAdapter();
            binding.recyclerView.setAdapter(xmlAdapter);
            xmlAdapter.setNewData(xmlMovieBeans);
            xmlAdapter.setOnItemListener(new OnItemClickListener<XmlMovieBean>() {
                @Override
                public void onItemClick(XmlMovieBean bean, int position) {
                    Intent intent = new Intent(context, PlayActivity.class);
                    String jsonUrl = url + "?ac=detail&ids=" + bean.getId();
                    intent.putExtra("url", jsonUrl);
                    PreferencesUtils.putString(context, Constant.movieData, JSON.toJSONString(bean));
                    context.startActivity(intent);
                }

                @Override
                public boolean onItemLongClick(XmlMovieBean bean, int position) {
                    return false;
                }
            });
        } else if (XmlDataUtil.dataType(json).equals(XmlDataUtil.json)) {
            ListMovieBean beans = JSON.parseObject(json, ListMovieBean.class);
            if (UiUtil.listIsEmpty(beans.getMovieBeanList())) {
                binding.requestFrom.setVisibility(View.GONE);
                return;
            }
            binding.recyclerView.setLayoutManager(new GridLayoutManager(context, 3));
            RecommendMovieAdapter recommendMovieAdapter = new RecommendMovieAdapter();
            binding.recyclerView.setAdapter(recommendMovieAdapter);
            recommendMovieAdapter.setNewData(beans.getMovieBeanList());
            recommendMovieAdapter.setOnItemListener(new OnItemClickListener<MovieBean>() {
                @Override
                public void onItemClick(MovieBean movieBean, int position) {
                    changeJson(movieBean, url);
                }

                @Override
                public boolean onItemLongClick(MovieBean movieBean, int position) {
                    return false;
                }
            });
        } else {
            binding.requestFrom.setVisibility(View.GONE);
        }


    }


    private void changeJson(MovieBean movieBean, String url) {
        XmlMovieBean bean = new XmlMovieBean();
        bean.setActor(movieBean.getVodActor());
        bean.setArea(movieBean.getVodArea());
        bean.setActor(movieBean.getVodActor());
        bean.setDirector(movieBean.getVodDirector());
        bean.setPic(movieBean.getVodPic());
        bean.setName(movieBean.getVodName());
        bean.setYear(movieBean.getVodYear());
        bean.setLang(movieBean.getVodLang());
        bean.setId(movieBean.getVodId() + "");
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
        String jsonUrl = url + "?ac=detail&ids=" + bean.getId();
        intent.putExtra("url", jsonUrl);
        PreferencesUtils.putString(context, Constant.movieData, JSON.toJSONString(bean));
        context.startActivity(intent);
    }
}
