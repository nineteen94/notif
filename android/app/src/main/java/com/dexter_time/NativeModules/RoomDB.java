package com.dexter_time.NativeModules;

import static com.dexter_time.Util.Constants.APPMODEL_APPNAME;
import static com.dexter_time.Util.Constants.APPMODEL_AVERAGEUSAGE;
import static com.dexter_time.Util.Constants.APPMODEL_DAYOFTHEWEEK;
import static com.dexter_time.Util.Constants.APPMODEL_HISTORICALUSAGE;
import static com.dexter_time.Util.Constants.APPMODEL_ISMONITORED;
import static com.dexter_time.Util.Constants.APPMODEL_LASTWEEKUSAGE;
import static com.dexter_time.Util.Constants.APPMODEL_PACKAGENAME;
import static com.dexter_time.Util.Constants.APPMODEL_THISWEEKUSAGE;
import static com.dexter_time.Util.Constants.APPMODEL_URI;
import static com.dexter_time.Util.Constants.APPMODEL_WEEKDAYSUSAGE;
import static com.dexter_time.Util.Helpers.getAverageUsage;
import static com.dexter_time.Util.Helpers.getString;
import static com.dexter_time.Util.OptimizedAppSetup.outgoingAppCheck;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.dexter_time.Database.AppDatabase;
import com.dexter_time.Database.AppExecutors;
import com.dexter_time.Models.AppUsageModel;
import com.dexter_time.R;
import com.dexter_time.Util.Constants;
import com.dexter_time.Util.Helpers;

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

    private int reformatUsage (int usage) {

        usage = (int) Math.round((double) usage / 60);

        if(usage >= 60) {
            return (int) Math.round((double) usage / 10) * 10;
        } else if(usage >= 30) {
            return (int) Math.round((double) usage / 5) * 5;
        } else {
            return usage;
        }
    }

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

                    String packageName = appUsageModel.getPackageName();

                    if( outgoingAppCheck(context, packageName) ) {

                        WritableMap appMap = new WritableNativeMap();

                        String appName = (String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA));

                        WritableArray weekUsage = new WritableNativeArray();

                        int thisWeekUsage = 0;

                        if(dayOfThWeek > 1) {
                            weekUsage.pushInt( reformatUsage(appUsageModel.getSundayUsage()));
                        }
                        if(dayOfThWeek > 2) {
                            weekUsage.pushInt( reformatUsage(appUsageModel.getMondayUsage()));
                        }
                        if(dayOfThWeek > 3) {
                            weekUsage.pushInt(reformatUsage( appUsageModel.getTuesdayUsage()));
                        }
                        if(dayOfThWeek > 4) {
                            weekUsage.pushInt(reformatUsage( appUsageModel.getWednesdayUsage()));
                        }
                        if(dayOfThWeek > 5) {
                            weekUsage.pushInt(reformatUsage( appUsageModel.getThursdayUsage()));
                        }
                        if(dayOfThWeek > 6) {
                            weekUsage.pushInt(reformatUsage(appUsageModel.getFridayUsage()));
                        }

                        weekUsage.pushInt(reformatUsage( appUsageModel.getTodayUsage()));

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
    public void isUserInfoAvailable(Promise promise) {
        Context context = getReactApplicationContext();
        SharedPreferences mainFile = context.getSharedPreferences(getString(context, R.string.APP_VARIABLE_FILE_KEY), Context.MODE_PRIVATE);
        promise.resolve(mainFile.getBoolean(getString(context, R.string.USER_INFO_AVAILABLE), false));
    }

    @ReactMethod
    public void setUserInfo(String name, String pronoun) {
        Context context = getReactApplicationContext();
        SharedPreferences mainFile = context.getSharedPreferences(getString(context, R.string.APP_VARIABLE_FILE_KEY), Context.MODE_PRIVATE);
        SharedPreferences.Editor mainFileEditor = mainFile.edit();
        mainFileEditor.putString(getString(context, R.string.USER_NAME), name);
        mainFileEditor.putString(getString(context, R.string.USER_PRONOUN), pronoun);
        mainFileEditor.putBoolean(getString(context, R.string.USER_INFO_AVAILABLE), true);
        mainFileEditor.apply();
    }

    @ReactMethod
    public void getUserName(Promise promise) {
        Context context = getReactApplicationContext();
        SharedPreferences mainFile = context.getSharedPreferences(getString(context, R.string.APP_VARIABLE_FILE_KEY), Context.MODE_PRIVATE);
        promise.resolve(mainFile.getString(getString(context, R.string.USER_NAME), ""));
    }

    @ReactMethod
    public void getUserPronoun(Promise promise) {
        Context context = getReactApplicationContext();
        SharedPreferences mainFile = context.getSharedPreferences(getString(context, R.string.APP_VARIABLE_FILE_KEY), Context.MODE_PRIVATE);
        promise.resolve(mainFile.getString(getString(context, R.string.USER_PRONOUN), ""));
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




















