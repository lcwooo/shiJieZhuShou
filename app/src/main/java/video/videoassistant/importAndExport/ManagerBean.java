package video.videoassistant.importAndExport;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

public class ManagerBean {
    @JSONField(name = "webDy")
    public List<String> webList;

    @JSONField(name = "jsonDy")
    public List<String> jointList;

    @JSONField(name = "jsonJx")
    public List<String> jsonJx;

    @JSONField(name = "webJx")
    public List<String> webJx;


    public List<String> getWebList() {
        return webList;
    }

    public void setWebList(List<String> webList) {
        this.webList = webList;
    }

    public List<String> getJointList() {
        return jointList;
    }

    public void setJointList(List<String> jointList) {
        this.jointList = jointList;
    }

    public List<String> getJsonJx() {
        return jsonJx;
    }

    public void setJsonJx(List<String> jsonJx) {
        this.jsonJx = jsonJx;
    }

    public List<String> getWebJx() {
        return webJx;
    }

    public void setWebJx(List<String> webJx) {
        this.webJx = webJx;
    }


    @Override
    public String toString() {
        return "ManagerBean{" +
                "webList=" + webList +
                ", jointList=" + jointList +
                ", jsonJx=" + jsonJx +
                ", webJx=" + webJx +
                '}';
    }
}
