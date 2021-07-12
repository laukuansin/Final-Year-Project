package com.example.a303com_laukuansin.handlers;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionHandler {
    private SharedPreferences _sharedPreferences;
    private SharedPreferences.Editor _editor;
    private Context _context;

    private static final int PRIVATE_MODE = 0;
    private static final String PREFERENCE_NAME = "preferences";

    public SessionHandler(Context context) {
        this._context = context;
        this._sharedPreferences = this._context.getSharedPreferences(PREFERENCE_NAME, PRIVATE_MODE);
        this._editor = this._sharedPreferences.edit();
    }

    public void clearLoginSession() {
        this._editor.clear();
        this._editor.commit();

    }
}
