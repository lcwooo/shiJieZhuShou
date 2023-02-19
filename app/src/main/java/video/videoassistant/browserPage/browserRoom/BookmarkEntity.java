package video.videoassistant.browserPage.browserRoom;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class BookmarkEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int Id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "url")
    public String url;

    @ColumnInfo(name = "time")
    public String time;


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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
