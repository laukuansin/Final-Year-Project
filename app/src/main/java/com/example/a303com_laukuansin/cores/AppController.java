package com.example.a303com_laukuansin.cores;
import android.view.ViewConfiguration;

import com.example.a303com_laukuansin.handlers.SessionHandler;

import java.lang.reflect.Field;

import androidx.multidex.MultiDexApplication;

public class AppController extends MultiDexApplication {
    private SessionHandler _sessionHandler;
    public SessionHandler getSessionHandler(){
        return _sessionHandler;
    }
    private static AppController _instance;

    public static synchronized AppController getInstance() {
        return _instance;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        _instance = this;

        _sessionHandler = new SessionHandler(this);
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if(menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception ex) {
            // Ignore
        }
        // Initialize the SDK before executing any other operations,
        //FacebookSdk.sdkInitialize(getApplicationContext());
        //AppEventsLogger.activateApp(this);
    }
}
