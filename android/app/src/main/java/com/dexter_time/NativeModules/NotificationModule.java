package com.dexter_time.NativeModules;

import static com.dexter_time.Util.Constants.IS_ONE_TIME_WORK;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.dexter_time.Worker.MainWorker;

public class NotificationModule extends ReactContextBaseJavaModule {
    NotificationModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "NotificationModule";
    }

    public static final String TAG = NotificationModule.class.getSimpleName();


    @ReactMethod
    public void startOneTimeWork (Promise promise) {

        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();


        try {

            Data data = new Data.Builder()
                    .putBoolean(IS_ONE_TIME_WORK, true)
                    .build();

            OneTimeWorkRequest oneTimeWorker = new OneTimeWorkRequest.Builder(
                    MainWorker.class)
                    .setInputData(data)
                    .build();

            WorkManager workManager = WorkManager.getInstance(getReactApplicationContext());

            workManager.enqueueUniqueWork("OneTimeWorker", ExistingWorkPolicy.REPLACE,oneTimeWorker);

            AppCompatActivity appCompatActivity = (AppCompatActivity) getReactApplicationContext().getCurrentActivity();

            ContextCompat.getMainExecutor(getReactApplicationContext()).execute(() -> {
                workManager.getWorkInfoByIdLiveData(oneTimeWorker.getId())
                        .observe((LifecycleOwner) appCompatActivity, new Observer<WorkInfo>() {
                            @Override
                            public void onChanged(WorkInfo workInfo) {
                                if(workInfo != null) {
                                    Log.d(TAG, workInfo.getState().toString());
                                    if(workInfo.getState().isFinished()) {
                                        promise.resolve(true);
                                    }
                                }
                            }
                        });
            });


        } catch (Exception e) {
            Log.d(TAG + methodName, e.getMessage());
            promise.resolve(null);
        }
    }

}
