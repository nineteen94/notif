package com.rn_episode1.Database;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.rn_episode1.Models.AppUsageModel;
import com.rn_episode1.Models.NotificationModel;
import com.rn_episode1.Models.PipelineModel;
import com.rn_episode1.Models.WorkerLogModel;

@Database(entities = {AppUsageModel.class, WorkerLogModel.class, NotificationModel.class, PipelineModel.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static final String TAG = AppDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "appdatabase";
    private static AppDatabase sInstance;

    public static AppDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(TAG, "Creating new database instance");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, AppDatabase.DATABASE_NAME)
                        .build();
            }
        }
        Log.d(TAG, "Getting the database instance");
        return sInstance;
    }

    public abstract AppUsageDao appUsageDao();
    public abstract WorkerLogDao workerLogDao();
    public abstract NotificationDao notificationDao();
    public abstract PipelineDao pipelineDao();
}
