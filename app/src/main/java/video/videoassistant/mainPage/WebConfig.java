package video.videoassistant.mainPage;

import android.content.Context;
import android.util.Log;

import com.yanzhenjie.andserver.annotation.Config;
import com.yanzhenjie.andserver.framework.website.StorageWebsite;

@Config
public class WebConfig implements com.yanzhenjie.andserver.framework.config.WebConfig {

    @Override
    public void onConfig(Context context, Delegate delegate) {
        String fs = context.getExternalFilesDir("playList").getAbsolutePath();
        Log.i("MainActivity", "onConfig: "+fs);
        delegate.addWebsite(new StorageWebsite(fs,"playList"));
    }


}
