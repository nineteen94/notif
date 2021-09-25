package com.rn_episode1.NativeModules;

import static com.rn_episode1.Util.Constants.APPMODEL_APPNAME;
import static com.rn_episode1.Util.Constants.APPMODEL_AVERAGEUSAGE;
import static com.rn_episode1.Util.Constants.APPMODEL_DAYOFTHEWEEK;
import static com.rn_episode1.Util.Constants.APPMODEL_HISTORICALUSAGE;
import static com.rn_episode1.Util.Constants.APPMODEL_ISMONITORED;
import static com.rn_episode1.Util.Constants.APPMODEL_LASTWEEKUSAGE;
import static com.rn_episode1.Util.Constants.APPMODEL_PACKAGENAME;
import static com.rn_episode1.Util.Constants.APPMODEL_THISWEEKUSAGE;
import static com.rn_episode1.Util.Constants.APPMODEL_URI;
import static com.rn_episode1.Util.Constants.APPMODEL_WEEKDAYSUSAGE;
import static com.rn_episode1.Util.Helpers.getAverageUsage;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;
import com.rn_episode1.Database.AppDatabase;
import com.rn_episode1.Database.AppExecutors;
import com.rn_episode1.Models.AppUsageModel;
import com.rn_episode1.Models.PipelineModel;
import com.rn_episode1.Util.Constants;
import com.rn_episode1.Util.Helpers;

import java.util.List;
import java.util.Map;

public class RoomDB extends ReactContextBaseJavaModule {

    RoomDB(ReactApplicationContext context) {
        super(context);
    }

    @Override
    public String getName() {
        return "RoomDB";
    }

    private static final String TAG = RoomDB.class.getSimpleName();

    @ReactMethod
    public void loadData(Promise promise) {

        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();

        Context context = getReactApplicationContext();

        AppExecutors.getInstance().diskIO().execute(() -> {

            try {

                Map<String, Long> currentTimeMap = Helpers.getTimeMap(context, false);

                int dayOfThWeek = Integer.parseInt(String.valueOf(currentTimeMap.get(Constants.TIME_MAP_DAY_OF_WEEK)));

                AppDatabase appDatabase = AppDatabase.getInstance(context);

                List<AppUsageModel> appUsageModelList = appDatabase.appUsageDao().selectAllAppUsageModels();

                PackageManager packageManager = context.getPackageManager();

                WritableArray result = new WritableNativeArray();

                for(int i = 0; i < appUsageModelList.size(); i ++) {

                    AppUsageModel appUsageModel = appUsageModelList.get(i);

                    WritableMap appMap = new WritableNativeMap();

                    String packageName = appUsageModel.getPackageName();

                    String appName = (String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA));

                    WritableArray weekUsage = new WritableNativeArray();

                    int thisWeekUsage = 0;

                    if(dayOfThWeek > 1) {
                        weekUsage.pushInt((int) Math.round((double) appUsageModel.getSundayUsage() / 60));
                    }
                    if(dayOfThWeek > 2) {
                        weekUsage.pushInt((int) Math.round((double) appUsageModel.getMondayUsage() / 60));
                    }
                    if(dayOfThWeek > 3) {
                        weekUsage.pushInt((int) Math.round((double) appUsageModel.getTuesdayUsage() / 60));
                    }
                    if(dayOfThWeek > 4) {
                        weekUsage.pushInt((int) Math.round((double) appUsageModel.getWednesdayUsage() / 60));
                    }
                    if(dayOfThWeek > 5) {
                        weekUsage.pushInt((int) Math.round((double) appUsageModel.getThursdayUsage() / 60));
                    }
                    if(dayOfThWeek > 6) {
                        weekUsage.pushInt((int) Math.round((double) appUsageModel.getFridayUsage() / 60));
                    }

                    weekUsage.pushInt((int) Math.round((double) appUsageModel.getTodayUsage() / 60));



                    for(int j = 0; j < weekUsage.size(); j ++) {
                        thisWeekUsage += weekUsage.getInt(j);
                    }

                    for(int j = weekUsage.size(); j < 7; j ++) {
                        weekUsage.pushNull();
                    }

                    appMap.putArray(APPMODEL_WEEKDAYSUSAGE, weekUsage);

                    appMap.putString(APPMODEL_PACKAGENAME, packageName);

                    appMap.putString(APPMODEL_APPNAME, appName);

                    appMap.putBoolean(APPMODEL_ISMONITORED, appUsageModel.getIsMonitored());

                    appMap.putInt(APPMODEL_HISTORICALUSAGE, appUsageModel.getHistoricalUsage());

                    appMap.putInt(APPMODEL_LASTWEEKUSAGE, (int) Math.round((double) appUsageModel.getLastWeekUsage() / 60));

                    appMap.putString(APPMODEL_URI, appUsageModel.getUri());

                    appMap.putInt(APPMODEL_AVERAGEUSAGE, getAverageUsage(appUsageModel));

                    appMap.putInt(APPMODEL_THISWEEKUSAGE, thisWeekUsage);

                    result.pushMap(appMap);

                }

                WritableMap appMap = new WritableNativeMap();

                appMap.putInt(APPMODEL_DAYOFTHEWEEK, dayOfThWeek);

                result.pushMap(appMap);

                promise.resolve(result);

            } catch (Exception e){
                Log.d(TAG + methodName, e.getMessage());
                promise.reject(TAG + methodName, e.getMessage());
            }

        });

    }


    @ReactMethod
    public void resetPipeline(Promise promise) {
        Context context = getReactApplicationContext();

        AppExecutors.getInstance().diskIO().execute(() -> {

            try {
                AppDatabase appDatabase = AppDatabase.getInstance(context);

                appDatabase.pipelineDao().cleanAllPipeline();

                promise.resolve(true);
            } catch(Exception e) {

                promise.reject(TAG, e.getMessage());
            }



        });
    }

    @ReactMethod
    public void setAppsToMonitor(ReadableArray selectedApps, Promise promise) {

        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();

        Context context = getReactApplicationContext();

        AppExecutors.getInstance().diskIO().execute(() -> {
            try{
                AppDatabase appDatabase = AppDatabase.getInstance(context);

                appDatabase.appUsageDao().resetIsMonitored();

                if(selectedApps == null || selectedApps.size() == 0) {
                    promise.resolve(true);
                    return;
                }

                for(int i = 0; i < selectedApps.size(); i ++) {

                    String packageName = selectedApps.getString(i);

                    AppUsageModel appUsageModel = appDatabase.appUsageDao().selectAppUsageModelByPackageName(packageName);

                    appUsageModel.setIsMonitored(true);

                    appDatabase.appUsageDao().updateAppUsageModel(appUsageModel);
                }

                promise.resolve(true);

            } catch (Exception e) {

                Log.d(TAG + methodName, e.getMessage());

                promise.reject(TAG + methodName, e.getMessage());
            }
        });
    }
}




















