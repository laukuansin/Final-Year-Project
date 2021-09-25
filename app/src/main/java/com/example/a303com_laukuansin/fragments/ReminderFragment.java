package com.example.a303com_laukuansin.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a303com_laukuansin.R;
import com.example.a303com_laukuansin.cores.BaseFragment;
import com.example.a303com_laukuansin.domains.Reminder;
import com.example.a303com_laukuansin.domains.User;
import com.example.a303com_laukuansin.utilities.NotificationAlarm;
import com.example.a303com_laukuansin.utilities.OnSingleClickListener;
import com.example.a303com_laukuansin.utilities.TimePickerDialogFragment;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class ReminderFragment extends BaseFragment {
    private final User user;
    private TextView _breakfastTime, _lunchTime, _dinnerTime, _snackTime, _exerciseTime, _weightInTime;
    private SwitchCompat _breakfastSwitch, _lunchSwitch, _dinnerSwitch, _snackSwitch, _exerciseSwitch, _weightInSwitch;
    private Reminder breakfastReminder,lunchReminder,dinnerReminder,snackReminder,exerciseReminder,weightInReminder;
    private NotificationAlarm notificationAlarm;
    private RetrieveReminders _retrieveReminders = null;
    private FirebaseFirestore database;
    private final String timeFormat = "hh:mm aaa";
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aaa");

    public ReminderFragment() {
        user = getSessionHandler().getUser();
    }

    public static ReminderFragment newInstance() {
        return new ReminderFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reminder, container, false);
        initialization(view);
        //load reminder
        loadData(user);
        return view;
    }

    private void loadData(User user) {
        if (_retrieveReminders == null) {
            _retrieveReminders = new RetrieveReminders(user);
            _retrieveReminders.execute();
        }
    }

    private void initialization(View view) {
        //bind view with id
        LinearLayout _breakfastLayout = view.findViewById(R.id.breakfastLayout);
        LinearLayout _lunchLayout = view.findViewById(R.id.lunchLayout);
        LinearLayout _dinnerLayout = view.findViewById(R.id.dinnerLayout);
        LinearLayout _snackLayout = view.findViewById(R.id.snackLayout);
        LinearLayout _exerciseLayout = view.findViewById(R.id.exerciseLayout);
        LinearLayout _weightInLayout = view.findViewById(R.id.weightInLayout);
        _breakfastTime = view.findViewById(R.id.breakfastTime);
        _lunchTime = view.findViewById(R.id.lunchTime);
        _dinnerTime = view.findViewById(R.id.dinnerTime);
        _snackTime = view.findViewById(R.id.snackTime);
        _exerciseTime = view.findViewById(R.id.exerciseTime);
        _weightInTime = view.findViewById(R.id.weightInTime);
        _breakfastSwitch = view.findViewById(R.id.breakfastSwitch);
        _lunchSwitch = view.findViewById(R.id.lunchSwitch);
        _dinnerSwitch = view.findViewById(R.id.dinnerSwitch);
        _snackSwitch = view.findViewById(R.id.snackSwitch);
        _exerciseSwitch = view.findViewById(R.id.exerciseSwitch);
        _weightInSwitch = view.findViewById(R.id.weightInSwitch);

        //set notification alarm
        notificationAlarm = new NotificationAlarm(getContext());
        //initialize database
        database = FirebaseFirestore.getInstance();

        //when click the breakfast layout
        _breakfastLayout.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                openTimerPickerDialog( _breakfastTime, _breakfastSwitch, breakfastReminder);
            }
        });

        //when click lunch layout
        _lunchLayout.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                openTimerPickerDialog( _lunchTime, _lunchSwitch, lunchReminder);
            }
        });

        //when click dinner layout
        _dinnerLayout.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                openTimerPickerDialog( _dinnerTime, _dinnerSwitch, dinnerReminder);
            }
        });

        //when click snack layout
        _snackLayout.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                openTimerPickerDialog( _snackTime, _snackSwitch, snackReminder);

            }
        });

        //when click exercise layout
        _exerciseLayout.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                openTimerPickerDialog( _exerciseTime, _exerciseSwitch, exerciseReminder);
            }
        });

        //when click weight in layout
        _weightInLayout.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                openTimerPickerDialog( _weightInTime, _weightInSwitch, weightInReminder);
            }
        });

        //when click the breakfast switch
        _breakfastSwitch.setOnClickListener(v -> {
            if (_breakfastSwitch.isChecked()) {
                Toast.makeText(getContext(), "Turn on breakfast reminder", Toast.LENGTH_SHORT).show();
                notificationAlarm.scheduleNotification(breakfastReminder);
                addReminderToDatabase(breakfastReminder);
            } else {
                Toast.makeText(getContext(), "Turn off breakfast reminder", Toast.LENGTH_SHORT).show();
                notificationAlarm.removeNotification(breakfastReminder.getReminderID());
                deleteReminderFromDatabase(breakfastReminder.getReminderID());
            }
        });

        //when click the lunch switch
        _lunchSwitch.setOnClickListener(v -> {
            if (_lunchSwitch.isChecked()) {
                Toast.makeText(getContext(), "Turn on lunch reminder", Toast.LENGTH_SHORT).show();
                notificationAlarm.scheduleNotification(lunchReminder);
                addReminderToDatabase(lunchReminder);
            } else {
                Toast.makeText(getContext(), "Turn off lunch reminder", Toast.LENGTH_SHORT).show();
                notificationAlarm.removeNotification(lunchReminder.getReminderID());
                deleteReminderFromDatabase(lunchReminder.getReminderID());
            }
        });

        //when click the dinner switch
        _dinnerSwitch.setOnClickListener(v -> {
            if (_dinnerSwitch.isChecked()) {
                Toast.makeText(getContext(), "Turn on dinner reminder", Toast.LENGTH_SHORT).show();
                notificationAlarm.scheduleNotification(dinnerReminder);
                addReminderToDatabase(dinnerReminder);
            } else {
                Toast.makeText(getContext(), "Turn off dinner reminder", Toast.LENGTH_SHORT).show();
                notificationAlarm.removeNotification(dinnerReminder.getReminderID());
                deleteReminderFromDatabase(dinnerReminder.getReminderID());
            }
        });

        //when click the snack switch
        _snackSwitch.setOnClickListener(v -> {
            if (_snackSwitch.isChecked()) {
                Toast.makeText(getContext(), "Turn on snack reminder", Toast.LENGTH_SHORT).show();
                notificationAlarm.scheduleNotification(snackReminder);
                addReminderToDatabase(snackReminder);
            } else {
                Toast.makeText(getContext(), "Turn off snack reminder", Toast.LENGTH_SHORT).show();
                notificationAlarm.removeNotification(snackReminder.getReminderID());
                deleteReminderFromDatabase(snackReminder.getReminderID());
            }
        });

        //when click the exercise switch
        _exerciseSwitch.setOnClickListener(v -> {
            if (_exerciseSwitch.isChecked()) {
                Toast.makeText(getContext(), "Turn on exercise reminder", Toast.LENGTH_SHORT).show();
                notificationAlarm.scheduleNotification(exerciseReminder);
                addReminderToDatabase(exerciseReminder);
            } else {
                Toast.makeText(getContext(), "Turn off exercise reminder", Toast.LENGTH_SHORT).show();
                notificationAlarm.removeNotification(exerciseReminder.getReminderID());
                deleteReminderFromDatabase(exerciseReminder.getReminderID());
            }
        });

        //when click the weight in switch
        _weightInSwitch.setOnClickListener(v -> {
            if (_weightInSwitch.isChecked()) {
                Toast.makeText(getContext(), "Turn on weight in reminder", Toast.LENGTH_SHORT).show();
                notificationAlarm.scheduleNotification(weightInReminder);
                addReminderToDatabase(weightInReminder);
            } else {
                Toast.makeText(getContext(), "Turn off weight in reminder", Toast.LENGTH_SHORT).show();
                notificationAlarm.removeNotification(weightInReminder.getReminderID());
                deleteReminderFromDatabase(weightInReminder.getReminderID());
            }
        });

    }

    private class RetrieveReminders extends AsyncTask<Void, Void, Void> {
        private User user;
        private SweetAlertDialog _progressDialog;

        public RetrieveReminders(User user) {
            this.user = user;
            _progressDialog = showProgressDialog("Loading...", getResources().getColor(R.color.colorPrimary));
            breakfastReminder = new Reminder();
            lunchReminder = new Reminder();
            dinnerReminder = new Reminder();
            snackReminder = new Reminder();
            exerciseReminder = new Reminder();
            weightInReminder = new Reminder();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            _progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            getActivity().runOnUiThread(() -> {
                //initialize setup each reminder
                breakfastReminder = setupReminder("Breakfast",2);
                lunchReminder = setupReminder("Lunch",3);
                dinnerReminder = setupReminder("Dinner",4);
                snackReminder = setupReminder("Snack",5);
                exerciseReminder = setupReminder("Exercise",6);
                weightInReminder = setupReminder("Weight In",7);

                String REMINDER_COLLECTION_PATH = String.format("Reminders/%1$s/AlarmIDList", user.getUID());
                //get the collection reference
                //collection path = Reminders/UID/AlarmIDList
                CollectionReference reminderCollectionReference = database.collection(REMINDER_COLLECTION_PATH);
                reminderCollectionReference.get().addOnSuccessListener(queryDocumentSnapshots -> {
                    if(_progressDialog.isShowing())
                        _progressDialog.dismiss();

                    for(DocumentSnapshot documentSnapshot:queryDocumentSnapshots.getDocuments())
                    {
                        String alarmID = documentSnapshot.getId();
                        String time = documentSnapshot.getString("time");
                        try {
                            Date date = dateFormat.parse(time);
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(date);
                            switch(alarmID){
                                case "2":{
                                    breakfastReminder.setTime(calendar);
                                    _breakfastSwitch.setChecked(true);
                                    break;
                                }
                                case "3":{
                                    lunchReminder.setTime(calendar);
                                    _lunchSwitch.setChecked(true);
                                    break;
                                }
                                case "4":{
                                    dinnerReminder.setTime(calendar);
                                    _dinnerSwitch.setChecked(true);
                                    break;
                                }
                                case "5":{
                                    snackReminder.setTime(calendar);
                                    _snackSwitch.setChecked(true);
                                    break;
                                }
                                case "6":{
                                    exerciseReminder.setTime(calendar);
                                    _exerciseSwitch.setChecked(true);
                                    break;
                                }
                                case "7":{
                                    weightInReminder.setTime(calendar);
                                    _weightInSwitch.setChecked(true);
                                    break;
                                }
                                default:{
                                    Log.d("Error","No found alarm ID");
                                    break;
                                }
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                    //if no reminder, then set the default time
                    if(breakfastReminder.getTime()==null)
                    {
                        //breakfastTime = "08:00 AM";
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(0,0,0,8,0);
                        breakfastReminder.setTime(calendar);
                    }
                    if(lunchReminder.getTime()==null)
                    {
                        //lunchTime = "12:00 PM";
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(0,0,0,12,0);;
                        lunchReminder.setTime(calendar);
                    }
                    if(dinnerReminder.getTime()==null)
                    {
                        //dinnerTime = "06:00 PM";
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(0,0,0,18,0);
                        dinnerReminder.setTime(calendar);
                    }
                    if(snackReminder.getTime()==null)
                    {
                        //snackTime = "03:00 PM";
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(0,0,0,15,0);
                        snackReminder.setTime(calendar);
                    }
                    if(exerciseReminder.getTime()==null)
                    {
                        //exerciseTime = "08:00 AM";
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(0,0,0,8,0);
                        exerciseReminder.setTime(calendar);
                    }
                    if(weightInReminder.getTime()==null)
                    {
                        //weightInTime = "08:00 AM";
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(0,0,0,8,0);
                        weightInReminder.setTime(calendar);
                    }

                    //set date in text view
                    _breakfastTime.setText(DateFormat.format(timeFormat,breakfastReminder.getTime()));
                    _lunchTime.setText(DateFormat.format(timeFormat,lunchReminder.getTime()));
                    _dinnerTime.setText(DateFormat.format(timeFormat,dinnerReminder.getTime()));
                    _snackTime.setText(DateFormat.format(timeFormat,snackReminder.getTime()));
                    _exerciseTime.setText(DateFormat.format(timeFormat,exerciseReminder.getTime()));
                    _weightInTime.setText(DateFormat.format(timeFormat,weightInReminder.getTime()));

                }).addOnFailureListener(e -> {
                    if(_progressDialog.isShowing())
                        _progressDialog.dismiss();
                    ErrorAlert(e.getMessage(), sweetAlertDialog -> sweetAlertDialog.dismiss(),true).show();
                    _retrieveReminders = null;
                });
            });
            return null;
        }
    }

    private Reminder setupReminder(String name,int notificationID)
    {
        Reminder reminder = new Reminder();
        reminder.setReminderID(notificationID);
        reminder.setReminderName(name);
        return reminder;
    }

    private void openTimerPickerDialog(TextView timeTextView, SwitchCompat switchCompat,Reminder reminder) {
        Bundle args = new Bundle();
        //pass the hour and minute to the timer picker dialog fragment
        args.putInt("hour", reminder.getTime().get(Calendar.HOUR_OF_DAY));
        args.putInt("minute", reminder.getTime().get(Calendar.MINUTE));
        TimePickerDialogFragment timePicker = new TimePickerDialogFragment();
        timePicker.setArguments(args);
        timePicker.setCallBack((timePicker1, hour, minute) -> {
            //set the hour and minute
            reminder.getTime().set(0, 0, 0, hour, minute);
            //update the text view
            timeTextView.setText(DateFormat.format(timeFormat, reminder.getTime()));
            //if the user is change the time while the switch is checked
            if (switchCompat.isChecked()) {
                //toast update message
                Toast.makeText(getContext(), String.format("Update %1$s reminder time success", reminder.getReminderName()), Toast.LENGTH_SHORT).show();
                //schedule notification
                notificationAlarm.scheduleNotification(reminder);
                //update the reminder to database
                updateReminderToDatabase(reminder);
            }
        });
        timePicker.show(getActivity().getSupportFragmentManager(), "tag");
    }

    private void addReminderToDatabase(Reminder reminder)
    {
        String REMINDER_COLLECTION_PATH = String.format("Reminders/%1$s/AlarmIDList", user.getUID());
        //get the collection reference
        //collection path = Reminders/UID/AlarmIDList
        CollectionReference reminderCollectionReference = database.collection(REMINDER_COLLECTION_PATH);
        Map<String,Object> reminderMap = new HashMap<>();
        reminderMap.put("time",DateFormat.format(timeFormat,reminder.getTime()));
        reminderMap.put("reminderName",reminder.getReminderName());
        //add reminder to database
        reminderCollectionReference.document(String.valueOf(reminder.getReminderID())).set(reminderMap);
    }

    private void updateReminderToDatabase(Reminder reminder)
    {
        String REMINDER_DOCUMENT_PATH = String.format("Reminders/%1$s/AlarmIDList/%2$d", user.getUID(),reminder.getReminderID());
        //get the document reference
        //document path = Reminders/UID/AlarmIDList/NotificationID
        DocumentReference reminderDocumentReference = database.document(REMINDER_DOCUMENT_PATH);
        Map<String,Object> reminderMap = new HashMap<>();
        reminderMap.put("time",DateFormat.format(timeFormat,reminder.getTime()));
        //update reminder to database
        reminderDocumentReference.update(reminderMap);
    }

    private void deleteReminderFromDatabase(int notificationID)
    {
        String REMINDER_DOCUMENT_PATH = String.format("Reminders/%1$s/AlarmIDList/%2$d", user.getUID(),notificationID);
        //get the document reference
        //document path = Reminders/UID/AlarmIDList/NotificationID
        DocumentReference reminderDocumentReference = database.document(REMINDER_DOCUMENT_PATH);
        //delete reminder from database
        reminderDocumentReference.delete();
    }
}
