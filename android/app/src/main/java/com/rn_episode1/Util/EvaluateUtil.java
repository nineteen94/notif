package com.rn_episode1.Util;

import static com.rn_episode1.Util.Constants.DEFAULT_AVERAGE_USAGE;
import static com.rn_episode1.Util.Constants.MILLI_IN_DAY;
import static com.rn_episode1.Util.Constants.MILLI_IN_HOUR;
import static com.rn_episode1.Util.Constants.MILLI_IN_MINUTE;
import static com.rn_episode1.Util.Constants.MIN_GAP_BW_LIVE_NOTIFICATIONS;
import static com.rn_episode1.Util.Constants.NOTIFICATION_SEGMENT_DAILY_BAD;
import static com.rn_episode1.Util.Constants.NOTIFICATION_SEGMENT_LIVE_FIRST;
import static com.rn_episode1.Util.Constants.NOTIFICATION_SEGMENT_LIVE_SECOND;
import static com.rn_episode1.Util.Constants.NOTIFICATION_SEGMENT_WEEKLY_CUMULATIVE;
import static com.rn_episode1.Util.Constants.NOTIFICATION_SEGMENT_WEEKLY_REPORT;
import static com.rn_episode1.Util.Constants.NOTIFICATION_SEGMENT_DAILY_GOOD;
import static com.rn_episode1.Util.Constants.TIME_MAP_DAY_OF_WEEK;
import static com.rn_episode1.Util.Constants.TIME_MAP_START;
import static com.rn_episode1.Util.Constants.TIME_MAP_START_OF_THE_DAY;
import static com.rn_episode1.Util.Constants.reportHead;
import static com.rn_episode1.Util.Constants.reportTailBad;
import static com.rn_episode1.Util.Constants.reportTailGood;
import static com.rn_episode1.Util.Helpers.getAverageUsage;
import static com.rn_episode1.Util.Helpers.getString;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.util.Log;

import com.rn_episode1.Database.AppDatabase;
import com.rn_episode1.Models.AppUsageModel;
import com.rn_episode1.Models.PipelineModel;
import com.rn_episode1.R;

