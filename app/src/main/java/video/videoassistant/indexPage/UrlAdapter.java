package video.videoassistant.indexPage;

import android.content.Intent;
import android.view.View;

import com.azhon.basic.adapter.BaseDBRVAdapter;

import video.videoassistant.BR;
import video.videoassistant.R;
import video.videoassistant.browserPage.BrowserActivity;
import video.videoassistant.browserPage.browserRoom.BookmarkEntity;
import video.videoassistant.databinding.AdapterBookBinding;

public class UrlAdapter extends BaseDBRVAdapter<BookmarkEntity, AdapterBookBinding> {


    public UrlAdapter() {
        super(R.layout.adapter_book, BR.bean);
    }

    @Override
    protected void initData(AdapterBookBinding binding, BookmarkEntity bookmark, int position) {
        binding.position.setText((position + 1) + ".");
        binding.info.setText(bookmark.getName());
        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, BrowserActivity.class);
                intent.putExtra("url", bookmark.getUrl());
                context.startActivity(intent);
            }
        });
    }
}
