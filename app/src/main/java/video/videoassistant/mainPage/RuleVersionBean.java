package video.videoassistant.mainPage;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;


public class RuleVersionBean {


    @JSONField(name = "code")
    private String code;
    @JSONField(name = "adUrl")
    private String adUrl;
    @JSONField(name = "adVersion")
    private int adVersion;
    @JSONField(name = "appVersion")
    private int appVersion;
    @JSONField(name = "updateUrl")
    private String updateUrl;

    @JSONField(name = "description")
    private String description;

    @JSONField(name = "other")
    private List<OtherBean> otherBeans;

    public List<OtherBean> getOtherBeans() {
        return otherBeans;
    }

    public void setOtherBeans(List<OtherBean> otherBeans) {
        this.otherBeans = otherBeans;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAdUrl() {
        return adUrl;
    }

    public void setAdUrl(String adUrl) {
        this.adUrl = adUrl;
    }

    public int getAdVersion() {
        return adVersion;
    }

    public void setAdVersion(int adVersion) {
        this.adVersion = adVersion;
    }

    public int getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(int appVersion) {
        this.appVersion = appVersion;
    }

    public String getUpdateUrl() {
        return updateUrl;
    }

    public void setUpdateUrl(String updateUrl) {
        this.updateUrl = updateUrl;
    }
}
