package com.example.a303com_laukuansin.cores;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.example.a303com_laukuansin.R;
import com.example.a303com_laukuansin.handlers.SessionHandler;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.IdRes;
import androidx.annotation.StringRes;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class BaseFragment extends Fragment {
    private static final int PERMISSION_CODE = 100;

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

    public void isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED ||
                    getActivity().checkSelfPermission(Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED)//if storage or camera did not get permission
            {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA},PERMISSION_CODE);//ask permission for storage and camera
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {//if same permission code
            if (grantResults.length > 0)
            {
                if(grantResults[0] != PackageManager.PERMISSION_GRANTED && grantResults[1]!=PackageManager.PERMISSION_GRANTED)//if denied camera and storage permission
                {
                    Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                else if(grantResults[0] != PackageManager.PERMISSION_GRANTED)//if denied storage permission only
                {
                    Toast.makeText(getContext(), "Storage Permission Denied", Toast.LENGTH_SHORT).show();
                }
                else if(grantResults[1]!=PackageManager.PERMISSION_GRANTED)//if denied camera permission only
                {
                    Toast.makeText(getContext(), "Camera Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    protected SweetAlertDialog ErrorAlert(String message,SweetAlertDialog.OnSweetClickListener positiveButtonAction)
    {
        SweetAlertDialog dialog = new SweetAlertDialog(this.getContext(),SweetAlertDialog.ERROR_TYPE);
        dialog.setTitleText(R.string.alert_title_error);
        dialog.setContentText(message);
        dialog.setConfirmButton(R.string.alert_ok,positiveButtonAction);
        return dialog;
    }
}
