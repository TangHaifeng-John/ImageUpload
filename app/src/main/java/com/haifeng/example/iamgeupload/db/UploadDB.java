package com.haifeng.example.iamgeupload.db;


import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.haifeng.example.iamgeupload.common.ImageApplication;


/**
 * 广告数据库
 */
@Database(entities = {UploadEntity.class}, version = 1, exportSchema = false)
 public abstract class UploadDB extends RoomDatabase {
    private static UploadDB sInstance;
    private static final Object sLock = new Object();
    private static final String DATABASE_NAME = "upload.db";

    public abstract UploadEntityDao getUploadDao();

    public static UploadDB getInstance() {
        synchronized (sLock) {
            if (sInstance == null) {
                sInstance =
                        Room.databaseBuilder(ImageApplication.Companion.getInstance(), UploadDB.class,
                                DATABASE_NAME)
                                .build();
            }
            return sInstance;
        }
    }
}
