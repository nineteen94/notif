package com.rn_episode1.Util;

import static com.rn_episode1.Util.Helpers.getCurrentTimeStamp;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.rn_episode1.Database.AppDatabase;
import com.rn_episode1.Models.AppUsageModel;
import com.rn_episode1.Models.WorkerLogModel;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.Arrays;

public final class OptimizedAppSetup {

    private static final String TAG = "Vishal" + OptimizedAppSetup.class.getSimpleName();

    private static final String[] preMonitoredApps = {"com.facebook.katana", "com.instagram.android", "com.twitter.android", "com.google.android.youtube"};

    private static final String[] preInstalledApps = {"com.google.android.youtube", "com.facebook.katana"};


    private static boolean isInstalledAndNotSystemApplication (String packageName, PackageManager packageManager) {

        try {

            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);

            boolean isNonSystemApplication = Arrays.asList(preInstalledApps).contains(packageName);

            return (applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0 || isNonSystemApplication;

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    @NonNull
    private static Bitmap getBitmapFromDrawable(@NonNull Drawable drawable) {
        final Bitmap bmp = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bmp);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bmp;
    }

    private static String getIconURI(Context context ,String packageName) {

        Drawable icon = null;
        try {
            icon = context.getPackageManager().getApplicationIcon(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }

        Bitmap bitmap = getBitmapFromDrawable(icon);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

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

    public static boolean incomingAppCheck(Context context, String packageName) {

        // this returns false if app is not a playstore app. and if it is and it doesn't exist in the DB, it creates the entry

        PackageManager packageManager = context.getPackageManager();

        boolean isInstalledAndNotSystemApplication = isInstalledAndNotSystemApplication(packageName, packageManager);

        if(!isInstalledAndNotSystemApplication) return false;

        AppDatabase appDatabase = AppDatabase.getInstance(context);

        AppUsageModel appUsageModel = appDatabase.appUsageDao().selectAppUsageModelByPackageName(packageName);

        if(appUsageModel == null) {

            ApplicationInfo applicationInfo = null;
            String applicationName = "";
            String applicationCategory = "C-1";

            try {
                applicationInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            if(applicationInfo != null) {

                applicationName = (String) packageManager.getApplicationLabel(applicationInfo);

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    applicationCategory = "C" + applicationInfo.category;
                }

            }

            String uri = getIconURI(context ,packageName);

            boolean isMonitoredApp = Arrays.asList(preMonitoredApps).contains(packageName);

            AppUsageModel newAppUsageModel = new AppUsageModel(packageName, applicationName, applicationCategory,uri , 0,-1,0,0,0,0,0,0,0,0,0,isMonitoredApp, true, 0, 0);

            appDatabase.appUsageDao().insertAppUsageModel(newAppUsageModel);

        }

        return true;
    }


    public static boolean outgoingAppCheck(Context context, String packageName) {

        try {
            PackageManager packageManager = context.getPackageManager();

            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);

            return true;

        } catch (PackageManager.NameNotFoundException e) {

            AppDatabase appDatabase = AppDatabase.getInstance(context);

            appDatabase.appUsageDao().deleteAppUsageModelByPackageName(packageName);

            return false;
        }

    }



}
