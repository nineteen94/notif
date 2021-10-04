package com.dexter_time.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.dexter_time.Models.NotificationModel;

import java.util.List;

@Dao
public interface NotificationDao {
    @Query("SELECT * FROM NOTIFICATIONTABLE")
    List<NotificationModel> selectAllNotifications();

    @Insert
    void insertNotification(NotificationModel notificationModel);

    @Update
    void updateNotification(NotificationModel notificationModel);

    @Delete
    void deleteNotification(NotificationModel notificationModel);

    @Query("SELECT * FROM NOTIFICATIONTABLE WHERE segment LIKE :segment AND category IN (:category, :baseCategory)  ORDER BY RANDOM() LIMIT 1")
    NotificationModel selectNotificationModel(String segment, String category, String baseCategory);
}
