package video.videoassistant.me.jointManage;

import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jeremyliao.liveeventbus.LiveEventBus;

import java.util.List;

import video.videoassistant.R;
import video.videoassistant.base.BaseActivity;
import video.videoassistant.databinding.ActivityJointBinding;
import video.videoassistant.me.urlManage.CollectionUrlEntity;
import video.videoassistant.me.urlManage.DialogAddUrl;
import video.videoassistant.me.urlManage.MyItemTouchHelperCallback;
import video.videoassistant.me.urlManage.OnStartDragListener;
import video.videoassistant.me.urlManage.RecyclerViewAdapter;
import video.videoassistant.util.Constant;
import video.videoassistant.util.UiUtil;

public class JointManageActivity extends BaseActivity<JointModel, ActivityJointBinding> {

    private DialogAddJoint addJoint;
    private JointAdapter adapter;
    private ItemTouchHelper mItemTouchHelper;
    private boolean isSort = false;


    @Override
    protected JointModel initViewModel() {
        return new ViewModelProvider(this).get(JointModel.class);
    }

    @Override
    protected int onCreate() {
        return R.layout.activity_joint;
    }

    @Override
    protected void initView() {
        dataBinding.setView(this);
    }

    @Override
    protected void initData() {

        viewModel.getAll();

        viewModel.listJoint.observe(this, new Observer<List<JointEntity>>() {
            @Override
            public void onChanged(List<JointEntity> jointEntities) {
                //UiUtil.showToastSafe(jointEntities.size() + "");
                initList(jointEntities);
            }
        });

        viewModel.isFinish.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                finish();
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

    }

    private void updateSort(List<JointEntity> sortList) {
        viewModel.updateSort(sortList, false);
        isSort = false;
    }

    private void initList(List<JointEntity> jointEntities) {
        dataBinding.recyc.setLayoutManager(new LinearLayoutManager(context));
        adapter = new JointAdapter(jointEntities, this, new OnStartDragListener() {
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
                JointEntity e = (JointEntity) entity;
                openOptions(view,e);
            }
        });
        dataBinding.recyc.setAdapter(adapter);
        mItemTouchHelper = new ItemTouchHelper(new MyItemTouchHelperCallback(adapter));
        mItemTouchHelper.attachToRecyclerView(dataBinding.recyc);
    }

    private void openOptions(View view, JointEntity entity) {
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

    private void editUrl(JointEntity entity) {
        DialogAddJoint dialogAddUrl = new DialogAddJoint(this);
        dialogAddUrl.show();
        dialogAddUrl.editInit(entity);
        dialogAddUrl.addUrl(new DialogAddJoint.AddUrlListener() {
            @Override
            public void addUrl(JointEntity entity) {

            }

            @Override
            public void editUrl(JointEntity entity) {
                //viewModel.updateUrl(entity);
                viewModel.checkUrl(entity);
            }
        });
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

    public void addUrlTypeDialog() {
        addJoint = new DialogAddJoint(this);
        addJoint.show();
        addJoint.addUrl(new DialogAddJoint.AddUrlListener() {
            @Override
            public void addUrl(JointEntity entity) {
                viewModel.checkUrl(entity);
            }

            @Override
            public void editUrl(JointEntity entity) {

            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            back();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        LiveEventBus.get(Constant.jointChange,String.class).post("");
        super.onDestroy();
    }
}
