package com.example.a303com_laukuansin.cores;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.example.a303com_laukuansin.R;
import com.example.a303com_laukuansin.handlers.SessionHandler;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.MenuRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public abstract class BaseAppCompatActivity extends AppCompatActivity {
    private SessionHandler _sessionHandler;
    private static BaseAppCompatActivity _instance;
    private AppController _appController;
    //private AccessTokenTracker _accessTokenTracker;
    public static BaseAppCompatActivity getInstance()
    {
        return _instance;
    }
    protected abstract @LayoutRes
    int ContentView();
    protected abstract void AttemptSave();
    protected abstract void AttemptDelete();
    protected abstract void AttemptSearch();
    protected abstract void AttemptAdd();
    protected abstract void AttemptFilter();
    protected abstract void AttemptRefresh();
    protected abstract @MenuRes
    int MenuResource();
    protected abstract boolean DisableActionMenu();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _instance = this;
        _sessionHandler = AppController.getInstance().getSessionHandler();
        _appController = (AppController)getApplicationContext();
        setContentView(ContentView());

        //check login or not
        _appController.getSessionHandler().checkAuthorization();
        /*if (_appController.getSessionHandler().isLoginWithFacebook()){
            FacebookSdk.sdkInitialize(getApplicationContext());
            _accessTokenTracker = new AccessTokenTracker() {
                @Override
                protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                    if (currentAccessToken == null){
                        if (_appController.getSessionHandler().isLoginWithFacebook())
                            LoginManager.getInstance().logOut();

                        _appController.getSessionHandler().clearLoginSession();
                        //  _appController.getSessionHandler().checkAuthorization();
                    }else{
                        if (oldAccessToken != currentAccessToken){
                            _appController.getSessionHandler().setFacebookSession(currentAccessToken.getUserId(),
                                    currentAccessToken.getToken());
                        }

                    }
                }
            };
        }*/

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        _sessionHandler = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (!DisableActionMenu()){
            getMenuInflater().inflate(MenuResource(), menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        switch (item.getItemId()){
            case R.id.action_save:
                AttemptSave();
                return true;
            case R.id.action_delete:
                AttemptDelete();
                return true;
            case R.id.action_add:
                AttemptAdd();
                return true;
            case R.id.action_search:
                AttemptSearch();
                return true;
            case R.id.action_filter:
                AttemptFilter();
                return true;
            case R.id.action_refresh:
                AttemptRefresh();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    protected void hideSoftKeyboard(){
        if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null){
            InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.RESULT_UNCHANGED_SHOWN);
        }
    }

    public SessionHandler getSessionHandler(){
        return _sessionHandler;
    }

    public Snackbar initSnackbar(@IdRes int contentId, String message, int duration){
        Snackbar snackbar = Snackbar.make(findViewById(contentId), message, duration);

        View snackbarView = snackbar.getView();

        int snackbarTextId = R.id.snackbar_text;
        TextView textView = (TextView)snackbarView.findViewById(snackbarTextId);
        textView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white_1000));

        return snackbar;
    }

    public Snackbar initSnackbar(@IdRes int contentId, @StringRes int messageId, int duration){
        return initSnackbar(contentId, getResources().getText(messageId).toString(), duration);
    }
}