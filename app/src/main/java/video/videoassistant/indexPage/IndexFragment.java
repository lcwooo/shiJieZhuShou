package video.videoassistant.indexPage;

import androidx.lifecycle.ViewModelProvider;

import com.azhon.basic.base.BaseFragment;

import video.videoassistant.R;
import video.videoassistant.databinding.FragmentIndexBinding;


public class IndexFragment extends BaseFragment<IndexModel, FragmentIndexBinding> {
    @Override
    protected IndexModel initViewModel() {
        return new ViewModelProvider(this).get(IndexModel.class);
    }

    @Override
    protected void showError(Object obj) {

    }

    @Override
    protected int onCreate() {
        return R.layout.fragment_index;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }
}
