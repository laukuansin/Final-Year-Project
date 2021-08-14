package com.example.a303com_laukuansin.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.a303com_laukuansin.R;
import com.example.a303com_laukuansin.activities.MainActivity;
import com.example.a303com_laukuansin.cores.AppController;
import com.example.a303com_laukuansin.cores.BaseFragment;
import com.example.a303com_laukuansin.domains.User;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

public class HomeFragment extends BaseFragment {
    private User user;
    private TextView dateView;
    private SimpleDateFormat dateFormat;
    private boolean isExpanded = false;
    private CompactCalendarView _calendarView;

    public HomeFragment() {
        user = getSessionHandler().getUser();
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initialization(view);

        return view;
    }

    private void initialization(View view)
    {
        getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getContext(),R.color.white));// set status background white
        }

        //set date format
        dateFormat= new SimpleDateFormat("d MMM yyyy");

        _calendarView = view.findViewById(R.id.calendarView);
        dateView = view.findViewById(R.id.date_view);
        ImageView arrow = view.findViewById(R.id.arrowView);
        LinearLayout datePickerButton = view.findViewById(R.id.datePickerLayout);
        setDate(new Date());

        //setup calendar

        //on click date
        datePickerButton.setOnClickListener(v -> {
            float rotation = isExpanded ? 0 : 180;
            ViewCompat.animate(arrow).rotation(rotation).start();

            isExpanded = !isExpanded;
            if(isExpanded)
            {
            }
            else{
            }
        });
    }

    private void setDate(Date date)
    {
        if(date.equals(new Date()))
        {
            dateView.setText("Today");
        }
        else{
            dateView.setText(dateFormat.format(date));
        }
    }
}