import java.lang.reflect.AccessibleObject;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public final class EvaluateUtil {

    private static final String TAG = "Vishal" + EvaluateUtil.class.getSimpleName();

    public static void evaluateDaily(Context context, Map<String, Long> currentTimeMap) {

        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();

        Log.d(TAG, methodName + " Starts!");

        try {
            long dayOfTheWeekLong = currentTimeMap.get(TIME_MAP_DAY_OF_WEEK);

            int dayOfTheWeek = Integer.parseInt(String.valueOf(dayOfTheWeekLong)) - 1;

            AppDatabase appDatabase = AppDatabase.getInstance(context);

            List<AppUsageModel> appUsageModelList = appDatabase.appUsageDao().selectMonitoredApplications();

            long startTime = currentTimeMap.get(TIME_MAP_START_OF_THE_DAY) + MILLI_IN_HOUR * 8;

            long endTime = startTime + MILLI_IN_HOUR * 2;

            for(int i = 0; i < appUsageModelList.size(); i ++) {

                AppUsageModel appUsageModel = appUsageModelList.get(i);

                int packageUsage = 0;

                switch (dayOfTheWeek) {
                    case 1:
                        packageUsage = appUsageModel.getSundayUsage();
                        break;
                    case 2:
                        packageUsage = appUsageModel.getMondayUsage();
                        break;
                    case 3:
                        packageUsage = appUsageModel.getTuesdayUsage();
                        break;
                    case 4:
                        packageUsage = appUsageModel.getWednesdayUsage();
                        break;
                    case 5:
                        packageUsage = appUsageModel.getThursdayUsage();
                        break;
                    case 6:
                        packageUsage = appUsageModel.getFridayUsage();
                        break;
                    case 7:
                        packageUsage = appUsageModel.getSaturdayUsage();
                        break;
                }

                String packageUsageGoodBehaviorPlaceholder;

                if(packageUsage != 0) {
                    packageUsageGoodBehaviorPlaceholder = "<" + (packageUsage / 60 + 1);
                } else {
                    packageUsageGoodBehaviorPlaceholder = "0";
                }

                packageUsage = (int) Math.round((double) packageUsage / 60);

                String packageUsageBadBehaviorPlaceholder;

                packageUsageBadBehaviorPlaceholder =  String.valueOf( 5 * (int) Math.round((double) packageUsage / 5) );

                int averageUsage = getAverageUsage(appUsageModel);

                PackageManager packageManager = context.getPackageManager();

                String packageName = appUsageModel.getPackageName();

                String applicationName = (String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA));

                PipelineModel pipelineModel = new PipelineModel(null, applicationName, null, null, startTime, endTime, false, packageName);

                if(packageUsage < 0.25 * averageUsage) {

                    pipelineModel.setApplicationUsagePlaceholder(packageUsageGoodBehaviorPlaceholder);

                    pipelineModel.setNotificationSegment(NOTIFICATION_SEGMENT_DAILY_GOOD);

                    appDatabase.pipelineDao().insertPipelineModel(pipelineModel);

                    break;

                } else if (packageUsage > 2.5 * averageUsage) {

                    pipelineModel.setApplicationUsagePlaceholder(packageUsageBadBehaviorPlaceholder);

                    pipelineModel.setNotificationSegment(NOTIFICATION_SEGMENT_DAILY_BAD);

                    appDatabase.pipelineDao().insertPipelineModel(pipelineModel);

                    break;

                }

            }

        } catch(Exception e) {
            Log.d(TAG + methodName, e.getMessage());
        }
    }

    public static void evaluateWeekly(Context context, Map<String, Long> currentTimeMap) {

        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();

        Log.d(TAG, methodName + " Starts!");

        try {
            /*
            AppDatabase appDatabase = AppDatabase.getInstance(context);

            List<AppUsageModel> appUsageModelList = appDatabase.appUsageDao().selectMonitoredApplications();

            StringBuilder report = new StringBuilder();

            int totalUsage = 0;

            for(int i = 0; i < appUsageModelList.size(); i ++) {

                AppUsageModel appUsageModel = appUsageModelList.get(i);

                String packageName = appUsageModel.getPackageName();

                PackageManager packageManager = context.getPackageManager();

                String applicationName = (String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA));

                int packageUsage = appUsageModel.getLastWeekUsage();

                int averageUsage = getAverageUsage(appUsageModel);

                packageUsage = (int) Math.round((double) packageUsage / 60); // avg use in minutes

                totalUsage += packageUsage;

                packageUsage = (int) Math.round((double) packageUsage / 7); // avg use in minutes per day

                int differential = packageUsage - averageUsage;

                int percentChange = (int) Math.round((double) (differential * 100) / averageUsage);

                report.append(reportUtil(applicationName, packageUsage, percentChange));
            }

             */

            /*
            Sum of time spend on all the selected apps  in hours
             */

            long startTime = currentTimeMap.get(TIME_MAP_START_OF_THE_DAY) + MILLI_IN_HOUR * 8;

            long endTime = startTime + MILLI_IN_HOUR * 3;

            AppDatabase appDatabase = AppDatabase.getInstance(context);

            int totalUsage = appDatabase.appUsageDao().getCompleteWeekUsageOfMonitoredApps();

            totalUsage = (int) Math.ceil((double) totalUsage / (60 * 60)); // hours

            PipelineModel pipelineModel = new PipelineModel(NOTIFICATION_SEGMENT_WEEKLY_CUMULATIVE, null ,String.valueOf(totalUsage), null, startTime, endTime, false, null);

            appDatabase.pipelineDao().insertPipelineModel(pipelineModel);

        } catch(Exception e) {
            Log.d(TAG + methodName, e.getMessage());
        }
    }

    private static String reportUtil (String applicationName, int packageUsage, int percentChange) {

        String head = reportHead[ThreadLocalRandom.current().nextInt(0, reportHead.length)];

        String result = head + applicationName + " " + packageUsage + "min";

        if(percentChange > 0) {
            // Bad Behavior
            result = result + " (+" + percentChange + ")" + reportTailBad[ThreadLocalRandom.current().nextInt(0, reportTailBad.length)];
        } else {
            // Good Behavior
            result = result + " (" + percentChange + ")" + reportTailGood[ThreadLocalRandom.current().nextInt(0, reportTailGood.length)];
        }

        return result;
    }

    public static void evaluateLive(Context context, Map<String, Long> currentTimeMap) {

        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();

        Log.d(TAG, methodName + " Starts!");

        try {

            SharedPreferences liveNotificationTrackerFile = context.getSharedPreferences(getString(context, R.string.LIVE_NOTIFICATION_TRACKER_FILE_KEY), Context.MODE_PRIVATE);

            SharedPreferences.Editor liveNotificationTrackerFileEditor = liveNotificationTrackerFile.edit();

            AppDatabase appDatabase = AppDatabase.getInstance(context);

            List<AppUsageModel> appUsageModelList = appDatabase.appUsageDao().selectMonitoredApplications();

            Log.d(TAG + methodName, appUsageModelList.toString());

            PackageManager packageManager = context.getPackageManager();

            boolean anyNotificationFired = false;

            long endTime = Math.min( currentTimeMap.get(TIME_MAP_START) + MILLI_IN_MINUTE * 20, currentTimeMap.get(TIME_MAP_START_OF_THE_DAY) + MILLI_IN_DAY);

            for(int i = 0; i < appUsageModelList.size() ; i ++) {

                AppUsageModel appUsageModel = appUsageModelList.get(i);

                String packageName = appUsageModel.getPackageName();

                String applicationName = (String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA));

                int packageUsage = appUsageModel.getTodayUsage();

                int averageUsage = getAverageUsage(appUsageModel);

                packageUsage = (int) Math.round((double) packageUsage / 60);

                int notificationsFired = liveNotificationTrackerFile.getInt(packageName + getString(context ,R.string.NOTIFICATION_NUM) ,0);

                int lastUsage = liveNotificationTrackerFile.getInt(packageName + getString(context ,R.string.NOTIFICATION_USAGE), 0);

                if(notificationsFired > 1 || packageUsage - lastUsage < MIN_GAP_BW_LIVE_NOTIFICATIONS) {
                    continue;
                }

                String packageUsagePlaceholder = String.valueOf ((int) Math.round((double) packageUsage / 5) * 5);

                if(notificationsFired == 0 && packageUsage > 0.75 * averageUsage) {

                    PipelineModel pipelineModel = new PipelineModel(NOTIFICATION_SEGMENT_LIVE_FIRST, applicationName, packageUsagePlaceholder, null, 0, endTime, false, packageName);

                    appDatabase.pipelineDao().insertPipelineModel(pipelineModel);

                    Log.d(TAG + methodName, pipelineModel.toString());

                    anyNotificationFired = true;


                } else if(notificationsFired == 1 && packageUsage > 1.25 * averageUsage) {

                    PipelineModel pipelineModel = new PipelineModel(NOTIFICATION_SEGMENT_LIVE_SECOND, applicationName, packageUsagePlaceholder , null, 0, endTime, false,  packageName);

                    appDatabase.pipelineDao().insertPipelineModel(pipelineModel);

                    anyNotificationFired = true;
                }

                if(anyNotificationFired) {
                    liveNotificationTrackerFileEditor.putInt(packageName + getString(context, R.string.NOTIFICATION_USAGE), packageUsage);
                    liveNotificationTrackerFileEditor.putInt(packageName + getString(context, R.string.NOTIFICATION_NUM), notificationsFired + 1);
                    liveNotificationTrackerFileEditor.apply();
                    break;
                }

            }

        } catch(Exception e) {
            Log.d(TAG + methodName, e.getMessage());
        }
    }
}

















