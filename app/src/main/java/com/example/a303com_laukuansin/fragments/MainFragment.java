package com.example.a303com_laukuansin.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.a303com_laukuansin.R;
import com.example.a303com_laukuansin.activities.SplashScreenActivity;
import com.example.a303com_laukuansin.cores.AppController;
import com.example.a303com_laukuansin.cores.BaseFragment;
import com.example.a303com_laukuansin.utilities.OnSingleClickListener;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MainFragment extends BaseFragment {
    private Button _button;
    public MainFragment() {
    }
    public static MainFragment newInstance() {
        return new MainFragment();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main, container, false);
        _button = view.findViewById(R.id.logout);
        ProgressDialog _progressDialog= new ProgressDialog(getContext());

        _button.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                _progressDialog.setMessage("Please Wait...");
                _progressDialog.setIndeterminate(false);
                _progressDialog.setCancelable(false);
                _progressDialog.show();
                AppController.getInstance().getSessionHandler().clearLoginSession();
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.signOut();
                Intent intent = new Intent(getContext(), SplashScreenActivity.class);
               // Closing all the Activities
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                // Add new Flag to start new Activity
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                getActivity().startActivity(intent);
                _progressDialog.dismiss();
            }
        });
        return view;
    }
}
