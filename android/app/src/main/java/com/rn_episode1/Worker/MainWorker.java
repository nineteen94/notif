package com.rn_episode1.Worker;

import static com.rn_episode1.Util.Constants.IS_ONE_TIME_WORK;
import static com.rn_episode1.Util.Constants.MILLI_IN_DAY;
import static com.rn_episode1.Util.Constants.NOTIFICATION_CHANNEL_ID;
import static com.rn_episode1.Util.Constants.NOTIFICATION_CHANNEL_NAME;
import static com.rn_episode1.Util.Constants.NOTIFICATION_MODEL_IMAGE_PATH;
import static com.rn_episode1.Util.Constants.NOTIFICATION_MODEL_MESSAGE;
import static com.rn_episode1.Util.Constants.NOTIFICATION_MODEL_MESSAGE_DEFAULT_VALUE;
import static com.rn_episode1.Util.Constants.NOTIFICATION_MODEL_SEGMENT;
import static com.rn_episode1.Util.Constants.NOTIFICATION_MODEL_SEGMENT_DEFAULT_VALUE;
import static com.rn_episode1.Util.Constants.NOTIFICATION_MODEL_TITLE;
import static com.rn_episode1.Util.Constants.NOTIFICATION_MODEL_TITLE_DEFAULT_VALUE;
import static com.rn_episode1.Util.Constants.ONE_MEGABYTE;
import static com.rn_episode1.Util.Constants.TIME_MAP_DAY_OF_WEEK;
import static com.rn_episode1.Util.Constants.TIME_MAP_START;
import static com.rn_episode1.Util.Constants.TIME_MAP_START_OF_THE_DAY;
import static com.rn_episode1.Util.Constants.TIME_MAP_START_OF_THE_WEEK;
import static com.rn_episode1.Util.Helpers.getAppIconBitMap;
import static com.rn_episode1.Util.Helpers.getDateFromMilli;
import static com.rn_episode1.Util.Helpers.getString;
import static com.rn_episode1.Util.Helpers.resetLiveNotificationTrackerFile;
import static com.rn_episode1.Util.Helpers.setColdStartWork;
import static com.rn_episode1.Util.Helpers.transferDayUsage;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.UserManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rn_episode1.Database.AppDatabase;
import com.rn_episode1.Database.AppExecutors;
import com.rn_episode1.MainActivity;
import com.rn_episode1.Models.AppUsageModel;
import com.rn_episode1.Models.NotificationModel;
import com.rn_episode1.Models.PipelineModel;
import com.rn_episode1.Models.WorkerLogModel;
import com.rn_episode1.R;
import com.rn_episode1.Util.ApplicationsUtil;
import com.rn_episode1.Util.EvaluateUtil;
import com.rn_episode1.Util.Helpers;
import com.rn_episode1.Util.HistoricalUsageUtil;
import com.rn_episode1.Util.WeekdaysUsageUtil;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MainWorker extends Worker {
    private static final String TAG = "Vishal" + MainWorker.class.getSimpleName();

    private final Context context;

    private Map<String, Long> currentTimeMap;
    private Map<String, Long> lastFetchTimeMap;
    private boolean isNewDay;
    private boolean isOneTimeWork;
    private boolean isColdStart;
    private boolean isSunday;
    private boolean isNewWeek;

    public MainWorker(@NonNull Context context, @NonNull WorkerParameters workerParameters) {
        super(context, workerParameters);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        initialize();
        return Result.success();
    }

    private void initialize() {

        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();

        Log.d(TAG, methodName + " Starts!");

        try {

            AppDatabase appDatabase = AppDatabase.getInstance(context);

            ApplicationsUtil.initializeApplications(context);

            currentTimeMap = Helpers.getTimeMap(context, false);

            lastFetchTimeMap = Helpers.getTimeMap(context, true);

            isColdStart = currentTimeMap.get(TIME_MAP_START_OF_THE_DAY) > lastFetchTimeMap.get(TIME_MAP_START_OF_THE_DAY) + MILLI_IN_DAY;

            if(isColdStart) {

                setColdStartWork(context, currentTimeMap.get(TIME_MAP_START_OF_THE_DAY));

                appDatabase.appUsageDao().resetTodayUsage();
            }

            isNewWeek = currentTimeMap.get(TIME_MAP_START_OF_THE_WEEK) > lastFetchTimeMap.get(TIME_MAP_START_OF_THE_WEEK);

            if(isNewWeek) {

                HistoricalUsageUtil.initializeHistoricalUsage(context, currentTimeMap.get(TIME_MAP_START_OF_THE_WEEK), isColdStart);
            }

            long start = Math.max(currentTimeMap.get(TIME_MAP_START_OF_THE_WEEK), lastFetchTimeMap.get(TIME_MAP_START_OF_THE_DAY));

            long end = Math.max(currentTimeMap.get(TIME_MAP_START_OF_THE_DAY) - MILLI_IN_DAY, lastFetchTimeMap.get(TIME_MAP_START_OF_THE_DAY));

            Calendar calendar = Calendar.getInstance();

            while(start < end) {

                calendar.setTimeInMillis(start);

                WeekdaysUsageUtil.initOrUpdateTodayUsage(context, start, start + MILLI_IN_DAY);

                int dayOfTheWeek = calendar.get(Calendar.DAY_OF_WEEK);

                transferDayUsage(context, dayOfTheWeek);

                appDatabase.appUsageDao().resetTodayUsage(); //?

                start = start + MILLI_IN_DAY;
            }

            dailyNotification();

        } catch (Exception e) {
            Log.d(TAG + methodName, e.getMessage());
        }

    }

    private void dailyNotification() {

        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();

        Log.d(TAG, methodName + " Starts!");

        try {

            //part 1 => reset file. data fetching, shifting and reset

            lastFetchTimeMap = Helpers.getTimeMap(context, true);

            AppDatabase appDatabase = AppDatabase.getInstance(context);

            isNewDay = currentTimeMap.get(TIME_MAP_START_OF_THE_DAY) > lastFetchTimeMap.get(TIME_MAP_START);

            if(isNewDay) {

                resetLiveNotificationTrackerFile(context);

                long startTime = lastFetchTimeMap.get(TIME_MAP_START);

                long endTime = currentTimeMap.get(TIME_MAP_START_OF_THE_DAY);

                int dayOfTheWeek = Integer.parseInt(String.valueOf(lastFetchTimeMap.get(TIME_MAP_DAY_OF_WEEK)));

                WeekdaysUsageUtil.initOrUpdateTodayUsage(context, startTime, endTime);

                transferDayUsage(context, dayOfTheWeek);

                appDatabase.appUsageDao().resetTodayUsage();
            }

            // part 2 => decide whether to run daily notification

            isOneTimeWork = getInputData().getBoolean(IS_ONE_TIME_WORK, false);

            lastFetchTimeMap = Helpers.getTimeMap(context, true);

            SharedPreferences appVariableFile = context.getSharedPreferences(getString(context ,R.string.APP_VARIABLE_FILE_KEY), Context.MODE_PRIVATE);

            SharedPreferences.Editor appVariableFileEditor = appVariableFile.edit();

            if(isOneTimeWork && isNewDay) {

                appVariableFileEditor.putBoolean(getString(context, R.string.DID_DAILY_NOTIFICATION_RUN), false);

                appVariableFileEditor.apply();
            }

            //part 3 check and run

            isSunday = Integer.parseInt(String.valueOf(currentTimeMap.get(TIME_MAP_DAY_OF_WEEK))) == 1;

            boolean didDailyNotificationRun = appVariableFile.getBoolean(getString(context, R.string.DID_DAILY_NOTIFICATION_RUN), true);

            isColdStart = currentTimeMap.get(TIME_MAP_START_OF_THE_DAY) == appVariableFile.getLong(getString(context, R.string.COLD_START_WORK_START_OF_DAY), 0);

            Log.d(TAG + "iscoldstartvalue", "val is " + isColdStart);

            if(!isOneTimeWork && !isSunday && (isNewDay || !didDailyNotificationRun) && !isColdStart) {

                EvaluateUtil.evaluateDaily(context, currentTimeMap);
                
                appVariableFileEditor.putBoolean(getString(context, R.string.DID_DAILY_NOTIFICATION_RUN), true);

                appVariableFileEditor.apply();
            }

            weeklyNotification();

        } catch(Exception e) {
            Log.d(TAG + methodName, e.getMessage());
        }

    }

    private void weeklyNotification() {

        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();

        Log.d(TAG, methodName + " Starts!");

        try {
            //part1 = data shifting & reset

            AppDatabase appDatabase = AppDatabase.getInstance(context);

            if(isNewWeek && isSunday) {
                appDatabase.appUsageDao().resetLastWeekUsage();
                appDatabase.appUsageDao().setLastWeekUsage();
                appDatabase.appUsageDao().resetWeekDaysData();
            }

            //part2 = weekly notification

            SharedPreferences appVariableFile = context.getSharedPreferences(getString(context ,R.string.APP_VARIABLE_FILE_KEY), Context.MODE_PRIVATE);

            SharedPreferences.Editor appVariableFileEditor = appVariableFile.edit();

            boolean didWeeklyNotificationRun = appVariableFile.getBoolean(getString(context, R.string.DID_WEEKLY_NOTIFICATION_RUN), true);

            if(!isOneTimeWork && (isNewWeek || !didWeeklyNotificationRun) && !isColdStart) {
                //notification work
                //change var to true
                EvaluateUtil.evaluateWeekly(context, currentTimeMap);
                appVariableFileEditor.putBoolean(getString(context, R.string.DID_WEEKLY_NOTIFICATION_RUN), true);
                appVariableFileEditor.apply();
            }


            //part3 = cloud data

            if(isOneTimeWork && isNewWeek && !isColdStart) {
                appVariableFileEditor.putBoolean(getString(context, R.string.DID_WEEKLY_NOTIFICATION_RUN), false);
                appVariableFileEditor.apply();
            }

            if(isOneTimeWork && isNewWeek) {
                appVariableFileEditor.putBoolean(getString(context, R.string.DID_FETCH_NOTIFICATION_DATA_FROM_CLOUD), false);
                appVariableFileEditor.apply();
            }

            boolean didFetchNotificationDataFromCloud = appVariableFile.getBoolean(getString(context, R.string.DID_FETCH_NOTIFICATION_DATA_FROM_CLOUD), true);

            if(!isOneTimeWork && (isNewWeek || !didFetchNotificationDataFromCloud)) {
                //fetch data and change var to true
                getNotificationDataFromFireStore();
            } else {
                liveNotification();
            }

        } catch (Exception e) {
            Log.d(TAG + methodName, e.getMessage());
        }

    }

    private void getNotificationDataFromFireStore() {

        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();

        Log.d(TAG, methodName + " Starts!");

        try {

            Calendar calendar = Calendar.getInstance();

            int week = calendar.get(Calendar.WEEK_OF_YEAR) - 1;

            FirebaseFirestore firestore = FirebaseFirestore.getInstance();

            List<NotificationModel> notificationModelList = new ArrayList<>();

            firestore.collection("notificationTM").whereEqualTo("Week",week).limit(50).get().addOnCompleteListener(task -> {

                if (task.isSuccessful() && task.getResult() != null) {

                    for (QueryDocumentSnapshot document : task.getResult()) {

                        String id = document.getId();
                        String title = document.getString(NOTIFICATION_MODEL_TITLE);
                        String message = document.getString(NOTIFICATION_MODEL_MESSAGE);
                        String segment = document.getString(NOTIFICATION_MODEL_SEGMENT);
                        String imagePath = document.getString(NOTIFICATION_MODEL_IMAGE_PATH);

                        NotificationModel notificationModel = new NotificationModel(id, title, message, imagePath, null, segment);

                        notificationModelList.add(notificationModel);
                    }
                }

                addNotificationToLocalDatabase(notificationModelList);

            }).addOnFailureListener(reason -> {
               Log.d(TAG + methodName, reason.toString());
            });


        } catch (Exception e){
            Log.d(TAG + methodName, e.getMessage());
        }


    }

    private void addNotificationToLocalDatabase(List<NotificationModel> notificationModelList) {

        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();

        Log.d(TAG, methodName + " Starts!");

        AppExecutors.getInstance().diskIO().execute(() -> {
            try {

                AppDatabase appDatabase = AppDatabase.getInstance(context);

                SharedPreferences appVariableFile = context.getSharedPreferences(getString(context ,R.string.APP_VARIABLE_FILE_KEY), Context.MODE_PRIVATE);

                SharedPreferences.Editor appVariableFileEditor = appVariableFile.edit();

                if(notificationModelList.size() > 0) {

                    appVariableFileEditor.putBoolean(getString(context ,R.string.DID_FETCH_NOTIFICATION_DATA_FROM_CLOUD), true);

//                    appDatabase.notificationDao().deleteAllNotifications();

                    List<NotificationModel> existingNotificationModels = appDatabase.notificationDao().selectAllNotifications();

                    for(int i = 0; i < existingNotificationModels.size(); i ++) {

                        int deleteOrNot = new Random().nextInt(5);

                        if(deleteOrNot != 0) {

                            appDatabase.notificationDao().deleteNotification(existingNotificationModels.get(i));
                        }
                    }

                } else {

                    appVariableFileEditor.putBoolean(getString(context ,R.string.DID_FETCH_NOTIFICATION_DATA_FROM_CLOUD), false);

                    List<NotificationModel> existingNotificationModelList = appDatabase.notificationDao().selectAllNotifications();

                    if(existingNotificationModelList.size() == 0) {

                        for(int i = 0; i < 7; i ++) {

                            NotificationModel notificationModel = new NotificationModel(String.valueOf((i+1)), NOTIFICATION_MODEL_TITLE_DEFAULT_VALUE[i], NOTIFICATION_MODEL_MESSAGE_DEFAULT_VALUE[i],null, null ,NOTIFICATION_MODEL_SEGMENT_DEFAULT_VALUE[i]);

                            notificationModelList.add(notificationModel);
                        }
                    }
                }

                for(int i = 0; i < notificationModelList.size(); i ++) {

                    NotificationModel notificationModel = notificationModelList.get(i);

                    appDatabase.notificationDao().insertNotification(notificationModel);
                }

                appVariableFileEditor.apply();

                liveNotification();

            } catch (Exception e){
                Log.d(TAG + methodName, e.getMessage());
            }
        });

    }

    private void liveNotification() {

        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();

        Log.d(TAG, methodName + " Starts!");

        try {
            lastFetchTimeMap = Helpers.getTimeMap(context, true);

            long startTime = lastFetchTimeMap.get(TIME_MAP_START);

            long endTime = currentTimeMap.get(TIME_MAP_START);

            WeekdaysUsageUtil.initOrUpdateTodayUsage(context, startTime, endTime);

            if(!isOneTimeWork) {

                EvaluateUtil.evaluateLive(context, currentTimeMap);
            }

            checkPipeline();

        } catch (Exception e) {
            Log.d(TAG + methodName, e.getMessage());
        }

    }

    private void checkPipeline() {

        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();

        Log.d(TAG, methodName + " Starts!");

        try {

            AppDatabase appDatabase = AppDatabase.getInstance(context);

//            appDatabase.pipelineDao().cleanPipelineTable(currentTimeMap.get(TIME_MAP_START));

            PipelineModel pipelineModel = appDatabase.pipelineDao().selectPipelineModel(currentTimeMap.get(TIME_MAP_START));

            if(pipelineModel == null) {

                Log.d(TAG + methodName, "No pipeline model found");
                return;
            }

            String notificationSegment = pipelineModel.getNotificationSegment();

            NotificationModel notificationModel = appDatabase.notificationDao().selectNotificationModel("%" + notificationSegment + "%");

            String imagePath = notificationModel.getImagePath();

            byte[] imageByteArray = notificationModel.getImageByteArray();

            boolean hasImage = imagePath != null && imagePath.length() != 0;

            boolean downloadedImage = imageByteArray != null;

            if(hasImage && !downloadedImage) {

                getNotificationImage(pipelineModel, notificationModel);

            } else {

                executeNotification(pipelineModel, notificationModel);
            }

        } catch (Exception e) {
            Log.d(TAG + methodName, e.getMessage());
        }

    }

    private void getNotificationImage(PipelineModel pipelineModel, NotificationModel notificationModel) {

        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();

        Log.d(TAG, methodName + " Starts!");

        try {
            FirebaseStorage storage = FirebaseStorage.getInstance();

            StorageReference storageRef = storage.getReference();

            StorageReference imageRef = storageRef.child(notificationModel.getImagePath());

            Log.d(TAG + methodName, imageRef.toString());

            imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {

                updateNotificationImage(pipelineModel, notificationModel, bytes);

            }).addOnFailureListener(e -> {

                Log.d(TAG + methodName, e.getMessage());

                executeNotification(pipelineModel, notificationModel);
            });
        } catch (Exception e) {
            Log.d(TAG + methodName, e.getMessage());
        }
    }

    private void updateNotificationImage(PipelineModel pipelineModel, NotificationModel notificationModel, byte[] imageByteArray) {

        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();

        Log.d(TAG, methodName + " Starts!");

        AppExecutors.getInstance().diskIO().execute(() -> {

            try {

                AppDatabase appDatabase = AppDatabase.getInstance(context);

                notificationModel.setImageByteArray(imageByteArray);

                appDatabase.notificationDao().updateNotification(notificationModel);

                executeNotification(pipelineModel, notificationModel);

            } catch (Exception e) {
                Log.d(TAG + methodName, e.getMessage());
            }

        });

    }

    private void executeNotification(PipelineModel pipelineModel, NotificationModel notificationModel) {

        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();

        Log.d(TAG, methodName + " Starts!");

        AppExecutors.getInstance().diskIO().execute(() -> {

            try {

                SharedPreferences appVariableFile = context.getSharedPreferences(getString(context, R.string.APP_VARIABLE_FILE_KEY), Context.MODE_PRIVATE);

                SharedPreferences.Editor appVariableFileEditor = appVariableFile.edit();

                int NOTIFICATION_ID = appVariableFile.getInt(getString(context, R.string.NOTIFICATION_ID), 0);

                NOTIFICATION_ID ++;

                String packageName = pipelineModel.getPackageName();

                Bitmap largeIcon;

                if(packageName == null || packageName.length() == 0) {

                    packageName = MainActivity.PACKAGE_NAME;
                }

                largeIcon = getAppIconBitMap(context, packageName);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    int importance = NotificationManager.IMPORTANCE_HIGH;

                    NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, importance);

                    NotificationManagerCompat.from(context).createNotificationChannel(channel);
                }

                AppDatabase appDatabase = AppDatabase.getInstance(context);

                String title = notificationModel.getTitle();

                String message = notificationModel.getMessage();

                if(pipelineModel.getApplicationNamePlaceholder() != null) {
                    message = message.replace("{applicationName}", pipelineModel.getApplicationNamePlaceholder());
                    title = title.replace("{applicationName}", pipelineModel.getApplicationNamePlaceholder());
                }

                if(pipelineModel.getApplicationUsagePlaceholder() != null) {
                    message = message.replace("{applicationUsage}", pipelineModel.getApplicationUsagePlaceholder());
                    title = title.replace("{applicationUsage}", pipelineModel.getApplicationUsagePlaceholder());
                }

//                if(pipelineModel.getReportPlaceholder() != null) {
//                    message = message.replace("{report}", pipelineModel.getReportPlaceholder());
//                }

                byte[] bigImageByteArray = notificationModel.getImageByteArray();

                Bitmap bigImageBitmap = null;

                if(bigImageByteArray != null) {
                    bigImageBitmap = BitmapFactory.decodeByteArray(bigImageByteArray, 0, bigImageByteArray.length);
                }

                Notification notification;

                if(bigImageBitmap == null) {
                    notification = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                            .setSmallIcon(R.drawable.notif_ghanta)
                            .setContentTitle(title)
                            .setContentText(message)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                            .setLargeIcon(largeIcon)
                            .build();
                } else {
                    notification = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                            .setSmallIcon(R.drawable.notif_ghanta)
                            .setContentTitle(title)
                            .setContentText(message)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                            .setLargeIcon(largeIcon)
                            .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bigImageBitmap))
                            .build();
                }

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

                notificationManager.notify(NOTIFICATION_ID, notification);

                pipelineModel.setIsFired(true);

                appDatabase.pipelineDao().updatePipelineModel(pipelineModel);

                appVariableFileEditor.putInt(getString(context, R.string.NOTIFICATION_ID), NOTIFICATION_ID);

                appVariableFileEditor.apply();

            } catch (Exception e){
                Log.d(TAG + methodName, e.getMessage());
            }

        });



    }


}

















