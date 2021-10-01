package com.rn_episode1.Models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "notificationtable")
public class NotificationModel {

    @PrimaryKey(autoGenerate = true) int id;

    String notificationID;
    String title;
    String message;
    String imagePath;

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    byte[] imageByteArray;

    String segment;
    String category;

    public NotificationModel(String notificationID, String title, String message,String imagePath, byte[] imageByteArray ,String segment, String category){
        this.notificationID = notificationID;
        this.title = title;
        this.message = message;
        this.imagePath = imagePath;
        this.imageByteArray = imageByteArray;
        this.segment = segment;
        this.category = category;
    }


    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public String getNotificationID() {
        return this.notificationID;
    }

    public void setNotificationID(String notificationID) {
        this.notificationID = notificationID;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public String getImagePath() {
        return this.imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public byte[] getImageByteArray() {
        return this.imageByteArray;
    }

    public void setImageByteArray(byte[] imageByteArray) {
        this.imageByteArray = imageByteArray;
    }

    public String getSegment() {
        return this.segment;
    }

    public void setSegment(String segment) {
        this.segment = segment;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
