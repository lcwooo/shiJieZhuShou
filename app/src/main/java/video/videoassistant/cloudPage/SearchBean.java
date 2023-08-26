package video.videoassistant.cloudPage;

import com.alibaba.fastjson.annotation.JSONField;

public class SearchBean {

    @JSONField(name = "from")
    private String from;

    @JSONField(name = "data")
    private String data;

    @JSONField(name = "url")
    private String url;

    public SearchBean() {
    }

    public SearchBean(String from, String data,String url) {
        this.from = from;
        this.data = data;
        this.url = url;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
