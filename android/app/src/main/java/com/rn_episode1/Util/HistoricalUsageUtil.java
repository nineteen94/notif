package com.rn_episode1.Util;

import static com.rn_episode1.Util.Constants.HISTORICAL_DATA_DAYS;
import static com.rn_episode1.Util.Constants.MILLI_IN_DAY;
import static com.rn_episode1.Util.Constants.MILLI_IN_MINUTE;
import static com.rn_episode1.Util.Helpers.getString;
import static com.rn_episode1.Util.OptimizedAppSetup.incomingAppCheck;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.rn_episode1.Database.AppDatabase;
import com.rn_episode1.Models.AppUsageModel;
import com.rn_episode1.R;

import java.util.Map;

public final class HistoricalUsageUtil {

    private static final String TAG = "Vishal" + HistoricalUsageUtil.class.getSimpleName();

    public static void initializeHistoricalUsage(Context context, long endTime, boolean setLastFetch) {

        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();

        Log.d(TAG, methodName + " Starts!");

        try {

            long startTime = endTime - MILLI_IN_DAY * Constants.HISTORICAL_DATA_DAYS;

            UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);

            PackageManager packageManager = context.getPackageManager();

            Map<String, UsageStats> data = usageStatsManager.queryAndAggregateUsageStats(startTime, endTime);

            AppDatabase appDatabase = AppDatabase.getInstance(context);

            for(Map.Entry<String, UsageStats> entry: data.entrySet()){

                String packageName = entry.getValue().getPackageName();

                if(incomingAppCheck(context, packageName)) {

                    AppUsageModel appUsageModel = appDatabase.appUsageDao().selectAppUsageModelByPackageName(packageName);

                    long usageInMilli = entry.getValue().getTotalTimeInForeground();

                    double usageInMinutes =  (double) usageInMilli / MILLI_IN_MINUTE;

                    PackageInfo packageInfo = packageManager.getPackageInfo(packageName,0);

                    long firstInstallTime = packageInfo.firstInstallTime;

                    long appInstallDurationInMilli = endTime - firstInstallTime;

                    if(appInstallDurationInMilli >= MILLI_IN_DAY * HISTORICAL_DATA_DAYS) {

                        int averageTimeSpentInMinutes = (int) Math.round( usageInMinutes / HISTORICAL_DATA_DAYS);

                        appUsageModel.setAverageUsage(averageTimeSpentInMinutes);
                    }

                    appUsageModel.setHistoricalUsage((int) usageInMinutes);

                    appDatabase.appUsageDao().updateAppUsageModel(appUsageModel);
                }
            }

            if(setLastFetch) {

                SharedPreferences appVariableFile = context.getSharedPreferences(getString(context, R.string.APP_VARIABLE_FILE_KEY), Context.MODE_PRIVATE);

                SharedPreferences.Editor appVariableFileEditor = appVariableFile.edit();

                appVariableFileEditor.putLong(getString(context, R.string.LAST_USAGE_DATA_FETCH), endTime);

                appVariableFileEditor.apply();
            }


        } catch (Exception e) {
            Log.d(TAG + methodName, e.getMessage());
        }
    }
}
