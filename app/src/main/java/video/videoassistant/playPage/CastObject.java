package video.videoassistant.playPage;

import androidx.annotation.NonNull;

import com.android.cast.dlna.core.ICast;

public class CastObject implements ICast.ICastVideo {

    private String url;
    private String id;
    private String name;


    public CastObject(String url, String id, String name) {
        this.url = url;
        this.id = id;
        this.name = name;
    }

    @Override
    public long getDurationMillSeconds() {
        return 0;
    }

    @Override
    public long getSize() {
        return 0;
    }

    @Override
    public long getBitrate() {
        return 0;
    }

    @NonNull
    @Override
    public String getId() {
        return id;
    }

    @NonNull
    @Override
    public String getUri() {
        return url;
    }

    @Override
    public String getName() {
        return name;
    }
}
