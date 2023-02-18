package video.videoassistant.collectPage;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.View;
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
import video.videoassistant.databinding.AdapterCollectMovieBinding;
import video.videoassistant.databinding.ItemCollectMoviesBinding;
import video.videoassistant.playPage.roomCollect.CollectEntity;

public class MovieListAdapter extends BaseDBRVAdapter<CollectEntity, AdapterCollectMovieBinding> {
    public MovieListAdapter() {
        super(R.layout.adapter_collect_movie, BR.bean);
    }

    @Override
    protected void initData(AdapterCollectMovieBinding binding, CollectEntity xmlMovieBean, int position) {
        XmlMovieBean bean = JSON.parseObject(xmlMovieBean.getJson(), XmlMovieBean.class);
        loadImage(binding.img,bean.getPic());
        binding.name.setText(bean.getName());
        binding.yanyuan.setText(bean.getDirector());
        binding.jie.setText(bean.getInfo());

        binding.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (delete != null) {
                    delete.deleteCollect(xmlMovieBean, position);
                }
            }
        });
    }


    public  void loadImage(ImageView imageView, String url) {
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
        void deleteCollect(CollectEntity xmlMovieBean, int p);
    }

    public Delete delete;

    public void getDeleteListener(Delete delete) {
        this.delete = delete;
    }
}
