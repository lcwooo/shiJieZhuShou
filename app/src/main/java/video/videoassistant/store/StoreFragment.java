package video.videoassistant.store;

import androidx.lifecycle.ViewModelProvider;

import com.azhon.basic.base.BaseFragment;

import video.videoassistant.R;
import video.videoassistant.databinding.FragmentStoreBinding;


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

    }

    @Override
    protected void initData() {

    }
}
