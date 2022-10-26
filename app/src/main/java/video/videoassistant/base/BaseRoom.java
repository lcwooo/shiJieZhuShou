package video.videoassistant.base;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import video.videoassistant.me.jointManage.JointDao;
import video.videoassistant.me.jointManage.JointEntity;
import video.videoassistant.me.urlManage.CollectionUrlDao;
import video.videoassistant.me.urlManage.CollectionUrlEntity;


@Database(entities = {CollectionUrlEntity.class, JointEntity.class}, version = 1)
public abstract class BaseRoom extends RoomDatabase {

    public abstract CollectionUrlDao urlTypeDao();

    public abstract JointDao getJointDao();

    // 单例
    private static BaseRoom database;

    public static BaseRoom getInstance(Context context) {
        if (database == null) {
            synchronized (BaseRoom.class) {
                if (database == null) {
                    database = Room.databaseBuilder(context.getApplicationContext(),
                            BaseRoom.class, "SharesDb.db").build();
                }
            }
        }
        return database;
    }

}
