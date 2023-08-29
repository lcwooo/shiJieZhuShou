package video.videoassistant.playPage;

import android.app.PictureInPictureParams;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.util.Rational;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.alibaba.fastjson.JSON;
import com.android.cast.dlna.dmc.DLNACastManager;
import com.android.cast.dlna.dmc.OnDeviceRegistryListener;
import com.jeremyliao.liveeventbus.LiveEventBus;

import org.fourthline.cling.model.meta.Device;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import video.videoassistant.R;
import video.videoassistant.base.BaseActivity;
import video.videoassistant.base.BaseApplication;
import video.videoassistant.cloudPage.MovieItemBean;
import video.videoassistant.cloudPage.XmlMovieBean;
import video.videoassistant.databinding.ActivityPlayBinding;
import video.videoassistant.me.handleManage.HandleEntity;
import video.videoassistant.me.jsonManage.JsonEntity;
import video.videoassistant.playPage.roomCollect.CollectDao;
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

    List<PlayBean> playBeans;
    private boolean isCanDlna = false;
    private PlayInfoBean infoBean;
    List<JsonEntity> jsonList;

    private DlnaDialog dlnaDialog;

    Device selectDevice;


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
        String json = PreferencesUtils.getString(context, Constant.movieData);
        movieBean = JSON.parseObject(json, XmlMovieBean.class);
        BaseApplication.getInstance().setSaveProgress(true);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment, PlayFragment.getInstance(""))
                .commit();

        getSupportFragmentManager().beginTransaction().replace(R.id.web, new X5PlayFragment())
                .commit();
        loadView();

    }


    public void loadView() {
        if (movieBean == null) {
            return;
        }

        String name = movieBean.getName();
        dataBinding.name.setText(name);

        dataBinding.remark.setText(movieBean.getName().length() > 15 ?
                movieBean.getName().substring(0, 15) : movieBean.getName()
                + "/导演:" + movieBean.getDirector() + "(" + movieBean.getYear() + ")");
        String info = "主演:" + movieBean.getActor() + "     " + movieBean.getInfo();
        dataBinding.info.setText(info);

        initType();

        List<MovieItemBean> movieItemBeans = movieBean.getMovieItemBeans();
        if (movieItemBeans != null && !movieItemBeans.isEmpty()) {
            initGroup(movieItemBeans.get(0));
        } else {
            UiUtil.showToastSafe("数据异常");
        }

        dataBinding.name.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                copyUrl(dataBinding.name.getText().toString());
                return true;
            }
        });
    }

    private void initGroup(MovieItemBean movieItemBean) {
        String from = movieItemBean.getPlayUrl();
        playBeans = new ArrayList<>();
        if (from.contains("#")) {
            String[] arr = from.split("#");
            for (String a : arr) {
                if (a.contains("$")) {
                    String[] splitArr = a.split("\\$");
                    if (splitArr.length >= 2) {
                        PlayBean bean = new PlayBean();
                        bean.setName(splitArr[0]);
                        bean.setUrl(splitArr[1]);
                        playBeans.add(bean);
                    }
                }
            }
        } else {
            if (from.contains("$")) {
                String[] splitArr = from.split("\\$");
                if (splitArr.length >= 2) {
                    PlayBean bean = new PlayBean();
                    bean.setName(splitArr[0]);
                    bean.setUrl(splitArr[1]);
                    playBeans.add(bean);
                }
            }
        }
        if (!playBeans.isEmpty()) {
            String firstUrl = playBeans.get(0).getUrl();
            postAddress(firstUrl);
            playUrl = firstUrl;
            dataBinding.group.removeAllViews();
            PlayAddressAdapter adapter = new PlayAddressAdapter(playBeans, context);
            dataBinding.group.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            adapter.setOnItemClickListener(new PlayAddressAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(String typeId, String typeName, String name) {
                    playUrl = typeName;
                    initAddress();
                    dataBinding.group.selectLocation(Integer.parseInt(typeId));
                    LiveEventBus.get("movieName", String.class).post(name);
                }
            });
        }
    }


    private void postAddress(String playUrl) {
        new CountDownTimer(1000, 1000) {

            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                LiveEventBus.get(Constant.playUrl, String.class).post(playUrl);
            }
        }.start();
    }

    private void initAddress() {

        PreferencesUtils.putString(this, Constant.urlIng, playUrl);

        LiveEventBus.get(Constant.playUrl, String.class).post(playUrl);

        if (playUrl.endsWith(".m3u8")) {
            PlayFragment.getInstance(playUrl);
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
            if (type == 2) {
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


        handleAddress();

    }


    private void handleAddress() {

        if (TextUtils.isEmpty(playUrl)) {
            return;
        }

        if (handleEntity != null) {
            if (playUrl.contains(".m3u8")) {
                LiveEventBus.get(Constant.playAddress, String.class).post(playUrl);
            } else {
                LiveEventBus.get(Constant.webUrlGo, String.class).post(handleEntity.getUrl() + playUrl);
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
                    jsonList = jsonEntities;
                    BaseApplication.getInstance().setJsonEntities(jsonEntities);
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
                    BaseApplication.getInstance().setHandleEntities(handleEntities);
                    handleEntity = handleEntities.get(0);
                }
            }
        });

        viewModel.playAddress.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                LiveEventBus.get(Constant.playAddress, String.class).post(s);
                downM3u8(s);
            }
        });


        LiveEventBus.get(Constant.playState, Integer.class)
                .observe(this, new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        if (integer == 1) {
                            nextPlay();
                        } else if (integer == 2) {
                            upPlay();
                        } else if (integer == 6) {
                            if (TextUtils.isEmpty(UiUtil.getWifiIP(context))) {
                                UiUtil.showToastSafe("请先打开wifi");
                                return;
                            }
                            dlna();
                        }
                    }
                });


        LiveEventBus.get(Constant.selectJiexi, Object.class)
                .observe(this, new Observer<Object>() {

                    public void onChanged(Object o) {

                        if (o instanceof JsonEntity) {
                            handleEntity = null;
                            jsonEntity = (JsonEntity) o;
                            handleAddress();
                        }

                        if (o instanceof HandleEntity) {
                            jsonEntity = null;
                            handleEntity = (HandleEntity) o;
                            handleAddress();
                        }
                    }
                });

        LiveEventBus.get(Constant.dlnaUrl, String.class).observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s.equals("ok")) {
                    isCanDlna = true;
                } else {
                    isCanDlna = false;
                }
            }
        });


        LiveEventBus.get(Constant.playAddressInfo, PlayInfoBean.class)
                .observe(this, new Observer<PlayInfoBean>() {
                    @Override
                    public void onChanged(PlayInfoBean playInfoBean) {
                        infoBean = playInfoBean;
                        dataBinding.rl.setVisibility(View.VISIBLE);
                        String json = PreferencesUtils.getString(context, Constant.defaultCloud, "");
                        String name = "";
                        if (!TextUtils.isEmpty(json)) {
                            String[] arr = json.split("\\|\\|");
                            name = arr[1];
                        }

                        dataBinding.movieInfo.setText("分辨率:" + playInfoBean.getInfo() + "  解析:" + name);
                        dataBinding.movieUrl.setText("播放地址:" + playInfoBean.getUrl());
                    }
                });

        viewModel.dlnaAddress.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                DLNACastManager.getInstance().cast(selectDevice, new CastObject(s, UUID.randomUUID().toString(), ""));
                UiUtil.showToastSafe(s);
                if (dlnaDialog != null) {
                    dlnaDialog.dismiss();
                }
            }
        });
    }


    private void downM3u8(String s) {
        String url = s;
        if (url.contains(".mp4") || url.contains("233dy")) {
            return;
        }

        Observable<String> observable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> emitter) {
                try {
                    HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                    String fs = getExternalFilesDir("playList").getAbsolutePath() + "/webPlay.m3u8";
                    InputStream input = conn.getInputStream();
                    String str = "";
                    if (conn.getResponseCode() == 200) {
                        int index;
                        byte[] bytes = new byte[1024];
                        FileOutputStream downloadFile = new FileOutputStream(fs);
                        while ((index = input.read(bytes)) != -1) {
                            str += new String(bytes, 0, index);
                            downloadFile.write(bytes, 0, index);
                            downloadFile.flush();
                        }
                        input.close();
                        downloadFile.close();
                        if (str.contains("https://") || str.contains("http://")) {
                            LiveEventBus.get(Constant.dlnaUrl, String.class).post("ok");
                        } else {
                            LiveEventBus.get(Constant.dlnaUrl, String.class).post("no");
                        }
                        Log.i(TAG, "dowmM3U8a: 下载完成" + fs);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    LiveEventBus.get(Constant.dlnaUrl, String.class).post("no");
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());


    }

    private void dlna() {

        dlnaDialog = new DlnaDialog(context, jsonList, playUrl);
        dlnaDialog.show();

        //     DLNACastManager.getInstance().cast(device, new CastObject(playUrl, UUID.randomUUID().toString(), ""));
        //
        dlnaDialog.getDlnaSelectListener(new DlnaDialog.DlnaSelect() {
            @Override
            public void selectDlna(Device device, JsonEntity entity) {
                if (!playUrl.endsWith("m3u8") && !playUrl.endsWith("mp4")) {
                    if (entity == null) {
                        UiUtil.showToastSafe("请先添加json解析再进行投屏");
                        return;
                    }
                    selectDevice = device;
                    viewModel.getDlnaAddress(playUrl, entity);

                } else {
                    DLNACastManager.getInstance().cast(device, new CastObject(playUrl, UUID.randomUUID().toString(), ""));
                    dlnaDialog.dismiss();
                }
            }
        });

    }

    private void upPlay() {

        if (UiUtil.listIsEmpty(playBeans)) {
            return;
        }
        if (playBeans.size() == 0) {
            UiUtil.showToastSafe("没有上一集了");
            return;
        }
        if (getLocation() <= 0) {
            UiUtil.showToastSafe("没有上一集了");
            return;
        }


        dataBinding.group.selectLocation(getLocation() - 1);
        playUrl = playBeans.get(getLocation() - 1).getUrl();
        LiveEventBus.get("movieName", String.class).post(playBeans.get(getLocation()).getName());
        initAddress();
    }

    private void nextPlay() {
        if (UiUtil.listIsEmpty(playBeans)) {
            return;
        }
        if (playBeans.size() == 0) {
            UiUtil.showToastSafe("没有下一集了");
            return;
        }


        if (getLocation() >= playBeans.size() - 1) {
            UiUtil.showToastSafe("没有下一集了");
            return;
        }

        dataBinding.group.selectLocation(getLocation() + 1);
        playUrl = playBeans.get(getLocation() + 1).getUrl();
        LiveEventBus.get("movieName", String.class).post(playBeans.get(getLocation()).getName());
        initAddress();
    }

    public int getLocation() {
        for (int i = 0; i < playBeans.size(); i++) {
            if (playBeans.get(i).getUrl().equals(playUrl)) {
                return i;
            }
        }
        return 0;
    }

    @Override
    protected void onStart() {
        super.onStart();
        DLNACastManager.getInstance().bindCastService(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        DLNACastManager.getInstance().unbindCastService(this);
    }

    public void json() {
        if (UiUtil.listIsEmpty(BaseApplication.getInstance().getJsonEntities())) {
            UiUtil.showToastSafe("您没有添加任何json解析接口");
            return;
        }

        if (playUrl.contains(".m3u8") || playUrl.contains(".mp4")) {
            UiUtil.showToastSafe("直链播放状态，无需解析");
            return;
        }

        List<JsonEntity> list = BaseApplication.getInstance().getJsonEntities();
        PopupMenu popupMenu = new PopupMenu(context, dataBinding.json);
        android.view.Menu menu_more = popupMenu.getMenu();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            menu_more.add(android.view.Menu.NONE, android.view.Menu.FIRST + i, i,
                    UiUtil.getMaxLength(list.get(i).getName(), 4));
        }

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int i = item.getItemId();
                JsonEntity entity = list.get(i - 1);
                LiveEventBus.get(Constant.selectJiexi, Object.class)
                        .post(entity);
                PreferencesUtils.putString(context, Constant.defaultCloud, "2||" + entity.getName() + "||" + entity.getUrl());
                return true;
            }
        });

        popupMenu.show();
    }

    public void web() {
        if (UiUtil.listIsEmpty(BaseApplication.getInstance().getHandleEntities())) {
            UiUtil.showToastSafe("您没有添加任何json解析接口");
            return;
        }

        if (playUrl.contains(".m3u8") || playUrl.contains(".mp4")) {
            UiUtil.showToastSafe("直链播放状态，无需解析");
            return;
        }

        List<HandleEntity> list = BaseApplication.getInstance().getHandleEntities();
        PopupMenu popupMenu = new PopupMenu(context, dataBinding.webJie);
        android.view.Menu menu_more = popupMenu.getMenu();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            menu_more.add(android.view.Menu.NONE, android.view.Menu.FIRST + i, i,
                    UiUtil.getMaxLength(list.get(i).getName(), 4));

        }

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int i = item.getItemId();
                HandleEntity entity = list.get(i - 1);
                LiveEventBus.get(Constant.selectJiexi, Object.class)
                        .post(entity);
                PreferencesUtils.putString(context, Constant.defaultCloud, "1||" + entity.getName() + "||" + entity.getUrl());
                return true;
            }
        });

        popupMenu.show();
    }

    public void moreSet() {
        copyUrl(playUrl);
    }

    public void collectMovie() {
        String url = getIntent().getStringExtra("url");
        String json = PreferencesUtils.getString(context, Constant.movieData);
        if (TextUtils.isEmpty(url) || TextUtils.isEmpty(json)) {
            UiUtil.showToastSafe("数据出错");
            return;
        }
        viewModel.addCollect(url, json);
    }

    public void movieMore() {
        if (infoBean == null) {
            return;
        }

        PopupMenu popupMenu = new PopupMenu(context, dataBinding.movieMore);
        popupMenu.getMenuInflater().inflate(R.menu.play_movie_more, popupMenu.getMenu());
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.copy:
                        copyUrl(infoBean.getUrl());
                        break;
                    case R.id.other_play:
                        otherPlay(infoBean.getUrl());
                        break;
                }
                return true;
            }
        });
    }

    private void otherPlay(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(url), "video/*");
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            UiUtil.showToastSafe("没有应用可以打开");
        }
    }

    public void copyUrl(String url) {
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setText(url);
        UiUtil.showToastSafe("已复制");
    }

}
