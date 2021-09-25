package com.rn_episode1.Models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity (tableName = "workerlogtable")
public class WorkerLogModel {

    @PrimaryKey(autoGenerate = true)
    int id;

    String fetchFrom;

    String fetchTill;

    int use;

    String app;

    public WorkerLogModel(String fetchFrom, String fetchTill, int use, String app) {
        this.fetchFrom = fetchFrom;
        this.fetchTill = fetchTill;
        this.use = use;
        this.app = app;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFetchFrom() {
        return this.fetchFrom;
    }

    public void setFetchFrom(String fetchFrom) {
        this.fetchFrom = fetchFrom;
    }

    public String getFetchTill() {
        return this.fetchTill;
    }

    public void setFetchTill(String fetchTill) {
        this.fetchTill = fetchTill;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public void setUse(int use) {
        this.use = use;
    }

    public int getUse(){
        return this.use;
    }

    public String getApp() {
        return this.app;
    }
}
