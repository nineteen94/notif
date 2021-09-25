package com.rn_episode1.Util;


import static com.rn_episode1.Util.Constants.TIME_MAP_START;
import static com.rn_episode1.Util.Helpers.getDateFromMilli;
import static com.rn_episode1.Util.Helpers.getTimeMap;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.rn_episode1.Database.AppDatabase;
import com.rn_episode1.Models.AppUsageModel;
import com.rn_episode1.Models.WorkerLogModel;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public final class ApplicationsUtil {

    private static final String TAG = "Vishal" + ApplicationsUtil.class.getSimpleName();

    private static final String[] preMonitoredApps = {"com.facebook.katana", "com.instagram.android", "com.twitter.android", "com.google.android.youtube"};

    private static final String[] preInstalledApps = {"com.google.android.youtube"};

    public static void initializeApplications (Context context) {

        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();

        try {

            PackageManager packageManager = context.getPackageManager();

            List<ApplicationInfo> installedApps = packageManager.getInstalledApplications(0);

            AppDatabase appDatabase = AppDatabase.getInstance(context);

            for(ApplicationInfo applicationInfo : installedApps) {

                String packageName = applicationInfo.packageName;

                boolean isPreInstalledApp = Arrays.asList(preInstalledApps).contains(packageName);

                if((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0 && !isPreInstalledApp) {

                    continue;
                }

                AppUsageModel appUsageModel = appDatabase.appUsageDao().selectAppUsageModelByPackageName(packageName);

                if(appUsageModel != null) {

                    appUsageModel.setTemp(false);

                    appDatabase.appUsageDao().updateAppUsageModel(appUsageModel);

                } else {

                    String uri = getIconURI(context ,packageName);

                    boolean isPreMonitoredApp = Arrays.asList(preMonitoredApps).contains(packageName);

                    AppUsageModel newAppUsageModel = new AppUsageModel(packageName, uri , 0,-1,0,0,0,0,0,0,0,0,0,isPreMonitoredApp, false, 0, 0);

                    appDatabase.appUsageDao().insertAppUsageModel(newAppUsageModel);

                }

            }

            List<AppUsageModel> listOfTrueTemp = appDatabase.appUsageDao().selectAppUsageModelByTemp(true);

            Map<String, Long> timeMap = getTimeMap(context, false);

            String currentTime = getDateFromMilli(timeMap.get(TIME_MAP_START));

            for(int i = 0; i < listOfTrueTemp.size(); i ++) {

                AppUsageModel appUsageModel = listOfTrueTemp.get(i);

                String packageName = appUsageModel.getPackageName();

                String marker = "correct deleted";

                boolean isInstalled = isPackageInstalled(packageName, packageManager);

                if(isInstalled) {

                    marker = "false deleted";

                    appDatabase.appUsageDao().setTempToFalseByPackageName(packageName);
                }

                WorkerLogModel workerLogModel = new WorkerLogModel(currentTime,packageName,appUsageModel.getAggregateUsage(),"deleted");

                appDatabase.workerLogDao().insertWorkerLogData(workerLogModel);
            }

            appDatabase.appUsageDao().deleteTrueTemp();

            appDatabase.appUsageDao().updateTempToTrue();

        } catch (Exception e) {
            Log.d(TAG + methodName, e.getMessage());
        }
    }

    private static boolean isPackageInstalled (String packageName, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }


    private static String getIconURI(Context context ,String packageName) {

        Drawable icon = null;
        try {
            icon = context.getPackageManager().getApplicationIcon(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        Bitmap bitmap = getBitmapFromDrawable(icon);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bitmapdata = stream.toByteArray();

        String fileName = packageName + "ICON";

        try {
            FileOutputStream fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fileOutputStream.write(bitmapdata);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Uri uri =  Uri.fromFile(context.getFileStreamPath(fileName));

        return uri.toString();
    }

    @NonNull
    private static Bitmap getBitmapFromDrawable(@NonNull Drawable drawable) {
        final Bitmap bmp = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bmp);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bmp;
    }


}

/*

            List<AppUsageModel> appUsageModelList = appDatabase.appUsageDao().selectAllAppUsageModels();

            if(appUsageModelList.size() == 0) {

                List<PackageInfo> installedApps = context.getPackageManager().getInstalledPackages(0);

                for(int i = 0; i < installedApps.size(); i ++) {

                    PackageInfo packageInfo = installedApps.get(i);

                    ApplicationInfo applicationInfo = packageInfo.applicationInfo;

                    String packageName = packageInfo.packageName;

                    boolean isPreMonitoredApp = Arrays.asList(preMonitoredApps).contains(packageName);

                    boolean isPreInstalledApp = Arrays.asList(preInstalledApps).contains(packageName);

                    if(((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) && !isPreInstalledApp) {
                        continue;
                    }

                    String uri = getIconURI(context, packageName);

                    AppUsageModel appUsageModel = new AppUsageModel(packageName, uri,0,0,0,0,0,0,0,0,0,0,0, isPreMonitoredApp , true,0, 0);

                    appDatabase.appUsageDao().insertAppUsageModel(appUsageModel);



    List<AppUsageModel> appUsageModelList = appDatabase.appUsageDao().selectAllAppUsageModels();

            if(appUsageModelList.size() == 0) {

                    } else {

                    List<PackageInfo> packageInfoList = context.getPackageManager().getInstalledPackages(0);

        for(int i = 0; i < packageInfoList.size(); i ++) {

        PackageInfo packageInfo = packageInfoList.get(i);

        ApplicationInfo applicationInfo = packageInfo.applicationInfo;

        String packageName = packageInfo.packageName;

        AppUsageModel appUsageModel = appDatabase.appUsageDao().selectAppUsageModelByPackageName(packageName);

        if(appUsageModel != null) {

        appUsageModel.setTemp(false);

        appDatabase.appUsageDao().updateAppUsageModel(appUsageModel);

        continue;
        }


        boolean isPreMonitoredApp = Arrays.asList(preMonitoredApps).contains(packageName);

        boolean isPreInstalledApp = Arrays.asList(preInstalledApps).contains(packageName);

        if(((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) && !isPreInstalledApp) {
        continue;
        }

        boolean appIsMonitored = isPreMonitoredApp;

        String uri = getIconURI(context ,packageName);

        AppUsageModel newAppUsageModel = new AppUsageModel(packageName, uri , 0,0,0,0,0,0,0,0,0,0,0,appIsMonitored, false, 0, 0);

        appDatabase.appUsageDao().insertAppUsageModel(newAppUsageModel);

        }

        List<AppUsageModel> listOfTrueTemp = appDatabase.appUsageDao().selectAppUsageModelByTemp(true);

        Map<String, Long> timeMap = getTimeMap(context, false);

        String currentTime = getDateFromMilli(timeMap.get(TIME_MAP_START));

        for(int i = 0; i < listOfTrueTemp.size(); i ++) {

        AppUsageModel appUsageModel = listOfTrueTemp.get(i);

        String packageName = appUsageModel.getPackageName();

        WorkerLogModel workerLogModel = new WorkerLogModel(currentTime,packageName,appUsageModel.getAggregateUsage(),"deleted");

        appDatabase.workerLogDao().insertWorkerLogData(workerLogModel);

        }

        appDatabase.appUsageDao().deleteTrueTemp();

        appDatabase.appUsageDao().updateTempToTrue();

        }
 */
