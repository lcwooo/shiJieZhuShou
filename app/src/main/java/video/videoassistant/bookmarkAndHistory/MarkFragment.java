package video.videoassistant.bookmarkAndHistory;

import android.os.Bundle;
import android.view.View;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.azhon.basic.base.BaseFragment;

import java.util.List;

import video.videoassistant.R;
import video.videoassistant.browserPage.browserRoom.BookmarkEntity;
import video.videoassistant.browserPage.browserRoom.HistoryEntity;
import video.videoassistant.databinding.FragmentMarkBinding;
import video.videoassistant.util.UiUtil;

public class MarkFragment extends BaseFragment<Model, FragmentMarkBinding> {

    int page;

    public static MarkFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt("page", page);
        MarkFragment fragment = new MarkFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected Model initViewModel() {
        return new ViewModelProvider(this).get(Model.class);
    }

    @Override
    protected void showError(Object obj) {

    }

    @Override
    protected int onCreate() {
        return R.layout.fragment_mark;
    }

    @Override
    protected void initView() {
        page = getArguments().getInt("page", 1);

        if(page==1){
            viewModel.getBookmark();
        }else {
            viewModel.getHistory();
        }

        dataBinding.clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(page==1){
                    viewModel.clearBookmark();
                }else {
                    viewModel.clearHistory();
                }
            }
        });
    }

    @Override
    protected void initData() {

        viewModel.listBookmark.observe(this, new Observer<List<BookmarkEntity>>() {
            @Override
            public void onChanged(List<BookmarkEntity> bookmarkEntities) {
                initBookMark(bookmarkEntities);
            }
        });

        viewModel.listHistory.observe(this, new Observer<List<HistoryEntity>>() {
            @Override
            public void onChanged(List<HistoryEntity> historyEntities) {
                initHistory(historyEntities);
            }
        });
    }

    private void initHistory(List<HistoryEntity> historyEntities) {
        dataBinding.recyc.setLayoutManager(new LinearLayoutManager(context));
        HistoryAdapter adapter = new HistoryAdapter();
        dataBinding.recyc.setAdapter(adapter);
        adapter.setNewData(historyEntities);
        adapter.getDeleteListener(new HistoryAdapter.Delete() {
            @Override
            public void deleteCollect(HistoryEntity bookmark, int p) {
                viewModel.deleteHistory(bookmark);
                adapter.removeDate(bookmark);
            }
        });
    }

    private void initBookMark(List<BookmarkEntity> bookmarkEntities) {
        dataBinding.recyc.setLayoutManager(new LinearLayoutManager(context));
        MarkAdapter adapter = new MarkAdapter();
        dataBinding.recyc.setAdapter(adapter);
        adapter.setNewData(bookmarkEntities);
        adapter.getDeleteListener(new MarkAdapter.Delete() {
            @Override
            public void deleteCollect(BookmarkEntity bookmark, int p) {
                viewModel.deleteBook(bookmark);
                adapter.removeDate(bookmark);
            }
        });
    }
}
