package video.videoassistant.me.urlManage;

import android.app.AlertDialog;
import android.content.DialogInterface;
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


import com.jeremyliao.liveeventbus.LiveEventBus;

import java.util.List;

import video.videoassistant.R;
import video.videoassistant.base.BaseActivity;
import video.videoassistant.databinding.ActivityUrlManageBinding;
import video.videoassistant.util.Constant;
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
                    dataBinding.clear.setVisibility(View.GONE);
                }else {
                    dataBinding.clear.setVisibility(View.VISIBLE);
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
            public void dragRemove(Object entity,View view) {
                CollectionUrlEntity e = (CollectionUrlEntity) entity;
                openOptions(view,e);
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
        LiveEventBus.get(Constant.urlChange,String.class).post("");
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

    public void clearData(){
        new AlertDialog.Builder(context)
                .setTitle("提醒")
                .setMessage("此操作将清空所以数据，您确认要清空所有数据?")
                .setNegativeButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        viewModel.clearData();
                    }
                })
                .setNeutralButton("取消", null).show();

    }


}
