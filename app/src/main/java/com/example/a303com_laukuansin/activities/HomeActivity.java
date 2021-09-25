package com.example.a303com_laukuansin.activities;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.a303com_laukuansin.R;
import com.example.a303com_laukuansin.cores.BaseActivity;
import com.example.a303com_laukuansin.domains.Reminder;
import com.example.a303com_laukuansin.fragments.AccountFragment;
import com.example.a303com_laukuansin.fragments.AnalyticFragment;
import com.example.a303com_laukuansin.fragments.HomeFragment;
import com.example.a303com_laukuansin.pedometer.SensorListener;
import com.example.a303com_laukuansin.utilities.NotificationAlarm;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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

    private void setupAlarmForReminder() {
        NotificationAlarm notificationAlarm = new NotificationAlarm(this);
        String REMINDER_COLLECTION_PATH = String.format("Reminders/%1$s/AlarmIDList", getSessionHandler().getUser().getUID());
        //get the collection reference
        //collection path = Reminders/UID/AlarmIDList
        CollectionReference reminderCollectionReference = FirebaseFirestore.getInstance().collection(REMINDER_COLLECTION_PATH);
        reminderCollectionReference.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
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
                    Reminder reminder = new Reminder();
                    reminder.setReminderID(Integer.parseInt(alarmID));
                    reminder.setReminderName(typeReminder);
                    reminder.setTime(calendar);

                    //schedule the notification when pop out
                    notificationAlarm.scheduleNotification(reminder);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initialization() {
        //setup bottom navigation bar
        BottomNavigationView btmNavBar = (BottomNavigationView) findViewById(R.id.bottomNavigationBar);
        btmNavBar.getMenu().getItem(0).setEnabled(false);//because on default first item at bottom navigation did not disable

        btmNavBar.setOnNavigationItemSelectedListener(item -> {
            //set the 3 menu to enable
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
            //load the fragment
            return loadFragment(_fragment);
        });
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            //add animation
            fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);//set animation
            fragmentTransaction.replace(R.id.frame_container, fragment);
            fragmentTransaction.commit();
            return true;
        }
        return false;
    }

}
