package com.example.a303com_laukuansin.pedometer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;

import com.example.a303com_laukuansin.R;
import com.example.a303com_laukuansin.cores.AppController;
import com.example.a303com_laukuansin.utilities.ConstantData;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;

public class SensorListener extends Service implements SensorEventListener {
    private final int NOTIFY_ID = 1;//notification id
    private String stepRecordID = "";//step record id
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");//date format example 11 Sep 2021
    private String currentDate = "";//today date
    private int currentStep = 0;//today step
    private SensorManager sensorManager = null;//sensor manager to get the step count
    private int stepSensor = -1;//0 is step counter, 1 is step detector
    private BroadcastReceiver receiver = null;
    private boolean hasRecord = false;//just today has record or not
    private int hasStepCount = 0;//before new day start record, it need to store the last reboot total step count, because the total step count will only keep increasing
    private int previousStepCount = 0;//previous step count before the next update step has called
    private Notification.Builder builder = null;//notification builder
    private NotificationManager notificationManager = null;//notification manager
    private FirebaseFirestore database;//database

    //only call once when open the service
    @Override
    public void onCreate() {
        super.onCreate();
        //initialize database
        database = FirebaseFirestore.getInstance();
        //initialize the broadcast receiver
        initBroadcastReceiver();
        //get the step detector
        new Thread(this::getStepDetector).start();
        //initialize today step data
        initTodayData();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        //when the step sensor is step counter
        if (stepSensor == 0) {
            //get the step taken from the last reboot while activated
            int tempStep = (int) sensorEvent.values[0];//1000
            //if no has record in today
            if (!hasRecord) {
                //change the true
                hasRecord = true;
                //store the the total step taken to hasStepCount
                hasStepCount = tempStep;//1000
            } else {//has record in today
                int thisStepCount = tempStep - hasStepCount;//get the step count has walked, tempStep will keep increase, hasStepCount will not increase or decrease, because it will be stored one day once
                currentStep += thisStepCount - previousStepCount;
                previousStepCount = thisStepCount;
            }
            //update the notification
            setStepBuilder();
            //save the step data
            saveStepData();
        } else if (stepSensor == 1) {//else the step sensor is step detector
            //when the step detector return 1.0 mean the step is detected
            if ((double) sensorEvent.values[0] == 1.0) {
                //then only add one step to the current step
                currentStep++;
                //update the notification
                setStepBuilder();
                saveStepData();
                //save the step data
            }
        }
    }

