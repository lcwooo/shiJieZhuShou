package video.videoassistant.mainPage;

import com.alibaba.fastjson.annotation.JSONField;

public class RuleVersionBean {

    @JSONField(name = "code")
    public String code;
    @JSONField(name = "url")
    public String url;
    @JSONField(name = "version")
    public int version;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
