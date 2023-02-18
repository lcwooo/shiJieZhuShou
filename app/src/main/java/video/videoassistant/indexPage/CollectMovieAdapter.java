package video.videoassistant.indexPage;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.alibaba.fastjson.JSON;
import com.azhon.basic.adapter.BaseDBRVAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import video.videoassistant.BR;
import video.videoassistant.R;
import video.videoassistant.cloudPage.XmlMovieBean;
import video.videoassistant.databinding.ItemCollectMoviesBinding;
import video.videoassistant.playPage.roomCollect.CollectEntity;

public class CollectMovieAdapter extends BaseDBRVAdapter<CollectEntity, ItemCollectMoviesBinding> {
    public CollectMovieAdapter() {
        super(R.layout.item_collect_movies, BR.bean);
    }

    @Override
    protected void initData(ItemCollectMoviesBinding binding, CollectEntity xmlMovieBean, int position) {
        XmlMovieBean bean = JSON.parseObject(xmlMovieBean.getJson(), XmlMovieBean.class);
        loadImage(binding.img,bean.getPic());
        binding.remark.setText(bean.getNote());
        binding.name.setText(bean.getName());
    }


    public void loadImage(ImageView imageView, String url) {
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
}
