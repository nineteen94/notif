package com.rn_episode1.Models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "appusagetable")
public class AppUsageModel {

    @PrimaryKey(autoGenerate = true) int id;
    String packageName;
    String appName;
    String appCategory;
    String uri;
    int historicalUsage;
    int averageUsage;
    int sundayUsage;
    int mondayUsage;
    int tuesdayUsage;
    int wednesdayUsage;
    int thursdayUsage;
    int fridayUsage;
    int saturdayUsage;
    int todayUsage;
    int lastWeekUsage;
    boolean isMonitored;
    boolean temp;
    int aggregateUsage;
    int aggregateDays;



    public AppUsageModel(String packageName, String appName,String appCategory,String uri, int historicalUsage, int averageUsage, int sundayUsage, int mondayUsage,
                         int tuesdayUsage, int wednesdayUsage, int thursdayUsage, int fridayUsage, int saturdayUsage , int todayUsage, int lastWeekUsage ,
                         boolean isMonitored, boolean temp, int aggregateUsage, int aggregateDays) {

        this.packageName = packageName;
        this.appName = appName;
        this.appCategory = appCategory;
        this.uri = uri;
        this.historicalUsage = historicalUsage;
        this.averageUsage = averageUsage;
        this.sundayUsage = sundayUsage;
        this.mondayUsage = mondayUsage;
        this.tuesdayUsage = tuesdayUsage;
        this.wednesdayUsage = wednesdayUsage;
        this.thursdayUsage = thursdayUsage;
        this.fridayUsage = fridayUsage;
        this.saturdayUsage = saturdayUsage;
        this.todayUsage = todayUsage;
        this.lastWeekUsage = lastWeekUsage;
        this.isMonitored = isMonitored;
        this.temp = temp;
        this.aggregateUsage = aggregateUsage;
        this.aggregateDays = aggregateDays;

    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public String getAppName() {
        return this.appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppCategory() {
        return this.appCategory;
    }

    public void setAppCategory(String appCategory) {
        this.appCategory = appCategory;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getUri() {
        return this.uri;
    }

    public void setHistoricalUsage (int historicalUsage) {
        this.historicalUsage = historicalUsage;
    }

    public int getHistoricalUsage () {
        return this.historicalUsage;
    }

    public int getAverageUsage() {
        return this.averageUsage;
    }

    public void setAverageUsage(int averageUsage) {
        this.averageUsage = averageUsage;
    }

    public void setSundayUsage(int sundayUsage) {
        this.sundayUsage = sundayUsage;
    }

    public int getSundayUsage() {
        return this.sundayUsage;
    }

    public void setMondayUsage(int mondayUsage) {
        this.mondayUsage = mondayUsage;
    }

    public int getMondayUsage() {
        return this.mondayUsage;
    }

    public void setTuesdayUsage(int tuesdayUsage) {
        this.tuesdayUsage = tuesdayUsage;
    }

    public int getTuesdayUsage() {
        return this.tuesdayUsage;
    }

    public void setWednesdayUsage(int wednesdayUsage) {
        this.wednesdayUsage = wednesdayUsage;
    }

    public int getWednesdayUsage() {
        return this.wednesdayUsage;
    }

    public void setThursdayUsage(int thursdayUsage) {
        this.thursdayUsage = thursdayUsage;
    }

    public int getThursdayUsage() {
        return this.thursdayUsage;
    }

    public void setFridayUsage(int fridayUsage) {
        this.fridayUsage = fridayUsage;
    }

    public int getFridayUsage() {
        return this.fridayUsage;
    }

    public void setSaturdayUsage(int saturdayUsage) {
        this.saturdayUsage = saturdayUsage;
    }

    public int getSaturdayUsage() {
        return this.saturdayUsage;
    }

    public void setTodayUsage(int todayUsage) {
        this.todayUsage = todayUsage;
    }

    public int getTodayUsage() {
        return this.todayUsage;
    }

    public void setLastWeekUsage(int lastWeekUsage) {
        this.lastWeekUsage = lastWeekUsage;
    }

    public int getLastWeekUsage() {
        return this.lastWeekUsage;
    }

    public void setIsMonitored (boolean isMonitored) {
        this.isMonitored = isMonitored;
    }

    public boolean getIsMonitored () {
        return this.isMonitored;
    }

    public boolean getTemp () {
        return  this.temp;
    }

    public void setTemp (boolean temp) {
        this.temp = temp;
    }

    public int getAggregateUsage() {
        return this.aggregateUsage;
    }

    public void setAggregateUsage(int aggregateUsage) {
        this.aggregateUsage = aggregateUsage;
    }

    public void setAggregateDays(int aggregateDays) {
        this.aggregateDays = aggregateDays;
    }

    public int getAggregateDays() {
        return this.aggregateDays;
    }
}
