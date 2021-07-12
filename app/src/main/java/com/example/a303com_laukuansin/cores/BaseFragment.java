package com.example.a303com_laukuansin.cores;

import android.content.DialogInterface;
import android.os.Bundle;

import com.example.a303com_laukuansin.R;
import com.example.a303com_laukuansin.handlers.SessionHandler;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.IdRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

public class BaseFragment extends Fragment {
    private SessionHandler _sessionHandler;

    public SessionHandler getSessionHandler(){
        if (_sessionHandler == null)
            _sessionHandler = AppController.getInstance().getSessionHandler();

        return _sessionHandler;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _sessionHandler = AppController.getInstance().getSessionHandler();
        setRetainInstance(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        _sessionHandler = null;
    }
    public Snackbar initSnackbar(@IdRes int contentId, String message, int duration){
        return ((BaseAppCompatActivity)getActivity()).initSnackbar(contentId, message, duration);
    }

    public Snackbar initSnackbar(@IdRes int contentId, @StringRes int messageId, int duration){
        return ((BaseAppCompatActivity)getActivity()).initSnackbar(contentId, messageId, duration);
    }

    protected AlertDialog AlertMessage(@StringRes int titleResourceId, @StringRes int messageResourceId, @StringRes int positiveButtonLabelResourceId, DialogInterface.OnClickListener positiveButtonAction,
                                       @StringRes int negativeButtonLabelResourceId, DialogInterface.OnClickListener negativeButtonAction){
        return AlertMessage(getString(titleResourceId), getString(messageResourceId), positiveButtonLabelResourceId != 0 ? getString(positiveButtonLabelResourceId) : "", positiveButtonAction,
                negativeButtonLabelResourceId != 0 ? getString(negativeButtonLabelResourceId) : "", negativeButtonAction);
    }

    protected AlertDialog AlertMessage(String title, String message, String positiveButtonLabel, DialogInterface.OnClickListener positiveButtonAction,
                                       String negativeButtonLabel, DialogInterface.OnClickListener negativeButtonAction){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.getContext());
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);
        //null should be your on click listener
        if (positiveButtonAction != null){
            alertDialogBuilder.setPositiveButton(positiveButtonLabel, positiveButtonAction);
        }
        if (negativeButtonAction != null){
            alertDialogBuilder.setNegativeButton(negativeButtonLabel, negativeButtonAction);
        }

        return alertDialogBuilder.create();
    }

    protected AlertDialog WarningAlert(@StringRes int messageResourceId, DialogInterface.OnClickListener positiveButtonAction){
        return AlertMessage(R.string.alert_title_warning, messageResourceId, R.string.alert_close, positiveButtonAction, 0, null);
    }

    protected AlertDialog SuccessAlert(@StringRes int messageResourceId, DialogInterface.OnClickListener positiveButtonAction){
        return AlertMessage(R.string.alert_title_success, messageResourceId, R.string.alert_close, positiveButtonAction, 0, null);
    }

    protected AlertDialog ErrorAlert(@StringRes int messageResourceId, DialogInterface.OnClickListener positiveButtonAction) {
        return AlertMessage(R.string.alert_title_error, messageResourceId, R.string.alert_close, positiveButtonAction, 0, null);
    }

    protected AlertDialog WarningAlert(String message, DialogInterface.OnClickListener positiveButtonAction){
        return AlertMessage(getString(R.string.alert_title_warning), message, getString(R.string.alert_close), positiveButtonAction, "", null);
    }

    protected AlertDialog SuccessAlert(String message, DialogInterface.OnClickListener positiveButtonAction){
        return AlertMessage(getString(R.string.alert_title_success), message, getString(R.string.alert_close), positiveButtonAction, "", null);
    }

    protected AlertDialog ErrorAlert(String message, DialogInterface.OnClickListener positiveButtonAction){
        return AlertMessage(getString(R.string.alert_title_error), message, getString(R.string.alert_close), positiveButtonAction, "", null);
    }
}
