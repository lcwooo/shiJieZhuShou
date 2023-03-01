package video.videoassistant.browserPage.browserRoom;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import video.videoassistant.me.jointManage.JointEntity;
import video.videoassistant.playPage.roomCollect.CollectEntity;

@Dao
public interface BookmarkDao {

    @Query("select * from bookmarkentity ORDER BY id desc")
    List<BookmarkEntity> getAll();

    @Query("delete  from bookmarkentity where url=:url")
    void deleteOne(String url);

    @Query("delete  from bookmarkentity")
    void deleteAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(BookmarkEntity entity);

    @Delete
    void delete(BookmarkEntity entity);
}
