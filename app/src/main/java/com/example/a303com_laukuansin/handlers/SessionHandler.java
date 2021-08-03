package com.example.a303com_laukuansin.handlers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.a303com_laukuansin.activities.SplashScreenActivity;
import com.example.a303com_laukuansin.domains.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.GsonBuilder;

public class SessionHandler {
    private SharedPreferences _sharedPreferences;
    private SharedPreferences.Editor _editor;
    private FirebaseAuth auth;
    private Context _context;

    private static final int PRIVATE_MODE = 0;
    private static final String PREFERENCE_NAME = "preferences";
    private static final String KEY_USER = "user";

    public SessionHandler(Context context) {
        this._context = context;
        this._sharedPreferences = this._context.getSharedPreferences(PREFERENCE_NAME, PRIVATE_MODE);
        this._editor = this._sharedPreferences.edit();
        auth = FirebaseAuth.getInstance();
    }
    private boolean isLoggedIn()
    {
        return auth.getCurrentUser() != null;
    }

    public void checkAuthorization() {
        if (!this.isLoggedIn()) {
            Intent intent = new Intent(this._context, SplashScreenActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            this._context.startActivity(intent);
        }
    }

    public void setUser(User user)
    {
        this._editor.putString(KEY_USER, new GsonBuilder().create().toJson(user));
        this._editor.commit();
    }

    public User getUser()
    {
        return new GsonBuilder().create().fromJson(this._sharedPreferences.getString(KEY_USER, ""), User.class);
    }


    public void clearLoginSession() {
        this._editor.clear();
        this._editor.commit();

    }
}
