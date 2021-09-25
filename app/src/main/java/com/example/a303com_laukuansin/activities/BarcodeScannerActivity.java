package com.example.a303com_laukuansin.activities;

import android.os.Build;
import android.os.Bundle;

import com.example.a303com_laukuansin.R;
import com.example.a303com_laukuansin.cores.BaseActivity;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

public class BarcodeScannerActivity extends BaseActivity{
    private CaptureManager _capture;
    @Override
    protected int ContentView() {
        return R.layout.activity_barcode_scanner;
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

        setupToolbar();

        DecoratedBarcodeView _barcodeScannerView = (DecoratedBarcodeView)findViewById(R.id.zxing_barcode_scanner);
        _capture = new CaptureManager(this, _barcodeScannerView);
        _capture.initializeFromIntent(getIntent(), savedInstanceState);
        _capture.decode();
    }

    private void setupToolbar()
    {
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.green_700));// set status background dark green
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        this._capture.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        this._capture.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this._capture.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        this._capture.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
