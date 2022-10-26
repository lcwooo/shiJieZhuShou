package video.videoassistant.me.jointManage;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;




@Dao
public interface JointDao {

    @Query("select * from jointentity")
    List<JointEntity> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(JointEntity entity);


}
