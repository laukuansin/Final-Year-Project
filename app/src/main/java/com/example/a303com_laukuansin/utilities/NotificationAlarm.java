package com.example.a303com_laukuansin.utilities;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.a303com_laukuansin.R;
import com.example.a303com_laukuansin.broadcastReceiver.ReminderService;
import com.example.a303com_laukuansin.domains.Reminder;

import java.util.Calendar;
import java.util.TimeZone;

import androidx.core.app.NotificationCompat;

/*
Create the daily alarm for reminder
*/
public class NotificationAlarm {
    private Context _context;
    private AlarmManager _alarmManager;

    public NotificationAlarm(Context _context) {
        this._context = _context;
        //set alarm manager
        _alarmManager = (AlarmManager) _context.getSystemService(Context.ALARM_SERVICE);
    }

    public void scheduleNotification(Reminder reminder) {
        Notification notification = getNotification(reminder.getReminderName());
        //get the current time in milliseconds
        long systemTime = System.currentTimeMillis();

        Calendar calendar = Calendar.getInstance();
        //set the calendar to current time in milliseconds
        calendar.setTimeInMillis(System.currentTimeMillis());
        //need reset time zone to prevent time lag
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        //set the minute when to reminder
        calendar.set(Calendar.MINUTE, reminder.getTime().get(Calendar.MINUTE));
        //set the hour when to reminder
        calendar.set(Calendar.HOUR_OF_DAY, reminder.getTime().get(Calendar.HOUR_OF_DAY));
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
        Intent notificationIntent = new Intent(_context, ReminderService.class);
        //pass the notification id and notification to broadcast receiver
        notificationIntent.putExtra(ReminderService.NOTIFICATION_ID, reminder.getReminderID());
        notificationIntent.putExtra(ReminderService.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(_context, reminder.getReminderID(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //remove previous alarm
        _alarmManager.cancel(pendingIntent);
        //set new repeating alarm
        _alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    private Notification getNotification(String typeReminder) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(_context, "default");
        builder.setContentTitle(String.format("%1$s reminder", typeReminder));
        if (typeReminder.equals("Weight In")) {
            builder.setContentText("Friendly reminder to weight in");
        } else {
            builder.setContentText(String.format("Friendly reminder to log your %1$s", typeReminder));
        }
        builder.setSmallIcon(R.drawable.logo_no_til);
        builder.setAutoCancel(true);
        builder.setPriority(Notification.PRIORITY_MAX);
        builder.setChannelId(ConstantData.CHANNEL_ID);
        return builder.build();
    }

    public void removeNotification(int notificationID) {
        Intent notificationIntent = new Intent(_context, ReminderService.class);
        notificationIntent.putExtra(ReminderService.NOTIFICATION_ID, notificationID);
        //pass the notification intent
        PendingIntent pendingIntent = PendingIntent.getBroadcast(_context, notificationID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //cancel the notification
        _alarmManager.cancel(pendingIntent);
    }

    public void removeAllNotification() {
        //get the alarm service
        AlarmManager alarmManager = (AlarmManager) _context.getSystemService(Context.ALARM_SERVICE);
        //loop from 2 to 7 because the alarm id is 2-7
        for (int i = 2; i <= 7; i++) {
            Intent myIntent = new Intent(_context, ReminderService.class);
            //get the pending intent
            PendingIntent pendingIntent = PendingIntent.getBroadcast(_context, i, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            //cancel the alarm
            alarmManager.cancel(pendingIntent);
        }
    }
}
