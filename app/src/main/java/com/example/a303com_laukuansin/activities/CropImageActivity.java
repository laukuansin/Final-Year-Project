package com.example.a303com_laukuansin.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.a303com_laukuansin.R;
import com.example.a303com_laukuansin.cores.BaseActivity;
import com.example.a303com_laukuansin.utilities.OnSingleClickListener;
import com.isseiaoki.simplecropview.CropImageView;
import com.isseiaoki.simplecropview.callback.CropCallback;
import com.isseiaoki.simplecropview.callback.LoadCallback;
import com.isseiaoki.simplecropview.callback.SaveCallback;

import java.io.File;
import java.io.FileOutputStream;

import androidx.core.content.ContextCompat;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class CropImageActivity extends BaseActivity {
    private String imageUriString;
    public static final String URI_STRING_KEY = "uri_string_key";
    public static final String RESULT_RETURN_KEY = "result_return_key";
    private Uri imageUri;
    private CropImageView _cropImageView;
    private SweetAlertDialog _progressDialog;

    @Override
    protected int ContentView() {
        return R.layout.activity_crop_image;
    }

    @Override
    protected boolean RequiredInternetConnection() {
        return false;
    }

    @Override
    protected void AttemptSave() {

    }

    @Override
    protected void AttemptDelete() {

    }

    @Override
    protected void AttemptSearch() {

    }

    @Override
    protected void AttemptAdd() {

    }

    @Override
    protected void AttemptHelp() {

    }

    @Override
    protected int MenuResource() {
        return 0;
    }

    @Override
    protected boolean DisableActionMenu() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            if (bundle.containsKey(URI_STRING_KEY)) {
                imageUriString = bundle.getString(URI_STRING_KEY);
                //convert string uri back to uri format
                imageUri = Uri.parse(imageUriString);
            }
        }

        //set the status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.green_700));// set status background dark green
        }

        initialization();

    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putString(URI_STRING_KEY, imageUriString);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        imageUriString = savedInstanceState.getString(URI_STRING_KEY);
    }

    private void initialization() {
        //bind view with id
        _cropImageView = (CropImageView) findViewById(R.id.cropImageView);
        ImageButton _doneButton = (ImageButton) findViewById(R.id.buttonDone);
        ImageButton _rotateLeftButton = (ImageButton) findViewById(R.id.buttonRotateLeft);
        ImageButton _rotateRightButton = (ImageButton) findViewById(R.id.buttonRotateRight);
        Button _fitImageButton = (Button) findViewById(R.id.buttonFitImage);
        Button _4_3ImageButton = (Button) findViewById(R.id.button4_3);
        Button _16_9ImageButton = (Button) findViewById(R.id.button16_9);

        //set progress dialog
        _progressDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        _progressDialog.setContentText("Cropping...");
        _progressDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.green_A700));
        _progressDialog.setCancelable(false);

        //setup up crop image view
        setupCropImageView();

        //when click rotate left
        _rotateLeftButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                _cropImageView.rotateImage(CropImageView.RotateDegrees.ROTATE_M90D);
            }
        });

        //when click rotate right
        _rotateRightButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                _cropImageView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D);
            }
        });

        _fitImageButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                _cropImageView.setCropMode(CropImageView.CropMode.FIT_IMAGE);
            }
        });

        _4_3ImageButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                _cropImageView.setCropMode(CropImageView.CropMode.RATIO_4_3);
            }
        });


        _16_9ImageButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                _cropImageView.setCropMode(CropImageView.CropMode.RATIO_16_9);
            }
        });

        //when click doneButton
        _doneButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                _progressDialog.show();
                _cropImageView.startCrop(imageUri, cropCallback, saveCallback);
            }
        });
    }

    private void setupCropImageView() {
        _cropImageView.setCompressFormat(Bitmap.CompressFormat.JPEG);
        _cropImageView.setCompressQuality(50);
        _cropImageView.setInitialFrameScale(1.0f);
        //load the image
        _cropImageView.startLoad(imageUri, loadCallback);
    }

    private final LoadCallback loadCallback = new LoadCallback() {
        @Override
        public void onSuccess() {
        }

        @Override
        public void onError(Throwable e) {
            Toast.makeText(CropImageActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };


    private final CropCallback cropCallback = new CropCallback() {
        @Override
        public void onSuccess(Bitmap cropped) {

            //success crop the image
            try {
                File file = new File(imageUriString);

                if (file.exists()) {
                    file.delete();
                }
                FileOutputStream outputStream = new FileOutputStream(file);
                cropped.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
                outputStream.flush();
                outputStream.close();
            } catch (Exception e) {
                Log.d("Error", e.getMessage());
            }

        }

        @Override
        public void onError(Throwable e) {
        }
    };

    private final SaveCallback saveCallback = new SaveCallback() {
        @Override
        public void onSuccess(Uri uri) {
            if (_progressDialog.isShowing())
                _progressDialog.dismiss();
            //return the uri image
            Intent intent = new Intent();
            intent.putExtra(RESULT_RETURN_KEY, imageUriString);
            setResult(RESULT_OK, intent);
            finish();
        }

        @Override
        public void onError(Throwable e) {
        }
    };

}
