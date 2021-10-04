package com.dexter_time.Util;

import static com.dexter_time.Util.Constants.MILLI_IN_SECOND;
import static com.dexter_time.Util.Helpers.getString;
import static com.dexter_time.Util.OptimizedAppSetup.incomingAppCheck;

import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.dexter_time.Database.AppDatabase;
import com.dexter_time.Models.AppUsageModel;
import com.dexter_time.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class WeekdaysUsageUtil {

    private static final String TAG = "Vishal" + WeekdaysUsageUtil.class.getSimpleName();

    public static void initOrUpdateTodayUsage(Context context, long startTime, long endTime) {

        int _RESUMED;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            _RESUMED = UsageEvents.Event.ACTIVITY_RESUMED;
        } else {
            _RESUMED = UsageEvents.Event.MOVE_TO_FOREGROUND;
        }

        int _PAUSED;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            _PAUSED = UsageEvents.Event.ACTIVITY_PAUSED;
        } else {
            _PAUSED = UsageEvents.Event.MOVE_TO_BACKGROUND;
        }

        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();

        Log.d(TAG, methodName + " Starts!");

        try {
            AppDatabase appDatabase = AppDatabase.getInstance(context);

            UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);

            UsageEvents usageEvents = usageStatsManager.queryEvents(startTime, endTime);

//            if(usageEvents == null) {
//                return;
//            }


            Map<String, List<UsageEvents.Event>> usageEventsMap = new HashMap<>();

            while(usageEvents.hasNextEvent()) {

                UsageEvents.Event currentEvent = new UsageEvents.Event();

                usageEvents.getNextEvent(currentEvent);

                String packageName = currentEvent.getPackageName();

                if(incomingAppCheck(context, packageName)) {

                    AppUsageModel appUsageModel = appDatabase.appUsageDao().selectAppUsageModelByPackageName(packageName);

                    int eventType = currentEvent.getEventType();

                    if(eventType == _RESUMED || eventType == _PAUSED) {
                        if(usageEventsMap.get(packageName) == null) {
                            usageEventsMap.put(packageName, new ArrayList<UsageEvents.Event>());
                        }
                        usageEventsMap.get(packageName).add(currentEvent);
                    }
                }

            }

            updateInDB(context, usageEventsMap, startTime, endTime);

        } catch (Exception e) {
            Log.d(TAG + methodName, e.getMessage());
        }
    }

    private static void updateInDB(Context context ,Map<String, List<UsageEvents.Event>> usageEventsMap, long startTime, long endTime) {

        int _RESUMED;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            _RESUMED = UsageEvents.Event.ACTIVITY_RESUMED;
        } else {
            _RESUMED = UsageEvents.Event.MOVE_TO_FOREGROUND;
        }

        int _PAUSED;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            _PAUSED = UsageEvents.Event.ACTIVITY_PAUSED;
        } else {
            _PAUSED = UsageEvents.Event.MOVE_TO_BACKGROUND;
        }

        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();

        Log.d(TAG, methodName + " Starts!");

        try{

            AppDatabase appDatabase = AppDatabase.getInstance(context);

            SharedPreferences appVariableFile = context.getSharedPreferences(getString(context, R.string.APP_VARIABLE_FILE_KEY), Context.MODE_PRIVATE);

            SharedPreferences.Editor appVariableFileEditor = appVariableFile.edit();

            for(Map.Entry<String,List<UsageEvents.Event>> usageEvent: usageEventsMap.entrySet()) {

                int totalEvents = usageEvent.getValue().size();

                String packageName = usageEvent.getKey();

                AppUsageModel appUsageModel = appDatabase.appUsageDao().selectAppUsageModelByPackageName(packageName);

                int packageUsage = 0;

                if(totalEvents > 1) {
                    for (int i = 0; i < totalEvents - 1; i++) {
                        UsageEvents.Event E0 = usageEvent.getValue().get(i);
                        UsageEvents.Event E1 = usageEvent.getValue().get(i + 1);

                        if (E0.getEventType() == _RESUMED && E1.getEventType() == _PAUSED) {
                            long diffInMilli = E1.getTimeStamp() - E0.getTimeStamp();
                            int diffInSeconds = (int) (diffInMilli/MILLI_IN_SECOND);
                            packageUsage += diffInSeconds;
                        }
                    }
                }

                if(usageEvent.getValue().get(0).getEventType() == _PAUSED) {
                    long diffInMilli = usageEvent.getValue().get(0).getTimeStamp() - startTime;
                    int diffInSeconds = (int) (diffInMilli/MILLI_IN_SECOND);
                    packageUsage += diffInSeconds;
                }

                if(usageEvent.getValue().get(totalEvents-1).getEventType() == _RESUMED) {
                    long diffInMilli = endTime - usageEvent.getValue().get(totalEvents-1).getTimeStamp();
                    int diffInSeconds = (int) (diffInMilli/MILLI_IN_SECOND);
                    packageUsage += diffInSeconds;
                }

                appUsageModel.setTodayUsage(appUsageModel.getTodayUsage() + packageUsage);

                appDatabase.appUsageDao().updateAppUsageModel(appUsageModel);
            }

            appVariableFileEditor.putLong(getString(context, R.string.LAST_USAGE_DATA_FETCH), endTime);

            appVariableFileEditor.apply();

        } catch(Exception e) {
            Log.d(TAG + methodName, e.getMessage() + Arrays.toString(e.getStackTrace()));
        }
    }


}






















