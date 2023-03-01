package video.videoassistant.browserPage.browserRoom;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import video.videoassistant.me.jointManage.JointEntity;

@Dao
public interface HistoryDao {

    @Query("select * from historyentity ORDER BY id desc")
    List<HistoryEntity> getAll();

    @Query("delete  from historyentity where url=:url")
    void deleteOne(String url);

    @Query("delete  from historyentity")
    void deleteAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(HistoryEntity entity);

    @Delete
    void delete(HistoryEntity entity);
}
