package video.videoassistant.cloudPage;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

public class TypeBean{


    /**
     * type_id : 1
     * type_name : 电影
     */

    @JSONField(name = "type_id")
    public String typeId;
    @JSONField(name = "type_name")
    public String typeName;

    public TypeBean() {
    }

    public TypeBean(String typeId, String typeName) {
        this.typeId = typeId;
        this.typeName = typeName;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}
