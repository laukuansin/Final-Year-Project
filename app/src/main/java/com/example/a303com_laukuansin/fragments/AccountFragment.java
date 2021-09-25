package com.example.a303com_laukuansin.fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.a303com_laukuansin.R;
import com.example.a303com_laukuansin.activities.ChangePasswordActivity;
import com.example.a303com_laukuansin.activities.EditPersonalInformationActivity;
import com.example.a303com_laukuansin.activities.MainActivity;
import com.example.a303com_laukuansin.activities.MealActivity;
import com.example.a303com_laukuansin.activities.ReminderActivity;
import com.example.a303com_laukuansin.broadcastReceiver.ReminderService;
import com.example.a303com_laukuansin.cores.BaseFragment;
import com.example.a303com_laukuansin.domains.User;
import com.example.a303com_laukuansin.pedometer.SensorListener;
import com.example.a303com_laukuansin.utilities.NotificationAlarm;
import com.example.a303com_laukuansin.utilities.OnSingleClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import de.hdodenhof.circleimageview.CircleImageView;

public class AccountFragment extends BaseFragment {
    private User user;
    private TextView _nameView, _emailView;
    private CircleImageView _profileImageView;

    public AccountFragment() {
    }

    public static AccountFragment newInstance() {
        return new AccountFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        initialization(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    private void initialization(View view) {
        //bind view with id
        Button logout = view.findViewById(R.id.logout);
        LinearLayout _personalInformationLayout = view.findViewById(R.id.personalInformationLayout);
        LinearLayout _reminderLayout = view.findViewById(R.id.reminderLayout);
        LinearLayout _changePasswordLayout = view.findViewById(R.id.changePasswordLayout);
        LinearLayout _privacyPolicyLayout = view.findViewById(R.id.privacyLayout);
        LinearLayout _termsConditionLayout = view.findViewById(R.id.termsConditionLayout);
        _nameView = view.findViewById(R.id.name);
        _emailView = view.findViewById(R.id.email);
        _profileImageView = view.findViewById(R.id.profileImage);

        //when click personal information
        _personalInformationLayout.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent intent = new Intent(getContext(), EditPersonalInformationActivity.class);
                startActivity(intent);
                //add animation sliding to next activity
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        //when click reminder
        _reminderLayout.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent intent = new Intent(getContext(), ReminderActivity.class);
                startActivity(intent);
                //add animation sliding to next activity
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        //when click change password
        _changePasswordLayout.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent intent = new Intent(getContext(), ChangePasswordActivity.class);
                startActivity(intent);
                //add animation sliding to next activity
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        //when click privacy policy
        _privacyPolicyLayout.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                //link to the privacy policy link
                String url = "https://www.termsfeed.com/live/0a3ce8d7-89f8-46ba-8b28-00d69656d822";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        //when click terms and condition
        _termsConditionLayout.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                //link to the terms and conditions link
                String url = "https://www.websitepolicies.com/policies/view/4KvzuEdd";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        //when click log out button
        logout.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                //stop the step service
                getActivity().stopService(new Intent(getContext(), SensorListener.class));
                //remove all reminder has set
                NotificationAlarm notificationAlarm = new NotificationAlarm(getContext());
                notificationAlarm.removeAllNotification();
                FirebaseAuth auth = FirebaseAuth.getInstance();
                //redirect to main activity
                Intent intent = new Intent(getContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                //sign out the authentication
                auth.signOut();
                //clear the preferences
                getSessionHandler().clearLoginSession();
            }
        });
    }

    private void loadData()
    {
        user = getSessionHandler().getUser();
        _nameView.setText(user.getName());
        _emailView.setText(user.getEmailAddress());
        //set profile image
        if (!user.getProfileImage().isEmpty()) {
            Picasso.get().load(user.getProfileImage()).placeholder(R.drawable.ic_profile_picture).into(_profileImageView);
        }
    }
}
