package video.videoassistant.me.jsonManage;

import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import video.videoassistant.R;
import video.videoassistant.base.BaseActivity;
import video.videoassistant.databinding.ActivityJsonBinding;

import video.videoassistant.me.urlManage.MyItemTouchHelperCallback;
import video.videoassistant.me.urlManage.OnStartDragListener;

import video.videoassistant.util.Constant;
import video.videoassistant.util.PreferencesUtils;
import video.videoassistant.util.UiUtil;

public class JsonActivity extends BaseActivity<JsonModel, ActivityJsonBinding> {


    private static final String TAG = "UrlManageActivity";
    private ItemTouchHelper mItemTouchHelper;
    private JsonAdapter adapter;
    private boolean isSort = false;


    @Override
    protected JsonModel initViewModel() {
        return new ViewModelProvider(this).get(JsonModel.class);
    }

    @Override
    protected int onCreate() {
        return R.layout.activity_json;
    }

    @Override
    protected void initView() {
        dataBinding.setView(this);
    }

    @Override
    protected void initData() {
        viewModel.getAllUrlType();

        viewModel.urlList.observe(this, new Observer<List<JsonEntity>>() {
            @Override
            public void onChanged(List<JsonEntity> urlTypeEntities) {
 /*               if (UiUtil.listIsEmpty(urlTypeEntities)) {
                    AddDefaultType();
                    return;
                }
                for (JsonEntity e : urlTypeEntities) {
                    Log.i(TAG, "onChanged: " + e.toString());
                }*/
                initRecycview(urlTypeEntities);
            }
        });

        viewModel.addType.observe(this, new Observer<JsonEntity>() {
            @Override
            public void onChanged(JsonEntity JsonEntity) {
                viewModel.getAllUrlType();
            }
        });

        viewModel.isSort.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    updateSort(adapter.getSortList());
                }
            }
        });

        viewModel.isFinish.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                finish();
            }
        });
    }

    private void initRecycview(List<JsonEntity> urlTypeEntities) {
        dataBinding.recyc.setLayoutManager(new LinearLayoutManager(context));
        adapter = new JsonAdapter(urlTypeEntities, this, new OnStartDragListener() {
            @Override
            public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
                mItemTouchHelper.startDrag(viewHolder);
            }

            @Override
            public void dragEnd() {
                isSort = true;
            }

            @Override
            public void dragRemove(Object entity, View view) {
                JsonEntity e = (JsonEntity) entity;
                openOptions(view, e);
            }
        });
        dataBinding.recyc.setAdapter(adapter);
        mItemTouchHelper = new ItemTouchHelper(new MyItemTouchHelperCallback(adapter));
        mItemTouchHelper.attachToRecyclerView(dataBinding.recyc);

    }

    private void openOptions(View view, JsonEntity entity) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.getMenuInflater().inflate(R.menu.url_more_set, popupMenu.getMenu());
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.delete:
                        viewModel.deleteUrlType(entity);
                        break;
                    case R.id.edit:
                        editUrl(entity);
                        break;
                    case R.id.index:
                        indexJiexi(entity);
                        break;
                }
                return true;
            }
        });
    }

    private void indexJiexi(JsonEntity entity) {
        PreferencesUtils.putString(this, Constant.defaultCloud, "2||" + entity.getName() + "||" + entity.getUrl());
        UiUtil.showToastSafe("设置成功");
    }

    private void editUrl(JsonEntity entity) {
        DialogJson DialogJson = new DialogJson(this);
        DialogJson.show();
        DialogJson.editInit(entity);
        DialogJson.addUrl(new DialogJson.AddUrlListener() {
            @Override
            public void addUrl(JsonEntity entity) {

            }

            @Override
            public void editUrl(JsonEntity entity) {
                viewModel.updateUrl((JsonEntity) entity);
            }
        });
    }

    private void updateSort(List<JsonEntity> sortList) {
        viewModel.updateSort(sortList, false);
        isSort = false;
    }

    private void AddDefaultType() {

    }

    public void addUrlTypeDialog() {
        DialogJson DialogJson = new DialogJson(this);
        DialogJson.show();
        DialogJson.addUrl(new DialogJson.AddUrlListener() {
            @Override
            public void addUrl(JsonEntity entity) {
                if (isSort && adapter != null) {
                    viewModel.updateSort(adapter.getSortList(), false);
                    isSort = false;
                }
                viewModel.addUrlType(entity);
            }

            @Override
            public void editUrl(JsonEntity entity) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void back() {
        if (isSort) {
            if (adapter != null) {
                viewModel.updateSort(adapter.getSortList(), true);
                isSort = false;
            }
        } else {
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            back();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
