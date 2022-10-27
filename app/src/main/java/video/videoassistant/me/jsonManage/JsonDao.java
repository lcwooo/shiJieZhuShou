package video.videoassistant.me.jsonManage;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;



@Dao
public interface JsonDao {

    @Query("select * from JsonEntity ORDER BY position asc")
    List<JsonEntity> getAll();

    @Query("select * from JsonEntity where url=:url")
    List<JsonEntity> getUrlList(String url);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(JsonEntity entity);

    @Update
    void update(JsonEntity entity);

    @Delete
    void delete(JsonEntity entity);


}
