package video.videoassistant.bookmarkAndHistory;

import android.content.Intent;
import android.view.View;

import com.azhon.basic.adapter.BaseDBRVAdapter;

import video.videoassistant.BR;
import video.videoassistant.R;
import video.videoassistant.browserPage.BrowserActivity;
import video.videoassistant.browserPage.browserRoom.HistoryEntity;
import video.videoassistant.databinding.AdapterMarkBinding;

public class HistoryAdapter extends BaseDBRVAdapter<HistoryEntity, AdapterMarkBinding> {
    public HistoryAdapter() {
        super(R.layout.adapter_mark, BR.bean);
    }

    @Override
    protected void initData(AdapterMarkBinding binding, HistoryEntity bean, int position) {
        binding.name.setText(bean.getName());
        binding.url.setText(bean.getUrl());
        binding.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (delete != null) {
                    delete.deleteCollect(bean, position);
                }
            }
        });

        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, BrowserActivity.class);
                intent.putExtra("url", bean.getUrl());
                context.startActivity(intent);
            }
        });
    }

    public Delete delete;

    public void getDeleteListener(Delete delete) {
        this.delete = delete;
    }

    public interface Delete {
        void deleteCollect(HistoryEntity bookmark, int p);
    }
}
