package video.videoassistant.me.urlManage;

import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

import video.videoassistant.R;
import video.videoassistant.base.BaseActivity;
import video.videoassistant.databinding.ActivityUrlManageBinding;
import video.videoassistant.util.UiUtil;

public class UrlManageActivity extends BaseActivity<UrlManageModel, ActivityUrlManageBinding> {


    private static final String TAG = "UrlManageActivity";
    private ItemTouchHelper mItemTouchHelper;
    private RecyclerViewAdapter adapter;
    private boolean isSort = false;


    @Override
    protected UrlManageModel initViewModel() {
        return new ViewModelProvider(this).get(UrlManageModel.class);
    }

    @Override
    protected int onCreate() {
        return R.layout.activity_url_manage;
    }

    @Override
    protected void initView() {
        dataBinding.setView(this);
    }

    @Override
    protected void initData() {
        viewModel.getAllUrlType();

        viewModel.urlList.observe(this, new Observer<List<CollectionUrlEntity>>() {
            @Override
            public void onChanged(List<CollectionUrlEntity> urlTypeEntities) {
                if (UiUtil.listIsEmpty(urlTypeEntities)) {
                    AddDefaultType();
                    return;
                }
                for (CollectionUrlEntity e : urlTypeEntities) {
                    Log.i(TAG, "onChanged: " + e.toString());
                }
                initRecycview(urlTypeEntities);
            }
        });

        viewModel.addType.observe(this, new Observer<CollectionUrlEntity>() {
            @Override
            public void onChanged(CollectionUrlEntity CollectionUrlEntity) {
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

    private void initRecycview(List<CollectionUrlEntity> urlTypeEntities) {
        dataBinding.recyc.setLayoutManager(new LinearLayoutManager(context));
        adapter = new RecyclerViewAdapter(urlTypeEntities, this, new OnStartDragListener() {
            @Override
            public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
                mItemTouchHelper.startDrag(viewHolder);
            }

            @Override
            public void dragEnd() {
                isSort = true;
            }

            @Override
            public void dragRemove(CollectionUrlEntity entity,View view) {
                openOptions(view,entity);
            }
        });
        dataBinding.recyc.setAdapter(adapter);
        mItemTouchHelper = new ItemTouchHelper(new MyItemTouchHelperCallback(adapter));
        mItemTouchHelper.attachToRecyclerView(dataBinding.recyc);

    }

    private void openOptions(View view, CollectionUrlEntity entity) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.getMenuInflater().inflate(R.menu.url_more, popupMenu.getMenu());
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
                }
                return true;
            }
        });
    }

    private void editUrl(CollectionUrlEntity entity) {
       DialogAddUrl dialogAddUrl = new DialogAddUrl(this);
        dialogAddUrl.show();
        dialogAddUrl.editInit(entity);
        dialogAddUrl.addUrl(new DialogAddUrl.AddUrlListener() {
            @Override
            public void addUrl(CollectionUrlEntity entity) {

            }

            @Override
            public void editUrl(CollectionUrlEntity entity) {
                viewModel.updateUrl(entity);
            }
        });
    }

    private void updateSort(List<CollectionUrlEntity> sortList) {
        viewModel.updateSort(sortList, false);
        isSort = false;
    }

    private void AddDefaultType() {

    }

    public void addUrlTypeDialog() {
       DialogAddUrl dialogAddUrl = new DialogAddUrl(this);
        dialogAddUrl.show();
        dialogAddUrl.addUrl(new DialogAddUrl.AddUrlListener() {
            @Override
            public void addUrl(CollectionUrlEntity entity) {
                if(isSort && adapter!=null){
                    viewModel.updateSort(adapter.getSortList(),false);
                    isSort = false;
                }
                viewModel.addUrlType(entity);
            }

            @Override
            public void editUrl(CollectionUrlEntity entity) {

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
