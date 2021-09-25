package com.example.a303com_laukuansin.cores;

import android.os.Bundle;

import com.example.a303com_laukuansin.R;
import com.example.a303com_laukuansin.handlers.SessionHandler;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.IdRes;
import androidx.fragment.app.Fragment;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class BaseFragment extends Fragment {
    private SessionHandler _sessionHandler;

    public SessionHandler getSessionHandler() {
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

    public Snackbar initSnackbar(@IdRes int contentId, String message, int duration) {
        return ((BaseAppCompatActivity) getActivity()).initSnackbar(contentId, message, duration);
    }

    protected SweetAlertDialog ErrorAlert(String message, SweetAlertDialog.OnSweetClickListener positiveButtonAction, boolean cancelable) {
        SweetAlertDialog dialog = new SweetAlertDialog(this.getContext(), SweetAlertDialog.ERROR_TYPE);
        dialog.setTitleText(R.string.alert_title_error);
        dialog.setContentText(message);
        dialog.setConfirmButton(R.string.alert_ok, positiveButtonAction);
        dialog.setCancelable(cancelable);
        return dialog;
    }

    protected SweetAlertDialog showProgressDialog(String message, int color) {
        SweetAlertDialog dialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        dialog.setContentText(message);
        dialog.getProgressHelper().setBarColor(color);
        dialog.setCancelable(false);
        return dialog;
    }
}
