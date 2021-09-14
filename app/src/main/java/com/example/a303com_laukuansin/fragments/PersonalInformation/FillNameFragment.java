package com.example.a303com_laukuansin.fragments.PersonalInformation;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.a303com_laukuansin.R;
import com.example.a303com_laukuansin.cores.BaseFragment;
import com.example.a303com_laukuansin.domains.User;
import com.example.a303com_laukuansin.utilities.OnSingleClickListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

public class FillNameFragment extends BaseFragment {
    private User user;
    private TextInputLayout _inputName;
    private OnReturnNameListener _listener;

    public FillNameFragment() {
        user = getSessionHandler().getUser();//get the user from preferences
    }
    public static FillNameFragment newInstance()
    {
        return new FillNameFragment();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_fill_name, container, false);

        //initialize
        initialization(view);


        return view;
    }

    private void loadGenderFragment()//load gender fragment
    {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,R.anim.slide_in_left, R.anim.slide_out_right);//set animation
        fragmentTransaction.replace(R.id.frameLayout,FillGenderFragment.newInstance());
        fragmentTransaction.commit();
    }

    private void initialization(View view)
    {
        _inputName = view.findViewById(R.id.nameLayout);
        TextInputEditText _inputEditName = view.findViewById(R.id.editTextName);
        TextView _nextButton = view.findViewById(R.id.nextButton);

        //initial listener
        _listener = (OnReturnNameListener)getContext();

        if(user.getName()!=null)//if the user name is no empty
        {
            _inputEditName.setText(getSessionHandler().getUser().getName());//display the user name

            //enable the button
            _nextButton.setAlpha(1f);
            _nextButton.setEnabled(true);
        }

        _inputEditName.addTextChangedListener(new TextWatcher() {//when user is typing the name
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().trim().isEmpty())//if the name is empty
                {
                    _nextButton.setEnabled(false);
                    _nextButton.setAlpha(0.3f);
                }
                else{//else not empty
                    _nextButton.setAlpha(1f);
                    _nextButton.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //when click next button action
        _nextButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                user = getSessionHandler().getUser();//get the latest user
                user.setName(_inputName.getEditText().getText().toString().trim());//set the name
                getSessionHandler().setUser(user);//update user
                loadGenderFragment();//load next fragment which is gender fragment
                _listener.nextStep();//call listener

            }
        });
    }

    public interface OnReturnNameListener
    {
        void nextStep();
    }


}
