package video.videoassistant.store;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;


import com.azhon.basic.adapter.BaseDBRVAdapter;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import video.videoassistant.BR;
import video.videoassistant.R;
import video.videoassistant.browserPage.BrowserActivity;
import video.videoassistant.databinding.AdapterListBinding;
import video.videoassistant.me.urlManage.CollectionUrlEntity;
import video.videoassistant.playPage.PlayActivity;


public class ListAdapter extends BaseDBRVAdapter<CollectionUrlEntity, AdapterListBinding> {
    public ListAdapter() {
        super(R.layout.adapter_list, BR.bean);
    }

    @Override
    protected void initData(AdapterListBinding binding, CollectionUrlEntity CollectionUrlEntity, int position) {
        binding.name.setText(CollectionUrlEntity.getName());
        binding.remark.setText(CollectionUrlEntity.getRemark());

        binding.url.setText(CollectionUrlEntity.getUrl());
        if (TextUtils.isEmpty(CollectionUrlEntity.getRemark())) {
            binding.remark.setVisibility(View.GONE);
        } else {
            binding.remark.setVisibility(View.VISIBLE);
        }
        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, BrowserActivity.class);
                intent.putExtra("url", CollectionUrlEntity.getUrl());
                context.startActivity(intent);
            }
        });
    }

    @SuppressLint("CheckResult")
    public RequestOptions getRequestOptions() {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.skipMemoryCache(true);
        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
        requestOptions.fitCenter();
        return requestOptions;
    }
}
