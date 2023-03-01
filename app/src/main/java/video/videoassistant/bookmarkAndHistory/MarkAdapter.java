package video.videoassistant.bookmarkAndHistory;

import android.view.View;

import com.azhon.basic.adapter.BaseDBRVAdapter;

import video.videoassistant.BR;
import video.videoassistant.R;
import video.videoassistant.browserPage.browserRoom.BookmarkEntity;
import video.videoassistant.collectPage.MovieListAdapter;
import video.videoassistant.databinding.AdapterMarkBinding;
import video.videoassistant.playPage.roomCollect.CollectEntity;

public class MarkAdapter extends BaseDBRVAdapter<BookmarkEntity, AdapterMarkBinding> {
    public MarkAdapter() {
        super(R.layout.adapter_mark, BR.bean);
    }

    @Override
    protected void initData(AdapterMarkBinding binding, BookmarkEntity bean, int position) {
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
    }

    public Delete delete;

    public void getDeleteListener(Delete delete) {
        this.delete = delete;
    }

    public interface Delete {
        void deleteCollect(BookmarkEntity bookmark, int p);
    }
}
