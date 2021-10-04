package com.dexter_time.Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.dexter_time.Models.WorkerLogModel;

import java.util.List;

@Dao
public interface WorkerLogDao {
    @Query("SELECT * FROM workerlogtable")
    List<WorkerLogModel> selectAllLogs();

    @Insert
    void insertWorkerLogData(WorkerLogModel workerLogModel);

}
