package video.videoassistant.playPage.roomCollect;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface RememberDao {

    @Query("select * from rememberentity where url=:url")
    RememberEntity get(String url);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(RememberEntity entity);

    @Delete
    void delete(RememberEntity entity);

    @Update
    void updateUser(RememberEntity entity);


}
