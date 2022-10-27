package video.videoassistant.me.handleManage;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;




@Dao
public interface HandleDao {

    @Query("select * from HandleEntity ORDER BY position asc")
    List<HandleEntity> getAll();

    @Query("select * from HandleEntity where url=:url")
    List<HandleEntity> getUrlList(String url);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(HandleEntity entity);

    @Update
    void update(HandleEntity entity);

    @Delete
    void delete(HandleEntity entity);

}
