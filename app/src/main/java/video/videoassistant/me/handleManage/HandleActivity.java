package video.videoassistant.me.handleManage;

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
import video.videoassistant.databinding.ActivityHandleBinding;
import video.videoassistant.me.urlManage.MyItemTouchHelperCallback;
import video.videoassistant.me.urlManage.OnStartDragListener;
import video.videoassistant.util.Constant;
import video.videoassistant.util.PreferencesUtils;
import video.videoassistant.util.UiUtil;

public class HandleActivity extends BaseActivity<HandleModel, ActivityHandleBinding> {


    private static final String TAG = "UrlManageActivity";
    private ItemTouchHelper mItemTouchHelper;
    private HandleAdapter adapter;
    private boolean isSort = false;


    @Override
    protected HandleModel initViewModel() {
        return new ViewModelProvider(this).get(HandleModel.class);
    }

    @Override
    protected int onCreate() {
        return R.layout.activity_handle;
    }

    @Override
    protected void initView() {
        dataBinding.setView(this);
    }

    @Override
    protected void initData() {
        viewModel.getAllUrlType();

        viewModel.urlList.observe(this, new Observer<List<HandleEntity>>() {
            @Override
            public void onChanged(List<HandleEntity> urlTypeEntities) {
                if (UiUtil.listIsEmpty(urlTypeEntities)) {
                    AddDefaultType();
                    return;
                }
                for (HandleEntity e : urlTypeEntities) {
                    Log.i(TAG, "onChanged: " + e.toString());
                }
                initRecycview(urlTypeEntities);
            }
        });

        viewModel.addType.observe(this, new Observer<HandleEntity>() {
            @Override
            public void onChanged(HandleEntity HandleEntity) {
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

    private void initRecycview(List<HandleEntity> urlTypeEntities) {
        dataBinding.recyc.setLayoutManager(new LinearLayoutManager(context));
        adapter = new HandleAdapter(urlTypeEntities, this, new OnStartDragListener() {
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
                HandleEntity e = (HandleEntity) entity;
                openOptions(view, e);
            }
        });
        dataBinding.recyc.setAdapter(adapter);
        mItemTouchHelper = new ItemTouchHelper(new MyItemTouchHelperCallback(adapter));
        mItemTouchHelper.attachToRecyclerView(dataBinding.recyc);

    }

    private void openOptions(View view, HandleEntity entity) {
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

    private void indexJiexi(HandleEntity entity) {
        PreferencesUtils.putString(this, Constant.defaultCloud, "2||" + entity.getName() + "||" + entity.getUrl());
        UiUtil.showToastSafe("设置成功");
    }

    private void editUrl(HandleEntity entity) {
        DialogHandle DialogHandle = new DialogHandle(this);
        DialogHandle.show();
        DialogHandle.editInit(entity);
        DialogHandle.addUrl(new DialogHandle.AddUrlListener() {
            @Override
            public void addUrl(HandleEntity entity) {

            }

            @Override
            public void editUrl(HandleEntity entity) {
                viewModel.updateUrl((HandleEntity) entity);
            }
        });
    }

    private void updateSort(List<HandleEntity> sortList) {
        viewModel.updateSort(sortList, false);
        isSort = false;
    }

    private void AddDefaultType() {

    }

    public void addUrlTypeDialog() {
        DialogHandle DialogHandle = new DialogHandle(this);
        DialogHandle.show();
        DialogHandle.addUrl(new DialogHandle.AddUrlListener() {
            @Override
            public void addUrl(HandleEntity entity) {
                if (isSort && adapter != null) {
                    viewModel.updateSort(adapter.getSortList(), false);
                    isSort = false;
                }
                viewModel.addUrlType(entity);
            }

            @Override
            public void editUrl(HandleEntity entity) {

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
