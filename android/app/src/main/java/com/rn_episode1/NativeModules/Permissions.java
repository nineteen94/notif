package com.rn_episode1.NativeModules;

import static com.rn_episode1.MainActivity.PACKAGE_NAME;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.judemanutd.autostarter.AutoStartPermissionHelper;

public class Permissions extends ReactContextBaseJavaModule {

    Permissions(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "Permissions";
    }

    private static final String TAG = Permissions.class.getSimpleName();

    @ReactMethod
    public void checkUsageStatsPermissions (Promise promise) {

        Context context = getReactApplicationContext();

        try {

            PackageManager packageManager = context.getPackageManager();

            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);

            AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);

            int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName);

            promise.resolve(mode == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            promise.reject("Something Went Wrong", e);
        }
    }

    @ReactMethod
    public void getUsageStatsPermissions() {

        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS, Uri.parse("package:"+PACKAGE_NAME));

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Context context = getReactApplicationContext();

        context.startActivity(intent);
    }

    @ReactMethod
    public void getIgnoreBatteryOptimizationPermission() {

        Intent intent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Context context = getReactApplicationContext();

        context.startActivity(intent);
    }

    @ReactMethod
    public void checkIgnoreBatteryOptimizationPermission (Promise promise) {

        Context context = getReactApplicationContext();
        try {
            String packageName = context.getPackageName();

            PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

            boolean permissionStatus = powerManager.isIgnoringBatteryOptimizations(packageName);

            promise.resolve(permissionStatus);

        } catch (Exception e) {
            promise.reject(TAG, e.getMessage());
        }
    }

    @ReactMethod
    public void getAutoStartPermission () {
        AutoStartPermissionHelper.Companion.getInstance().getAutoStartPermission(getReactApplicationContext(), true, true);
    }

}





















