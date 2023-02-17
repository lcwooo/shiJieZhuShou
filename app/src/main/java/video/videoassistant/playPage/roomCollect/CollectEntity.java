package video.videoassistant.playPage.roomCollect;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class CollectEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int Id;

    @ColumnInfo(name = "name")
    public String name;


    @ColumnInfo(name = "url")
    public String url;

    @ColumnInfo(name = "json")
    public String json;

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

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    @Override
    public String toString() {
        return "CollectEntity{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", json='" + json + '\'' +
                '}';
    }
}
