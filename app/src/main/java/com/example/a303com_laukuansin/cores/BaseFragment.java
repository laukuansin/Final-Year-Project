package com.example.a303com_laukuansin.cores;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import com.example.a303com_laukuansin.R;
import com.example.a303com_laukuansin.handlers.SessionHandler;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.IdRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import cn.pedant.SweetAlert.SweetAlertDialog;

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


//    protected AlertDialog WarningAlert(@StringRes int messageResourceId, DialogInterface.OnClickListener positiveButtonAction){
//        return AlertMessage(R.string.alert_title_warning, messageResourceId, R.string.alert_close, positiveButtonAction, 0, null);
//    }
//
//    protected AlertDialog SuccessAlert(@StringRes int messageResourceId, DialogInterface.OnClickListener positiveButtonAction){
//        return AlertMessage(R.string.alert_title_success, messageResourceId, R.string.alert_close, positiveButtonAction, 0, null);
//    }
//
//    protected AlertDialog ErrorAlert(@StringRes int messageResourceId, DialogInterface.OnClickListener positiveButtonAction) {
//        return AlertMessage(R.string.alert_title_error, messageResourceId, R.string.alert_close, positiveButtonAction, 0, null);
//    }
//
//    protected AlertDialog WarningAlert(String message, DialogInterface.OnClickListener positiveButtonAction){
//        return AlertMessage(getString(R.string.alert_title_warning), message, getString(R.string.alert_close), positiveButtonAction, "", null);
//    }
//
//    protected AlertDialog SuccessAlert(String message, DialogInterface.OnClickListener positiveButtonAction){
//        return AlertMessage(getString(R.string.alert_title_success), message, getString(R.string.alert_close), positiveButtonAction, "", null);
//    }
//
//    protected AlertDialog ErrorAlert(String message, DialogInterface.OnClickListener positiveButtonAction){
//        return AlertMessage(getString(R.string.alert_title_error), message, getString(R.string.alert_close), positiveButtonAction, "", null);
//    }

    protected SweetAlertDialog ErrorAlert(String message,SweetAlertDialog.OnSweetClickListener positiveButtonAction)
    {
        SweetAlertDialog dialog = new SweetAlertDialog(this.getContext(),SweetAlertDialog.ERROR_TYPE);
        dialog.setTitleText(R.string.alert_title_error);
        dialog.setContentText(message);
        dialog.setConfirmButton(R.string.alert_ok,positiveButtonAction);
        return dialog;
    }
}
