package com.rn_episode1.Util;

import static com.rn_episode1.Util.Constants.DEFAULT_AVERAGE_USAGE;
import static com.rn_episode1.Util.Constants.MILLI_IN_DAY;
import static com.rn_episode1.Util.Constants.MILLI_IN_MINUTE;
import static com.rn_episode1.Util.Constants.TIME_MAP_DAY_OF_WEEK;
import static com.rn_episode1.Util.Constants.TIME_MAP_START;
import static com.rn_episode1.Util.Constants.TIME_MAP_START_OF_THE_DAY;
import static com.rn_episode1.Util.Constants.TIME_MAP_START_OF_THE_WEEK;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.rn_episode1.Database.AppDatabase;
import com.rn_episode1.Models.AppUsageModel;
import com.rn_episode1.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Helpers {

    private static final String TAG = "Vishal" + Helpers.class.getSimpleName();

    public static int getAverageUsage (AppUsageModel appUsageModel) {

        return appUsageModel.getAverageUsage() == -1
                ? (appUsageModel.getAggregateUsage() == 0
                ? DEFAULT_AVERAGE_USAGE
                : appUsageModel.getAggregateUsage() / (60 * appUsageModel.getAggregateDays()))
                : appUsageModel.getAverageUsage();
    }

    public static Bitmap getAppIconBitMap (Context context ,String packageName) {

        try {
            Drawable icon = context.getPackageManager().getApplicationIcon(packageName);

            Bitmap bitmap;

            if (icon instanceof BitmapDrawable) {

                BitmapDrawable bitmapDrawable = (BitmapDrawable) icon;

                if(bitmapDrawable.getBitmap() != null) {

                    return bitmapDrawable.getBitmap();
                }
            }

            if(icon.getIntrinsicWidth() <= 0 || icon.getIntrinsicHeight() <= 0) {

                bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel

            } else {

                bitmap = Bitmap.createBitmap(icon.getIntrinsicWidth(), icon.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(bitmap);

            icon.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());

            icon.draw(canvas);

            return bitmap;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Map<String, Long> getTimeMap(Context context, boolean getLastFetch) {

        Map<String , Long> timeMap = new HashMap<>();

        Calendar calendar = Calendar.getInstance();

        if(getLastFetch) {
            SharedPreferences appVariableFile = context.getSharedPreferences(getString(context, R.string.APP_VARIABLE_FILE_KEY), Context.MODE_PRIVATE);
            long lastUsageDataFetch = appVariableFile.getLong(getString(context, R.string.LAST_USAGE_DATA_FETCH), 0);
            calendar.setTimeInMillis(lastUsageDataFetch);
        }

        timeMap.put(TIME_MAP_START, calendar.getTimeInMillis());

        timeMap.put(TIME_MAP_DAY_OF_WEEK, (long) calendar.get(Calendar.DAY_OF_WEEK));

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        timeMap.put(TIME_MAP_START_OF_THE_DAY, calendar.getTimeInMillis());

        timeMap.put(TIME_MAP_START_OF_THE_WEEK, calendar.getTimeInMillis() - (calendar.get(Calendar.DAY_OF_WEEK) - 1) * MILLI_IN_DAY);

        return timeMap;
    }

    public static String getDateFromMilli (long milli) {

        DateFormat formatter = new SimpleDateFormat("dd-MMM HH:mm");

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milli);

        return formatter.format(calendar.getTime());
    }

    public static String getString(Context context , int x) {
        try{
            final String string = context.getResources().getString(x);
            return string;
        } catch (Exception e) {
            return "ERR";
        }
    }

    public static void setColdStartWork(Context context, long time) {

        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();

        Log.d(TAG, methodName + " Starts!");

        SharedPreferences appVariableFile = context.getSharedPreferences(getString(context, R.string.APP_VARIABLE_FILE_KEY), Context.MODE_PRIVATE);

        SharedPreferences.Editor appVariableFileEditor = appVariableFile.edit();

        appVariableFileEditor.putLong(getString(context, R.string.COLD_START_WORK_START_OF_DAY), time);

        appVariableFileEditor.apply();
    }

    public static void transferDayUsage(Context context, int dayOfTheWeek) {

        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();

        Log.d(TAG, methodName + " Starts!");

        AppDatabase appDatabase = AppDatabase.getInstance(context);

        appDatabase.appUsageDao().updateAggregateUsage();

        appDatabase.appUsageDao().updateAggregateDays();

        switch (dayOfTheWeek) {
            case 1:
                appDatabase.appUsageDao().transferSunday();
                break;
            case 2:
                appDatabase.appUsageDao().transferMonday();
                break;
            case 3:
                appDatabase.appUsageDao().transferTuesday();
                break;
            case 4:
                appDatabase.appUsageDao().transferWednesday();
                break;
            case 5:
                appDatabase.appUsageDao().transferThursday();
                break;
            case 6:
                appDatabase.appUsageDao().transferFriday();
                break;
            case 7:
                appDatabase.appUsageDao().transferSaturday();
                break;
            default:
                break;
        }
    }

    public static void resetLiveNotificationTrackerFile(Context context) {

        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();

        Log.d(TAG, methodName + " Starts!");

        SharedPreferences liveNotificationTrackerFile = context.getSharedPreferences(getString(context, R.string.LIVE_NOTIFICATION_TRACKER_FILE_KEY), Context.MODE_PRIVATE);

        SharedPreferences.Editor liveNotificationTrackerFileEditor = liveNotificationTrackerFile.edit();

        Map<String, ?> allTrackers = liveNotificationTrackerFile.getAll();

        for(Map.Entry<String, ?> entry: allTrackers.entrySet()){
            String key = entry.getKey();
            liveNotificationTrackerFileEditor.putInt(key, 0);
            liveNotificationTrackerFileEditor.putInt(key, 0);

        }
        liveNotificationTrackerFileEditor.apply();
    }
}
