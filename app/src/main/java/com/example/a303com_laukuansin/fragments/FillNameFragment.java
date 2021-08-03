package com.example.a303com_laukuansin.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a303com_laukuansin.R;
import com.example.a303com_laukuansin.activities.PersonalInformationActivity;
import com.example.a303com_laukuansin.cores.BaseFragment;
import com.example.a303com_laukuansin.domains.User;
import com.example.a303com_laukuansin.utilities.OnSingleClickListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class FillNameFragment extends BaseFragment {
    private User user;
    private TextInputLayout _inputName;
    private TextInputEditText _inputEditName;
    private TextView _nextButton;
    private OnReturnNameListener _listener;

    public FillNameFragment(User user) {
        Bundle args = new Bundle();
        args.putSerializable(PersonalInformationActivity.KEY_USER,user);
        setArguments(args);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = (User) getArguments().getSerializable(PersonalInformationActivity.KEY_USER);
        }
        setHasOptionsMenu(false);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_fill_name, container, false);

        //initialize
        initialization(view);

        _nextButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                user.setName(_inputName.getEditText().toString().trim());
                _listener.returnUserWithName(user);
            }
        });
        return view;

    }

    private void initialization(View view)
    {
        _inputName = view.findViewById(R.id.nameLayout);
        _inputEditName = view.findViewById(R.id.editTextName);
        _nextButton = view.findViewById(R.id.nextButton);

        //assign listener
        _listener = (OnReturnNameListener)getContext();

        _inputEditName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().trim().isEmpty())
                {
                    _nextButton.setEnabled(false);
                    _nextButton.setAlpha(0.5f);
                }
                else{
                    _nextButton.setAlpha(1f);
                    _nextButton.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public interface OnReturnNameListener
    {
        void returnUserWithName(User user);

    }


}
