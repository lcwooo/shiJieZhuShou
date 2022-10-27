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
import video.videoassistant.databinding.ItemXmlBinding;


public class XmlAdapter extends BaseDBRVAdapter<XmlMovieBean, ItemXmlBinding> {


    public XmlAdapter() {
        super(R.layout.item_xml, BR.bean);
    }

    @Override
    protected void initData(ItemXmlBinding binding, XmlMovieBean xmlMovieBean, int position) {

    }

    @BindingAdapter({"imageUrl"})
    public static void loadImage(ImageView imageView, String url) {
        if (!TextUtils.isEmpty(url)) {
            if (url.contains("mac")) {
                Glide.with(imageView.getContext()).applyDefaultRequestOptions(getRequestOptions()).load(url.replace("mac", "http"))
                        .into(imageView);
            } else {
                Glide.with(imageView.getContext()).applyDefaultRequestOptions(getRequestOptions()).load(url)
                        .into(imageView);
            }
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
