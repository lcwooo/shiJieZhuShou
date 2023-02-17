package video.videoassistant.collectPage;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.azhon.basic.adapter.BaseDBRVAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import video.videoassistant.BR;
import video.videoassistant.R;
import video.videoassistant.cloudPage.XmlMovieBean;
import video.videoassistant.databinding.AdapterCollectMovieBinding;
import video.videoassistant.databinding.ItemCollectMoviesBinding;

public class MovieListAdapter extends BaseDBRVAdapter<XmlMovieBean, AdapterCollectMovieBinding> {
    public MovieListAdapter() {
        super(R.layout.adapter_collect_movie, BR.bean);
    }

    @Override
    protected void initData(AdapterCollectMovieBinding binding, XmlMovieBean xmlMovieBean, int position) {
        binding.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (delete != null) {
                    delete.deleteCollect(xmlMovieBean, position);
                }
            }
        });
    }

    @BindingAdapter({"imageUrl"})
    public static void loadImage(ImageView imageView, String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (url.contains("mac")) {
            Glide.with(imageView.getContext()).applyDefaultRequestOptions(getRequestOptions()).load(url.replace("mac", "http"))
                    .into(imageView);
        } else {
            Glide.with(imageView.getContext()).applyDefaultRequestOptions(getRequestOptions()).load(url)
                    .into(imageView);
        }
    }

    @SuppressLint("CheckResult")
    public static RequestOptions getRequestOptions() {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.error(R.drawable.error);
        requestOptions.skipMemoryCache(false);
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        requestOptions.fitCenter();
        return requestOptions;
    }

    public interface Delete {
        void deleteCollect(XmlMovieBean xmlMovieBean, int p);
    }

    public Delete delete;

    public void getDeleteListener(Delete delete) {
        this.delete = delete;
    }
}
