package video.videoassistant.playPage;

import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson.JSON;
import com.jeremyliao.liveeventbus.LiveEventBus;

import java.util.ArrayList;
import java.util.List;

import video.videoassistant.R;
import video.videoassistant.base.BaseActivity;
import video.videoassistant.cloudPage.CenterLayoutManager;
import video.videoassistant.cloudPage.MovieItemBean;
import video.videoassistant.cloudPage.XmlMovieBean;
import video.videoassistant.databinding.ActivityPlayBinding;
import video.videoassistant.me.handleManage.HandleEntity;
import video.videoassistant.me.jsonManage.JsonEntity;
import video.videoassistant.util.Constant;
import video.videoassistant.util.UiUtil;

public class PlayActivity extends BaseActivity<PlayModel, ActivityPlayBinding> {


    private XmlMovieBean movieBean;
    private static final String TAG = "PlayActivity";
    private String playUrl;
    private boolean isX5 = false;

    //json解析节点
    private JsonEntity jsonEntity;
    //网页解析
    private HandleEntity handleEntity;

    @Override
    protected PlayModel initViewModel() {
        return new ViewModelProvider(this).get(PlayModel.class);
    }

    @Override
    protected int onCreate() {
        return R.layout.activity_play;
    }

    @Override
    protected void initView() {
        movieBean = JSON.parseObject(getIntent().getStringExtra("json"), XmlMovieBean.class);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment, new PlayFragment())
                .commit();
        isX5 = false;
        if (movieBean != null) {
            dataBinding.name.setText(movieBean.getName());
            dataBinding.remark.setText(movieBean.getName().length() > 15 ?
                    movieBean.getName().substring(0, 15) : movieBean.getName()
                    + "/导演:" + movieBean.getDirector() + "(" + movieBean.getYear() + ")");
            dataBinding.info.setText("主演:" + movieBean.getActor() + "     " + movieBean.getInfo());
            initType();
            initGroup(movieBean.getMovieItemBeans().get(0));
        }


    }

    private void initGroup(MovieItemBean movieItemBean) {
        String from = movieItemBean.getPlayUrl();
        if (from.contains(".m3u8")) {
            dataBinding.llWeb.setVisibility(View.GONE);
            dataBinding.llJson.setVisibility(View.GONE);
        } else {
            dataBinding.llJson.setVisibility(View.VISIBLE);
            dataBinding.llWeb.setVisibility(View.VISIBLE);
        }
        dataBinding.from.setText("播放来源(" + movieItemBean.getFrom() + "):");
        List<PlayBean> playBeans = new ArrayList<>();
        if (from.contains("#")) {
            String[] arr = from.split("#");
            for (String a : arr) {
                PlayBean bean = new PlayBean();
                bean.setName(a.split("\\$")[0]);
                bean.setUrl(a.split("\\$")[1]);
                playBeans.add(bean);
            }
        } else {
            PlayBean bean = new PlayBean();
            bean.setName(from.split("\\$")[0]);
            bean.setUrl(from.split("\\$")[1]);
            playBeans.add(bean);
        }
        dataBinding.group.removeAllViews();
        PlayAddressAdapter adapter = new PlayAddressAdapter(playBeans, context);
        dataBinding.group.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.setOnItemClickListener(new PlayAddressAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String typeId, String typeName) {
                playUrl = typeName;
                initAddress();
            }
        });
    }

    private void initAddress() {
        if (handleEntity == null && jsonEntity == null) {
            UiUtil.showToastSafe("请先添加解析");
            return;
        }
        if (handleEntity != null && jsonEntity != null) {
            handleEntity = null;
        }

        if (handleEntity != null && !isX5) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment, new X5PlayFragment())
                    .commit();
            isX5 = true;
        }

        if (jsonEntity != null && isX5) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment, new PlayFragment())
                    .commit();
            isX5 = false;
        }

        showDialog("", false);
        new CountDownTimer(1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                dismissDialog();
                handleAddress();
            }
        }.start();


    }

    private void handleAddress() {

        if (TextUtils.isEmpty(playUrl)) {
            return;
        }

        if (handleEntity != null) {
            if (playUrl.contains(".m3u8")) {
                LiveEventBus.get(Constant.playAddress, String.class).post(playUrl);
            } else {
                LiveEventBus.get(Constant.playAddress, String.class).post(handleEntity.getUrl() + playUrl);
            }

        } else {
            if (playUrl.contains(".m3u8")) {
                LiveEventBus.get(Constant.playAddress, String.class).post(playUrl);
            } else {
                viewModel.getPlayAddress(jsonEntity.getUrl() + playUrl);
            }
        }
    }

    private void initType() {
        dataBinding.playType.removeAllViews();
        if (UiUtil.listIsEmpty(movieBean.getMovieItemBeans())) {
            UiUtil.showToastSafe("接口数据不正确");
            return;
        }
        for (MovieItemBean bean : movieBean.getMovieItemBeans()) {
            View view = View.inflate(context, R.layout.item_play_type, null);
            TextView name = view.findViewById(R.id.name);
            name.setText(bean.getFrom());
            dataBinding.playType.addView(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    initGroup(bean);
                }
            });
        }
    }

    @Override
    protected void initData() {
        viewModel.getJsonList();
        viewModel.getHandleList();

        viewModel.jsonList.observe(this, new Observer<List<JsonEntity>>() {
            @Override
            public void onChanged(List<JsonEntity> jsonEntities) {
                if (UiUtil.listIsEmpty(jsonEntities)) {
                    dataBinding.llJson.setVisibility(View.GONE);
                    return;
                }
                jsonEntity = jsonEntities.get(0);
                initJsonList(jsonEntities);
            }
        });

        viewModel.handleList.observe(this, new Observer<List<HandleEntity>>() {
            @Override
            public void onChanged(List<HandleEntity> handleEntities) {
                if (UiUtil.listIsEmpty(handleEntities)) {
                    dataBinding.llWeb.setVisibility(View.GONE);
                    return;
                }
                handleEntity = handleEntities.get(0);
                dataBinding.tvLine.setText("在线网页解析(" + handleEntities.get(0).getName() + "):");
                initWebList(handleEntities);
            }
        });

        viewModel.playAddress.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                LiveEventBus.get(Constant.playAddress, String.class).post(s);
            }
        });
    }

    private void initWebList(List<HandleEntity> handleEntities) {
        CenterLayoutManager layoutManager = new CenterLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        dataBinding.recycWeb.setLayoutManager(layoutManager);
        WebPlayAdapter urlAdapter = new WebPlayAdapter();
        dataBinding.recycWeb.setAdapter(urlAdapter);
        urlAdapter.setNewData(handleEntities);
        urlAdapter.getSortIndex(new SortIndex() {
            @Override
            public void toIndex(Object o, int position) {
                HandleEntity entity = (HandleEntity) o;
                handleEntity = entity;
                jsonEntity = null;
                initAddress();
                dataBinding.tvLine.setText("在线网页解析(" + entity.getName() + "):");
            }

            @Override
            public void toMore(Object o, int position) {

            }
        });
    }

    private void initJsonList(List<JsonEntity> jsonEntities) {
        CenterLayoutManager layoutManager = new CenterLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        dataBinding.recycJson.setLayoutManager(layoutManager);
        JsonPlayAdapter urlAdapter = new JsonPlayAdapter();
        dataBinding.recycJson.setAdapter(urlAdapter);
        urlAdapter.setNewData(jsonEntities);
        urlAdapter.getSortIndex(new SortIndex() {
            @Override
            public void toIndex(Object o, int position) {
                JsonEntity entity = (JsonEntity) o;
                jsonEntity = entity;
                handleEntity = null;
                initAddress();
            }

            @Override
            public void toMore(Object o, int position) {

            }
        });
    }


}
