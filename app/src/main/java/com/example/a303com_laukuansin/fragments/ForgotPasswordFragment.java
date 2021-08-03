package com.example.a303com_laukuansin.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.example.a303com_laukuansin.R;
import com.example.a303com_laukuansin.cores.BaseFragment;
import com.example.a303com_laukuansin.utilities.OnSingleClickListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class ForgotPasswordFragment extends BaseFragment {
    private TextInputLayout _inputEmail;
    private Button _submitButton;
    public ForgotPasswordFragment()
    {

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forgot_password, container, false);

        //initialize
        initialization(view);

        //when click submit button action
        _submitButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                checkInput();
            }
        });

        return view;
    }

    private void initialization(View view)
    {
        _inputEmail = view.findViewById(R.id.emailLayout);
        _submitButton = view.findViewById(R.id.submitButton);
    }

    private void checkInput()
    {
        String email = _inputEmail.getEditText().getText().toString().trim();
        boolean check = true;
        if(email.isEmpty())
        {
            _inputEmail.setError("Email address cannot be empty!");
            check = false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            _inputEmail.setError("Invalid Email address format!");
            check = false;
        }
        else{
            _inputEmail.setError(null);
        }
        if(check)
        {
            //progress dialog
            SweetAlertDialog _progressDialog= new SweetAlertDialog(getContext(),SweetAlertDialog.PROGRESS_TYPE);
            _progressDialog.setContentText("Sending reset password email.");
            _progressDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorPrimary));
            _progressDialog.setCancelable(false);
            _progressDialog.show();
            //firebase instance
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                if (_progressDialog.isShowing())
                    _progressDialog.dismiss();
                //if success
                if(task.isSuccessful()){
                    getActivity().getSupportFragmentManager().popBackStack();
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frameLayout,new SuccessEmailFragment());
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
                else{
                    ErrorAlert(task.getException().getMessage(), (sweetAlertDialog) -> sweetAlertDialog.cancel()).show();
                }
            });
        }
    }
}
