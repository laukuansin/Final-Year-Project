package com.example.a303com_laukuansin.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.a303com_laukuansin.R;
import com.example.a303com_laukuansin.activities.MainActivity;
import com.example.a303com_laukuansin.cores.BaseFragment;
import com.example.a303com_laukuansin.pedometer.SensorListener;
import com.example.a303com_laukuansin.utilities.OnSingleClickListener;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AccountFragment extends BaseFragment{
    public AccountFragment() {
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
        Button logout=view.findViewById(R.id.logout);
        logout.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                getActivity().stopService(new Intent(getContext(), SensorListener.class));
                FirebaseAuth auth=FirebaseAuth.getInstance();
                Intent intent = new Intent(getContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                auth.signOut();
                getSessionHandler().clearLoginSession();
            }
        });
        return view;
    }
}
