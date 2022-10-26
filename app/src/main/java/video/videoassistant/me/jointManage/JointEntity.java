package video.videoassistant.me.jointManage;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class JointEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int Id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "remark")
    public String remark;

    @ColumnInfo(name = "url")
    public String url;

    @ColumnInfo(name = "position")
    public int position;

    @ColumnInfo(name = "type")
    public String type;


    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "JointEntity{" +
                "Id=" + Id +
                ", name='" + name + '\'' +
                ", remark='" + remark + '\'' +
                ", url='" + url + '\'' +
                ", position=" + position +
                ", type='" + type + '\'' +
                '}';
    }
}
