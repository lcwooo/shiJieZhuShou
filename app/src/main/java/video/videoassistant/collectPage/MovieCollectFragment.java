package video.videoassistant.collectPage;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson.JSON;
import com.azhon.basic.base.BaseFragment;
import com.jeremyliao.liveeventbus.LiveEventBus;

import java.io.FileDescriptor;
import java.util.ArrayList;
import java.util.List;

import video.videoassistant.R;
import video.videoassistant.cloudPage.XmlMovieBean;
import video.videoassistant.databinding.FragmentMovieCollectBinding;
import video.videoassistant.indexPage.CollectMovieAdapter;
import video.videoassistant.playPage.roomCollect.CollectEntity;
import video.videoassistant.util.Constant;
import video.videoassistant.util.UiUtil;

public class MovieCollectFragment extends BaseFragment<CollectModel, FragmentMovieCollectBinding> {

    List<XmlMovieBean> beanList;
    private MovieListAdapter adapter;

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
                beanList = new ArrayList<>();
                for (CollectEntity entity : collectEntities) {
                    XmlMovieBean bean = JSON.parseObject(entity.getJson(), XmlMovieBean.class);
                    bean.setBiao(entity.getUrl());
                    beanList.add(bean);
                }
                initCollectRecyc();
            }
        });

    }

    private void initCollectRecyc() {
        dataBinding.recyc.setLayoutManager(new LinearLayoutManager(context));
        adapter = new MovieListAdapter();
        dataBinding.recyc.setAdapter(adapter);
        adapter.setNewData(beanList);
        adapter.getDeleteListener(new MovieListAdapter.Delete() {
            @Override
            public void deleteCollect(XmlMovieBean xmlMovieBean, int p) {
                viewModel.deleteMovie(xmlMovieBean);
                adapter.removeDate(xmlMovieBean);
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
