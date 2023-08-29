package video.videoassistant.mainPage;

import com.alibaba.fastjson.annotation.JSONField;

public class OtherBean {


    @JSONField(name = "type")
    private int type;
    @JSONField(name = "tittle")
    private String tittle;
    @JSONField(name = "data")
    private String data;

    @JSONField(name = "isShow")
    private boolean isShow;

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTittle() {
        return tittle;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
