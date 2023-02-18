package video.videoassistant.collectPage;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson.JSON;
import com.azhon.basic.adapter.OnItemClickListener;
import com.azhon.basic.base.BaseFragment;
import com.jeremyliao.liveeventbus.LiveEventBus;

import java.io.FileDescriptor;
import java.util.ArrayList;
import java.util.List;

import video.videoassistant.R;
import video.videoassistant.cloudPage.XmlMovieBean;
import video.videoassistant.databinding.FragmentMovieCollectBinding;
import video.videoassistant.indexPage.CollectMovieAdapter;
import video.videoassistant.indexPage.MovieUtils;
import video.videoassistant.playPage.PlayActivity;
import video.videoassistant.playPage.roomCollect.CollectEntity;
import video.videoassistant.util.Constant;
import video.videoassistant.util.UiUtil;

public class MovieCollectFragment extends BaseFragment<CollectModel, FragmentMovieCollectBinding> {


    private MovieListAdapter adapter;
    public String clickUrl;

    @Override
    protected CollectModel initViewModel() {
        return new ViewModelProvider(this).get(CollectModel.class);
    }

    @Override
    protected void showError(Object obj) {

    }

    @Override
    protected int onCreate() {
        return R.layout.fragment_movie_collect;
    }

    @Override
    protected void initView() {

        dataBinding.setView(this);
        dataBinding.setModel(viewModel);

    }

    @Override
    protected void initData() {

        viewModel.getData();

        viewModel.closePage.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                getActivity().finish();
            }
        });

        viewModel.clearData.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                UiUtil.showToastSafe("删除成功");
                viewModel.getData();
            }
        });

        viewModel.collectList.observe(this, new Observer<List<CollectEntity>>() {
            @Override
            public void onChanged(List<CollectEntity> collectEntities) {
                initCollectRecyc(collectEntities);
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

    private void initCollectRecyc(List<CollectEntity> collectEntities) {
        dataBinding.recyc.setLayoutManager(new LinearLayoutManager(context));
        adapter = new MovieListAdapter();
        dataBinding.recyc.setAdapter(adapter);
        adapter.setNewData(collectEntities);
        adapter.getDeleteListener(new MovieListAdapter.Delete() {
            @Override
            public void deleteCollect(CollectEntity xmlMovieBean, int p) {

                viewModel.deleteMovie(xmlMovieBean);
                adapter.removeDate(xmlMovieBean);
            }
        });
        adapter.setOnItemListener(new OnItemClickListener<CollectEntity>() {
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
    public void onDestroy() {
        super.onDestroy();
        LiveEventBus.get(Constant.refreshCollectMovie, String.class).post("yes");
    }

    public void tips() {
        new AlertDialog.Builder(context)
                .setTitle("提醒")
                .setMessage("你确认要清空所有收藏的视频?")
                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        viewModel.clearData();
                    }
                })
                .setNeutralButton("取消", null).show();
    }
}
