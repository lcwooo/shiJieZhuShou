package video.videoassistant.cloudPage;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

public class TypeListBean {

    public TypeListBean() {
    }

    @JSONField(name = "class")
    private List<TypeBean> typeBeanList;

    @JSONField(name = "list")
    private List<String> list;

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public List<TypeBean> getTypeBeanList() {
        return typeBeanList;
    }

    public void setTypeBeanList(List<TypeBean> typeBeanList) {
        this.typeBeanList = typeBeanList;
    }
}
