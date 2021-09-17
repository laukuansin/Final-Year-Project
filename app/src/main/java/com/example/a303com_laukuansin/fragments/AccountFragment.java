package com.example.a303com_laukuansin.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.a303com_laukuansin.R;
import com.example.a303com_laukuansin.activities.ChangePasswordActivity;
import com.example.a303com_laukuansin.activities.MainActivity;
import com.example.a303com_laukuansin.activities.MealActivity;
import com.example.a303com_laukuansin.cores.BaseFragment;
import com.example.a303com_laukuansin.domains.User;
import com.example.a303com_laukuansin.pedometer.SensorListener;
import com.example.a303com_laukuansin.utilities.OnSingleClickListener;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AccountFragment extends BaseFragment{
    private User user;

    public AccountFragment() {
        user = getSessionHandler().getUser();
    }

    public static AccountFragment newInstance()
    {
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

    private void initialization(View view)
    {
        //bind view with id
        Button logout=view.findViewById(R.id.logout);
        LinearLayout _personalInformationLayout = view.findViewById(R.id.personalInformationLayout);
        LinearLayout _reminderLayout = view.findViewById(R.id.reminderLayout);
        LinearLayout _changePasswordLayout = view.findViewById(R.id.changePasswordLayout);
        LinearLayout _privacyPolicyLayout = view.findViewById(R.id.privacyLayout);
        LinearLayout _termsConditionLayout = view.findViewById(R.id.termsConditionLayout);

        //when click personal information
        _personalInformationLayout.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {

            }
        });

        //when click reminder
        _reminderLayout.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {

            }
        });

        //when click change password
        _changePasswordLayout.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent intent = new Intent(getContext(), ChangePasswordActivity.class);
                startActivity(intent);
                //add animation sliding to next activity
                getActivity().overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
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
                FirebaseAuth auth=FirebaseAuth.getInstance();
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
}
