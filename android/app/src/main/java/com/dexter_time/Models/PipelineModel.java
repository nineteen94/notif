package com.dexter_time.Models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity (tableName = "pipelinetable")
public class PipelineModel {

    @PrimaryKey(autoGenerate = true)
    int id;

    String applicationNamePlaceholder;

    String applicationUsagePlaceholder;

    String notificationSegment;

    long startTime;

    long endTime;

    boolean isFired;

    String reportPlaceholder;

    String packageName; // Optional - Later On can be used to fire notification when the app is active

    public PipelineModel(String notificationSegment, String applicationNamePlaceholder, String applicationUsagePlaceholder, String reportPlaceholder, long startTime, long endTime, boolean isFired  ,String packageName) {
        this.notificationSegment = notificationSegment;
        this.applicationNamePlaceholder = applicationNamePlaceholder;
        this.applicationUsagePlaceholder = applicationUsagePlaceholder;
        this.reportPlaceholder = reportPlaceholder;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isFired = isFired;
        this.packageName = packageName;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public void setNotificationSegment(String notificationSegment) {
        this.notificationSegment = notificationSegment;
    }

    public String getNotificationSegment() {
        return this.notificationSegment;
    }

    public void setApplicationNamePlaceholder(String applicationNamePlaceholder) {
        this.applicationNamePlaceholder = applicationNamePlaceholder;
    }

    public String getApplicationNamePlaceholder() {
        return this.applicationNamePlaceholder;
    }

    public void setApplicationUsagePlaceholder(String applicationUsagePlaceholder) {
        this.applicationUsagePlaceholder = applicationUsagePlaceholder;
    }

    public String getApplicationUsagePlaceholder() {
        return this.applicationUsagePlaceholder;
    }

    public void setReportPlaceholder(String reportPlaceholder) {
        this.reportPlaceholder = reportPlaceholder;
    }

    public String getReportPlaceholder() {
        return this.reportPlaceholder;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getEndTime() {
        return this.endTime;
    }

    public void setIsFired(boolean isFired) {
        this.isFired = isFired;
    }

    public boolean isFired() {
        return this.isFired;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPackageName() {
        return this.packageName;
    }
}
