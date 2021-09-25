package com.rn_episode1.Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.rn_episode1.Models.WorkerLogModel;

import java.util.List;

@Dao
public interface WorkerLogDao {
    @Query("SELECT * FROM workerlogtable")
    List<WorkerLogModel> selectAllLogs();

    @Insert
    void insertWorkerLogData(WorkerLogModel workerLogModel);

}
