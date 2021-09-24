package com.example.a303com_laukuansin.activities;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.a303com_laukuansin.R;
import com.example.a303com_laukuansin.broadcastReceiver.ReminderService;
import com.example.a303com_laukuansin.cores.BaseActivity;
import com.example.a303com_laukuansin.domains.Reminder;
import com.example.a303com_laukuansin.fragments.AccountFragment;
import com.example.a303com_laukuansin.fragments.AnalyticFragment;
import com.example.a303com_laukuansin.fragments.HomeFragment;
import com.example.a303com_laukuansin.pedometer.SensorListener;
import com.example.a303com_laukuansin.utilities.ConstantData;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class HomeActivity extends BaseActivity {
    private Fragment _fragment;

    @Override
    protected int ContentView() {
        return R.layout.activity_bottom_navigation;
    }

    @Override
    protected boolean RequiredInternetConnection() {
        return true;
    }

    @Override
    protected void AttemptSave() {

    }

    @Override
    protected void AttemptDelete() {

    }

    @Override
    protected void AttemptSearch() {

    }

    @Override
    protected void AttemptAdd() {

    }

    @Override
    protected void AttemptHelp() {

    }

    @Override
    protected int MenuResource() {
        return 0;
    }

    @Override
    protected boolean DisableActionMenu() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isPermissionGranted();//check permission

        //if the user is log in only start track the step, and set the alarm manager for reminder if have
        if (getSessionHandler().isLoggedIn()) {
            ContextCompat.startForegroundService(this, new Intent(this, SensorListener.class));
            setupAlarmForReminder();
        }

        initialization();

        //set the default fragment
        if (savedInstanceState == null) {
            _fragment = HomeFragment.newInstance();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.frame_container, _fragment).commit();
        }
    }

    private void setupAlarmForReminder()
    {
        String REMINDER_COLLECTION_PATH = String.format("Reminders/%1$s/AlarmIDList", getSessionHandler().getUser().getUID());
        //get the collection reference
        //collection path = Reminders/UID/AlarmIDList
        CollectionReference reminderCollectionReference = FirebaseFirestore.getInstance().collection(REMINDER_COLLECTION_PATH);
        reminderCollectionReference.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for(DocumentSnapshot documentSnapshot:queryDocumentSnapshots.getDocuments())
            {
                //get the alarm id
                String alarmID = documentSnapshot.getId();
                //get the time
                String time = documentSnapshot.getString("time");
                String typeReminder = documentSnapshot.getString("reminderName");
                try {
                    //convert the time string to date, then only convert to calendar
                    Date date = new SimpleDateFormat("hh:mm aaa").parse(time);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    //schedule the notification when pop out
                    scheduleNotification(getNotification(typeReminder),Integer.parseInt(alarmID),calendar);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void scheduleNotification(Notification notification, int notificationID,Calendar time) {
        //get the current time in milliseconds
        long systemTime = System.currentTimeMillis();

        Calendar calendar = Calendar.getInstance();
        //set the calendar to current time in milliseconds
        calendar.setTimeInMillis(System.currentTimeMillis());
        //need reset time zone to prevent time lag
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        //set the minute when to reminder
        calendar.set(Calendar.MINUTE,time.get(Calendar.MINUTE));
        //set the hour when to reminder
        calendar.set(Calendar.HOUR_OF_DAY, time.get(Calendar.HOUR_OF_DAY));
        //set second to 0
        calendar.set(Calendar.SECOND, 0);
        //set millisecond to 0
        calendar.set(Calendar.MILLISECOND, 0);

        //get the selected time to remind in milliseconds
        long selectTime = calendar.getTimeInMillis();

        //if time is bigger than your select time, which mean the time u want to remind has been pass through
        if (systemTime > selectTime) {
            //add one more day, wait until next day only will notify
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        //get the notification intent
        Intent notificationIntent = new Intent(this, ReminderService.class);
        //pass the notification id and notification to broadcast receiver
        notificationIntent.putExtra(ReminderService.NOTIFICATION_ID, notificationID);
        notificationIntent.putExtra(ReminderService.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, notificationID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //set alarm manager
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        //remove previous alarm
        alarmManager.cancel(pendingIntent);
        //set new repeating alarm
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    private Notification getNotification(String typeReminder) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");
        builder.setContentTitle(String.format("%1$s reminder", typeReminder));
        if(typeReminder.equals("Weight In"))
        {
            builder.setContentText("Friendly reminder to weight in");
        }
        else{
            builder.setContentText(String.format("Friendly reminder to log your %1$s", typeReminder));
        }
        builder.setSmallIcon(R.drawable.logo_no_til);
        builder.setAutoCancel(true);
        builder.setPriority(Notification.PRIORITY_MAX);
        builder.setChannelId(ConstantData.CHANNEL_ID);
        return builder.build();
    }

    private void initialization() {
        //setup bottom navigation bar
        BottomNavigationView btmNavBar = (BottomNavigationView) findViewById(R.id.bottomNavigationBar);
        btmNavBar.getMenu().getItem(0).setEnabled(false);//because on default first item at bottom navigation did not disable

        btmNavBar.setOnNavigationItemSelectedListener(item -> {

            for (int i = 0; i < 3; i++) {
                btmNavBar.getMenu().getItem(i).setEnabled(true);
            }
            switch (item.getItemId()) {
                case R.id.home://when click home page
                    _fragment = HomeFragment.newInstance();
                    btmNavBar.getMenu().getItem(0).setEnabled(false);
                    break;
                case R.id.analytic://when click analytic page
                    _fragment = AnalyticFragment.newInstance();
                    btmNavBar.getMenu().getItem(1).setEnabled(false);
                    break;
                case R.id.account://when click account page
                    _fragment = AccountFragment.newInstance();
                    btmNavBar.getMenu().getItem(2).setEnabled(false);
                    break;
                default:
                    Toast.makeText(HomeActivity.this, "Default frag", Toast.LENGTH_SHORT).show();
                    break;
            }
            btmNavBar.setSelectedItemId(item.getItemId());//set selected item
            return loadFragment(_fragment);
        });
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);//set animation
            fragmentTransaction.replace(R.id.frame_container, fragment);
            fragmentTransaction.commit();
            return true;
        }
        return false;
    }

}
