package video.videoassistant.me.urlManage;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CollectionUrlDao {

    @Query("select * from CollectionUrlEntity ORDER BY position asc")
    List<CollectionUrlEntity> getAll();

    @Query("select * from CollectionUrlEntity where url=:url")
    List<CollectionUrlEntity> getUrlList(String url);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CollectionUrlEntity entity);

    @Update
    void update(CollectionUrlEntity entity);

    @Delete
    void delete(CollectionUrlEntity entity);


}
