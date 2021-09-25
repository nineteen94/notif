package com.rn_episode1.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.rn_episode1.Models.AppUsageModel;

import java.util.List;

@Dao
public interface AppUsageDao {

    //BASIC

    @Query("SELECT * FROM APPUSAGETABLE")
    List<AppUsageModel> selectAllAppUsageModels();

    @Insert
    void insertAppUsageModel(AppUsageModel appUsageModel);

    @Update
    void updateAppUsageModel(AppUsageModel appUsageModel);

    @Delete
    void deleteAppUsageModel(AppUsageModel appUsageModel);

    @Query("SELECT * FROM APPUSAGETABLE WHERE packageName = :packageName LIMIT 1")
    AppUsageModel selectAppUsageModelByPackageName(String packageName);

    @Query("SELECT * FROM APPUSAGETABLE WHERE isMonitored = 1")
    List<AppUsageModel> selectMonitoredApplications();



    // WEEK USAGE
    @Query("SELECT SUM(lastWeekUsage) FROM APPUSAGETABLE WHERE isMonitored = 1")
    int getCompleteWeekUsageOfMonitoredApps();

    // Evaluate notifications
    @Query("SELECT * FROM APPUSAGETABLE WHERE `temp` = :temp")
    List<AppUsageModel> selectAppUsageModelByTemp(boolean temp);

    // Aggregate Average
    @Query("UPDATE APPUSAGETABLE SET aggregateUsage = aggregateUsage + todayUsage")
    void updateAggregateUsage();

    @Query("UPDATE APPUSAGETABLE SET aggregateDays = aggregateDays + 1")
    void updateAggregateDays();



    //Removing and Adding Apps via Temp

    @Query("UPDATE APPUSAGETABLE SET `temp` = 1")
    void updateTempToTrue();

    @Query("DELETE FROM APPUSAGETABLE WHERE `temp`= 1")
    void deleteTrueTemp();

    @Query("UPDATE APPUSAGETABLE SET `temp`=1 WHERE packageName=:packageName")
    void setTempToFalseByPackageName(String packageName);


    // Weekly Data

    @Query("UPDATE APPUSAGETABLE SET lastWeekUsage = sundayUsage + mondayUsage + tuesdayUsage + wednesdayUsage + thursdayUsage + fridayUsage + saturdayUsage")
    void setLastWeekUsage();

    //Handling changes in apps from UI

    @Query("UPDATE APPUSAGETABLE SET isMonitored = 0")
    void resetIsMonitored();

    //Reset data

    @Query("UPDATE APPUSAGETABLE SET lastWeekUsage = 0")
    void resetLastWeekUsage();

    @Query("UPDATE APPUSAGETABLE SET sundayUsage = 0, mondayUsage = 0, tuesdayUsage = 0, wednesdayUsage = 0, thursdayUsage = 0, fridayUsage = 0, saturdayUsage = 0")
    void resetWeekDaysData();

    @Query("UPDATE APPUSAGETABLE SET todayUsage = 0")
    void resetTodayUsage();


    //Transfer data to respective day

    @Query("UPDATE APPUSAGETABLE SET sundayUsage = todayUsage")
    void transferSunday();

    @Query("UPDATE APPUSAGETABLE SET mondayUsage = todayUsage")
    void transferMonday();

    @Query("UPDATE APPUSAGETABLE SET tuesdayUsage = todayUsage")
    void transferTuesday();

    @Query("UPDATE APPUSAGETABLE SET wednesdayUsage = todayUsage")
    void transferWednesday();

    @Query("UPDATE APPUSAGETABLE SET thursdayUsage = todayUsage")
    void transferThursday();

    @Query("UPDATE APPUSAGETABLE SET fridayUsage = todayUsage")
    void transferFriday();

    @Query("UPDATE APPUSAGETABLE SET saturdayUsage = todayUsage")
    void transferSaturday();

}

























