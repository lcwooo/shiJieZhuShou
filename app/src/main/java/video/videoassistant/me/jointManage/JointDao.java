package video.videoassistant.me.jointManage;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import video.videoassistant.me.urlManage.CollectionUrlEntity;


@Dao
public interface JointDao {

    @Query("select * from jointentity ORDER BY position asc")
    List<JointEntity> getAll();

    @Query("select * from JointEntity where url=:url")
    List<JointEntity> getUrlList(String url);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(JointEntity entity);

    @Update
    void update(JointEntity entity);

    @Delete
    void delete(JointEntity entity);

}
