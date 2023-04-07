package video.videoassistant.playPage.roomCollect;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;



@Entity
public class RememberEntity {



    @PrimaryKey(autoGenerate = false)
    @NonNull
    public String url;

    @ColumnInfo(name = "time")
    public Long time;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}