    //when start service is called
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //setup the notification manager
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //for android 8.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(this, ConstantData.CHANNEL_ID);
            //setup the notification channel
            NotificationChannel notificationChannel = new NotificationChannel(ConstantData.CHANNEL_ID, ConstantData.CHANNEL_NAME, NotificationManager.IMPORTANCE_MIN);
            notificationChannel.enableLights(false);//disable the light
            notificationChannel.setShowBadge(false);//disable to show the icon in status bar
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);// no show the notification on a secure lockscreen
            notificationManager.createNotificationChannel(notificationChannel);
            builder.setChannelId(ConstantData.CHANNEL_ID);
        } else {
            builder = new Notification.Builder(this);
        }

        setStepBuilder();
        startForeground(NOTIFY_ID, builder.build());//start the service
        return START_NOT_STICKY;
    }

    private void saveStepData() {
        //if the user is log in
        if (AppController.getInstance().getSessionHandler().getUser().getUID()!=null) {
            String path = String.format("StepRecords/%1$s/Records", AppController.getInstance().getSessionHandler().getUser().getUID());
            database.collection(path).whereEqualTo("date",currentDate).get().addOnSuccessListener(queryDocumentSnapshots -> {
                if(queryDocumentSnapshots.isEmpty())
                {
                    addStep();
                }
                else{
                    stepRecordID = queryDocumentSnapshots.getDocuments().get(0).getId();
                    updateStep();
                }
            });
        }
    }

    private void addStep() {
        if (AppController.getInstance().getSessionHandler().getUser()!=null) {
            Map<String, Object> stepRecordMap = new HashMap<>();//create hash map to store the step record's data
            stepRecordMap.put("stepCount", currentStep);
            stepRecordMap.put("date",currentDate);
            String path = String.format("StepRecords/%1$s/Records", AppController.getInstance().getSessionHandler().getUser().getUID());
            database.collection(path).add(stepRecordMap);
        }
    }

    private void updateStep() {
        if (AppController.getInstance().getSessionHandler().getUser()!=null&&!stepRecordID.isEmpty()) {
            Map<String, Object> stepRecordMap = new HashMap<>();//create hash map to store the step record's data
            stepRecordMap.put("stepCount", currentStep);
            String path = String.format("StepRecords/%1$s/Records/%2$s", AppController.getInstance().getSessionHandler().getUser().getUID(),stepRecordID);
            database.document(path).update(stepRecordMap);
        }
    }

    private void initTodayData() {
        //check the user is login
        if (AppController.getInstance().getSessionHandler().getUser()!=null) {
            //get today day
            currentDate = dateFormat.format(new Date());
            //get the path of database
            String path = String.format("StepRecords/%1$s/Records", AppController.getInstance().getSessionHandler().getUser().getUID());
            //get the today step data
            database.collection(path).whereEqualTo("date",currentDate).get().addOnSuccessListener(queryDocumentSnapshots -> {
                if(queryDocumentSnapshots.isEmpty())
                {
                    currentStep = 0;
                }
                else{
                    //because the record in each day will only have 1 so get(0)
                    stepRecordID = queryDocumentSnapshots.getDocuments().get(0).getId();
                    currentStep = queryDocumentSnapshots.getDocuments().get(0).getLong("stepCount").intValue();
                }
            });
        }
    }

    private void isNewDay() {
        //to check if current time is next day or not
        String nextDay = "00.00";//12 AM
        if (nextDay.equals(new SimpleDateFormat("HH:mm").format(new Date())) || !currentDate.equals(dateFormat.format(new Date()))) {
            //if next day, initialize the step data again
            initTodayData();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void initBroadcastReceiver() {
        //add the intent filter
        IntentFilter filter = new IntentFilter();
        //when the screen is off
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        //when the device is shut down
        filter.addAction(Intent.ACTION_SHUTDOWN);
        //when the user is present after device wakes up
        filter.addAction(Intent.ACTION_USER_PRESENT);
        //when a user action should request a temporary system dialog to dismiss
        filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        //When the date is changed
        filter.addAction(Intent.ACTION_DATE_CHANGED);
        //when the time is changed
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        //when the current time has changed
        filter.addAction(Intent.ACTION_TIME_TICK);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case Intent.ACTION_SCREEN_OFF:
                    case Intent.ACTION_USER_PRESENT:
                    case Intent.ACTION_CLOSE_SYSTEM_DIALOGS:
                    case Intent.ACTION_SHUTDOWN: {
                        saveStepData();
                        //update the notification
                        setStepBuilder();
                        break;
                    }

                    case Intent.ACTION_DATE_CHANGED:
                    case Intent.ACTION_TIME_CHANGED:
                    case Intent.ACTION_TIME_TICK: {
                        saveStepData();
                        //update the notification
                        setStepBuilder();
                        isNewDay();
                        break;
                    }
                }
            }
        };
        registerReceiver(receiver, filter);//register the broadcast with the intent filter
    }

    private void getStepDetector() {
        //setup the step sensor
        if (sensorManager != null) {
            sensorManager = null;
        }
        //set the sensor manager
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //add the count step listener
        addCountStepListener();
    }

    private void addCountStepListener() {
        //set count sensor: to count the step
        //set detector sensor: to detect the step
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        Sensor detectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        if (countSensor != null) {
            stepSensor = 0;//to differentiate the sensor
            sensorManager.registerListener(SensorListener.this, countSensor, SensorManager.SENSOR_DELAY_NORMAL);//register the sensor for count sensor
        } else if (detectorSensor != null) {
            stepSensor = 1;//to differentiate the sensor
            sensorManager.registerListener(SensorListener.this, detectorSensor, SensorManager.SENSOR_DELAY_NORMAL);//register the sensor for detector sensor
        }
    }

    private void setStepBuilder() {
        //setup the notification builder
        //check the builder is not null
        if (builder != null) {
            //get the goal step
            int goalStep = AppController.getInstance().getSessionHandler().getUser().getSuggestStepWalk();
            builder.setSmallIcon(R.drawable.logo_no_til)//set the small icon
                    .setProgress(goalStep, currentStep, false)//set progress bar
                    .setContentText(String.format("Goal: %1$d", goalStep))//set content text
                    .setContentTitle(String.format("Today's step count: %1$d", currentStep));//set title
            Notification notification = builder.build();//build notification
            notificationManager.notify(NOTIFY_ID, notification);//post the notification to status bar
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Stop foreground
        stopForeground(true);
        //unregister receiver
        unregisterReceiver(receiver);
        sensorManager.unregisterListener(this);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }
}
