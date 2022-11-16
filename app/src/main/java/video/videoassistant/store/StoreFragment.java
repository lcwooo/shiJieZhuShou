package video.videoassistant.store;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.azhon.basic.base.BaseFragment;
import com.jeremyliao.liveeventbus.LiveEventBus;

import java.util.List;

import video.videoassistant.R;
import video.videoassistant.databinding.FragmentStoreBinding;
import video.videoassistant.me.urlManage.CollectionUrlEntity;
import video.videoassistant.util.Constant;


public class StoreFragment extends BaseFragment<StoreModel, FragmentStoreBinding> {
    @Override
    protected StoreModel initViewModel() {
        return new ViewModelProvider(this).get(StoreModel.class);
    }

    @Override
    protected void showError(Object obj) {

    }

    @Override
    protected int onCreate() {
        return R.layout.fragment_store;
    }

    @Override
    protected void initView() {
        dataBinding.recyc.setLayoutManager(new GridLayoutManager(context, 3));
    }

    @Override
    protected void initData() {
        viewModel.getAll();

        viewModel.urlList.observe(this, new Observer<List<CollectionUrlEntity>>() {
            @Override
            public void onChanged(List<CollectionUrlEntity> list) {
                initList(list);
            }
        });

        LiveEventBus.get(Constant.urlChange, String.class)
                .observe(this, new Observer<String>() {
                    @Override
                    public void onChanged(String s) {
                        viewModel.getAll();
                    }
                });
    }

    private void initList(List<CollectionUrlEntity> list) {
        ListAdapter adapter = new ListAdapter();
        dataBinding.recyc.setAdapter(adapter);
        adapter.setNewData(list);
    }
}
