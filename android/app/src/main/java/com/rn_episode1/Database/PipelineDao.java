package com.rn_episode1.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.rn_episode1.Models.PipelineModel;

import java.util.List;

@Dao
public interface PipelineDao {

    @Query("SELECT * FROM PIPELINETABLE " +
            "WHERE isFired = 0 " +
            "AND endTime > :currentTime " +
            "AND startTime < :currentTime " +
            "ORDER BY startTime LIMIT 1")
    PipelineModel selectPipelineModel(long currentTime);

    @Insert
    void insertPipelineModel(PipelineModel pipelineModel);

    @Delete
    void deletePipelineModel(PipelineModel pipelineModel);

    @Update
    void updatePipelineModel(PipelineModel pipelineModel);

    @Query("DELETE FROM PIPELINETABLE WHERE isFired = 1 OR endTime < :currentTime")
    void cleanPipeline(long currentTime);

    @Query("DELETE FROM PIPELINETABLE")
    void cleanAllPipeline();

}
