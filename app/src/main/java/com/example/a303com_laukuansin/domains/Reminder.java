package com.example.a303com_laukuansin.domains;

import java.util.Calendar;

public class Reminder {
    private int reminderID;
    private String reminderName;
    private Calendar time;

    public Reminder() {
    }

    public int getReminderID() {
        return reminderID;
    }

    public void setReminderID(int reminderID) {
        this.reminderID = reminderID;
    }

    public String getReminderName() {
        return reminderName;
    }

    public void setReminderName(String reminderName) {
        this.reminderName = reminderName;
    }

    public Calendar getTime() {
        return time;
    }

    public void setTime(Calendar time) {
        this.time = time;
    }
}
