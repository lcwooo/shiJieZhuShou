package video.videoassistant.playPage;

import com.alibaba.fastjson.annotation.JSONField;

public class PlayBean {
    public String name;
    @JSONField(name = "url")
    public String url;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
