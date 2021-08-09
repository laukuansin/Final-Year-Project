package com.example.a303com_laukuansin.handlers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import com.example.a303com_laukuansin.activities.HomeActivity;
import com.example.a303com_laukuansin.activities.MainActivity;
import com.example.a303com_laukuansin.activities.PersonalInformationActivity;
import com.example.a303com_laukuansin.domains.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.gson.GsonBuilder;


public class SessionHandler {
    private SharedPreferences _sharedPreferences;
    private SharedPreferences.Editor _editor;
    private FirebaseAuth auth;
    private FirebaseFirestore database;
    private Context _context;

    private static final int PRIVATE_MODE = 0;
    private static final String PREFERENCE_NAME = "preferences";
    private static final String KEY_USER = "user";

    public SessionHandler(Context context) {
        this._context = context;
        this._sharedPreferences = this._context.getSharedPreferences(PREFERENCE_NAME, PRIVATE_MODE);
        this._editor = this._sharedPreferences.edit();
        auth = FirebaseAuth.getInstance();//get firebase auth
        database = FirebaseFirestore.getInstance();//get database
    }

    private boolean isLoggedIn()
    {
        return auth.getCurrentUser() != null;
    }

    public void checkAuthorization() {//check authorization
        if(isLoggedIn())//if user is log in before
        {
            CollectionReference userRef = database.collection("Users");//get users collection
            Query query = userRef.whereEqualTo("UID",getUser().getUID());
            query.get().addOnCompleteListener(task -> {
                if (task.isSuccessful())
                {
                    int size = task.getResult().size();//get the size of user
                    if(size==0)//mean the user did not fill personal information before, he need go to fill personal information
                    {
                        Intent intent = new Intent(this._context, PersonalInformationActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        this._context.startActivity(intent);
                    }
                    else{//fill finish information before, go to main activity
                        Intent intent = new Intent(this._context, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        this._context.startActivity(intent);
                    }
                }
                else{
                    Log.d("Error","Query fail!");
                }
            });
        }
        else{//user never log in before
            Intent intent = new Intent(this._context, HomeActivity.class);
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
