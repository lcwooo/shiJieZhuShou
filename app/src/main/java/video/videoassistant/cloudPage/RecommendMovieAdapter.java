package video.videoassistant.cloudPage;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.azhon.basic.adapter.BaseDBRVAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import video.videoassistant.BR;
import video.videoassistant.R;
import video.videoassistant.databinding.ItemRecommendMoviesBinding;


public class RecommendMovieAdapter extends BaseDBRVAdapter<MovieBean, ItemRecommendMoviesBinding> {

    public RecommendMovieAdapter() {
        super(R.layout.item_recommend_movies, BR.bean);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initData(ItemRecommendMoviesBinding binding, MovieBean movieBean, int p) {

        binding.hot.setText(movieBean.getVodHitsMonth() + "");
        if (!TextUtils.isEmpty(movieBean.getVodRemarks())) {
            if (movieBean.getVodRemarks().contains("更新至")) {
                binding.remark.setText(movieBean.getVodRemarks().replace("更新至",
                        ""));
            } else {
                binding.remark.setText(movieBean.getVodRemarks());
            }
        }

    }


    @BindingAdapter({"imageUrl"})
    public static void loadImage(ImageView imageView, String url) {
        if(TextUtils.isEmpty(url)){
            return;
        }
        if (url.contains("mac")) {
            Glide.with(imageView.getContext()).applyDefaultRequestOptions(getRequestOptions()).load(url.replace("mac", "http"))
                    .into(imageView);
        }else {
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
}
