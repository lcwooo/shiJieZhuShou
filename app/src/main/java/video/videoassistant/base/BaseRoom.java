package video.videoassistant.base;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import video.videoassistant.me.handleManage.HandleDao;
import video.videoassistant.me.handleManage.HandleEntity;
import video.videoassistant.me.jointManage.JointDao;
import video.videoassistant.me.jointManage.JointEntity;
import video.videoassistant.me.jsonManage.JsonDao;
import video.videoassistant.me.jsonManage.JsonEntity;
import video.videoassistant.me.urlManage.CollectionUrlDao;
import video.videoassistant.me.urlManage.CollectionUrlEntity;
import video.videoassistant.playPage.roomCollect.CollectDao;
import video.videoassistant.playPage.roomCollect.CollectEntity;


@Database(entities = {CollectionUrlEntity.class, JointEntity.class,
        JsonEntity.class, HandleEntity.class, CollectEntity.class}, version = 1)
public abstract class BaseRoom extends RoomDatabase {

    public abstract CollectionUrlDao urlTypeDao();

    public abstract JointDao getJointDao();

    public abstract JsonDao getJsonDao();

    public abstract HandleDao getHandleDao();

    public abstract CollectDao getCollectDao();

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
