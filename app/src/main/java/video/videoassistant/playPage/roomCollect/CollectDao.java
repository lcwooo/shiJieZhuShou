package video.videoassistant.playPage.roomCollect;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import video.videoassistant.me.jointManage.JointEntity;
import video.videoassistant.me.jsonManage.JsonEntity;

@Dao
public interface CollectDao {

    @Query("select * from CollectEntity where url=:url")
    List<CollectEntity> getUrlList(String url);


    @Query("delete  from CollectEntity where url=:url")
    void deleteOne(String url);

    @Query("select * from CollectEntity ORDER BY id desc")
    List<CollectEntity> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CollectEntity entity);

    @Delete
    void delete(JointEntity entity);

    @Query("delete  from CollectEntity")
    void deleteAll();

}
