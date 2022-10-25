package video.videoassistant.me.urlManage;

import android.text.TextUtils;
import android.view.View;

import com.azhon.basic.adapter.BaseDBRVAdapter;

import video.videoassistant.BR;
import video.videoassistant.R;
import video.videoassistant.databinding.AdapterUrlBinding;

public class UrlAdapter extends BaseDBRVAdapter<CollectionUrlEntity, AdapterUrlBinding> {
    public UrlAdapter() {
        super(R.layout.adapter_url, BR.view);
    }

    @Override
    protected void initData(AdapterUrlBinding binding, CollectionUrlEntity collectionUrlEntity, int position) {

        binding.name.setText(collectionUrlEntity.name);
        binding.url.setText(collectionUrlEntity.url);
        if(TextUtils.isEmpty(collectionUrlEntity.remark)){
            binding.remark.setVisibility(View.GONE);
        }else {
            binding.remark.setVisibility(View.VISIBLE);
            binding.remark.setText(collectionUrlEntity.remark);
        }
    }
}
