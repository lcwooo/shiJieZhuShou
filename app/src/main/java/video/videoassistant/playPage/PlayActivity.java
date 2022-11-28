package video.videoassistant.playPage;

import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
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
import video.videoassistant.util.PreferencesUtils;
import video.videoassistant.util.UiUtil;

public class PlayActivity extends BaseActivity<PlayModel, ActivityPlayBinding> {


    private XmlMovieBean movieBean;
    private static final String TAG = "PlayActivity";
    private String playUrl;
    private List<View> lines = new ArrayList<>();
    private List<TextView> names = new ArrayList<>();

    //json解析节点
    private JsonEntity jsonEntity;
    //网页解析
    private HandleEntity handleEntity;

    private boolean isX5;


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
        dataBinding.setView(this);
        movieBean = JSON.parseObject(getIntent().getStringExtra("json"), XmlMovieBean.class);
        String json = PreferencesUtils.getString(this, Constant.defaultCloud, "");
        if (!TextUtils.isEmpty(json)) {
            String[] arr = json.split("\\|\\|");
            int type = Integer.parseInt(arr[0]);
            if (type == 1) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment, PlayFragment.getInstance(""))
                        .commit();
                isX5 = false;
            } else {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment, X5PlayFragment.getInstance(""))
                        .commit();
                isX5 = true;
            }
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment, PlayFragment.getInstance(""))
                    .commit();
            isX5 = false;
        }

        if (movieBean != null) {
            dataBinding.name.setText(movieBean.getName());
            dataBinding.remark.setText(movieBean.getName().length() > 15 ?
                    movieBean.getName().substring(0, 15) : movieBean.getName()
                    + "/导演:" + movieBean.getDirector() + "(" + movieBean.getYear() + ")");
            dataBinding.info.setText("主演:" + movieBean.getActor() + "     " + movieBean.getInfo());
            initType();
            try {
                initGroup(movieBean.getMovieItemBeans().get(0));
            } catch (Exception e) {
                e.printStackTrace();
                UiUtil.showToastSafe("数据异常");
            }
        }


    }

    private void initGroup(MovieItemBean movieItemBean) {
        String from = movieItemBean.getPlayUrl();

        List<PlayBean> playBeans = new ArrayList<>();
        if (from.contains("#")) {
            String[] arr = from.split("#");
            for (String a : arr) {
                if (a.contains("$")) {
                    PlayBean bean = new PlayBean();
                    bean.setName(a.split("\\$")[0]);
                    bean.setUrl(a.split("\\$")[1]);
                    playBeans.add(bean);
                }
            }
        } else {
            if (from.contains("$")) {
                PlayBean bean = new PlayBean();
                bean.setName(from.split("\\$")[0]);
                bean.setUrl(from.split("\\$")[1]);
                playBeans.add(bean);
            }
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

        LiveEventBus.get(Constant.playAddress, String.class).post("https://jx.aidouer.net/?url=https://www.iqiyi.com/v_1qzx9b00hs4.html");


        /*if (playUrl.contains(".m3u8")) {
            if (isX5) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment, PlayFragment.getInstance(playUrl))
                        .commit();
                isX5 = false;
            } else {
                PlayFragment.getInstance(playUrl);
            }
            return;
        }

        if (handleEntity == null && jsonEntity == null) {
            UiUtil.showToastSafe("请先添加解析");
            return;
        }

        if (handleEntity != null && jsonEntity != null) {
            handleEntity = null;
        }

        String json = PreferencesUtils.getString(this, Constant.defaultCloud, "");
        if (!TextUtils.isEmpty(json)) {
            String[] arr = json.split("\\|\\|");
            int type = Integer.parseInt(arr[0]);
            if (type == 1) {
                handleEntity = null;
                JsonEntity entity = new JsonEntity();
                entity.setName(arr[1]);
                entity.setUrl(arr[2]);
                jsonEntity = entity;
            } else {
                jsonEntity = null;
                HandleEntity entity = new HandleEntity();
                entity.setName(arr[1]);
                entity.setUrl(arr[2]);
                handleEntity = entity;
            }
        }


        handleAddress();*/

    }

    private void handleAddress() {

        if (TextUtils.isEmpty(playUrl)) {
            return;
        }

        if (handleEntity != null) {
            if (playUrl.contains(".m3u8")) {
                LiveEventBus.get(Constant.playAddress, String.class).post(playUrl);
            } else {
                UiUtil.showToastSafe("web");
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

        for (int i = 0; i < movieBean.getMovieItemBeans().size(); i++) {
            MovieItemBean bean = movieBean.getMovieItemBeans().get(i);
            View view = View.inflate(context, R.layout.item_play_type, null);
            TextView name = view.findViewById(R.id.name);
            View line = view.findViewById(R.id.line);
            if (i == 0) {
                name.setTextColor(getResources().getColor(R.color.red));
                line.setVisibility(View.VISIBLE);
            } else {
                name.setTextColor(getResources().getColor(R.color.textColor));
                line.setVisibility(View.GONE);
            }
            lines.add(line);
            names.add(name);
            name.setText(bean.getFrom());
            dataBinding.playType.addView(view);
            int finalI = i;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        initGroup(bean);
                        initLine(finalI);
                    } catch (Exception e) {
                        e.printStackTrace();
                        UiUtil.showToastSafe("数据异常");
                    }
                }
            });
        }
    }

    private void initLine(int i) {
        for (int j = 0; j < lines.size(); j++) {
            if (i == j) {
                lines.get(j).setVisibility(View.VISIBLE);
                names.get(j).setTextColor(getResources().getColor(R.color.red));
            } else {
                lines.get(j).setVisibility(View.GONE);
                names.get(j).setTextColor(getResources().getColor(R.color.textColor));
            }
        }
    }

    @Override
    protected void initData() {
        viewModel.getJsonList();


        viewModel.jsonList.observe(this, new Observer<List<JsonEntity>>() {
            @Override
            public void onChanged(List<JsonEntity> jsonEntities) {
                if (UiUtil.listIsEmpty(jsonEntities)) {
                    jsonEntity = null;
                } else {
                    jsonEntity = jsonEntities.get(0);
                }
                viewModel.getHandleList();
            }
        });

        viewModel.handleList.observe(this, new Observer<List<HandleEntity>>() {
            @Override
            public void onChanged(List<HandleEntity> handleEntities) {
                if (UiUtil.listIsEmpty(handleEntities)) {
                    handleEntity = null;
                } else {
                    handleEntity = handleEntities.get(0);
                }
            }
        });

        viewModel.playAddress.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                LiveEventBus.get(Constant.playAddress, String.class).post(s);
            }
        });
    }


}
