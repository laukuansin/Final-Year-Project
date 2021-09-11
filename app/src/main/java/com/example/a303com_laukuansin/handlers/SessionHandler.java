package com.example.a303com_laukuansin.handlers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import com.example.a303com_laukuansin.activities.MainActivity;
import com.example.a303com_laukuansin.activities.HomeActivity;
import com.example.a303com_laukuansin.activities.PersonalInformationActivity;
import com.example.a303com_laukuansin.domains.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.gson.GsonBuilder;

import androidx.annotation.Nullable;


public class SessionHandler {
    private SharedPreferences _sharedPreferences;
    private SharedPreferences.Editor _editor;
    private FirebaseAuth auth;
    private FirebaseFirestore database;
    private Context _context;

    private static final String PREFERENCE_NAME = "preferences";
    private static final String KEY_USER = "user";

    public SessionHandler(Context context) {
        this._context = context;
        this._sharedPreferences = this._context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        this._editor = this._sharedPreferences.edit();
        auth = FirebaseAuth.getInstance();//get firebase auth
        database = FirebaseFirestore.getInstance();//get database
    }

    public boolean isLoggedIn()
    {
        return auth.getCurrentUser() != null;
    }

    public void checkAuthorization() {//check authorization
        if(isLoggedIn())//if user is log in before
        {
            FirebaseUser firebaseUser = auth.getCurrentUser();//get current user
            DocumentReference docRef = database.collection("Users").document(firebaseUser.getUid());//get users document
            docRef.addSnapshotListener((documentSnapshot, error) -> {
                if(error!=null)//if have error
                {
                    Log.d("Error:",error.getMessage());
                    return;
                }
                if(documentSnapshot.exists())//if user document is exists
                {
                    loadUser(documentSnapshot,firebaseUser);

                    Intent intent = new Intent(_context, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    _context.startActivity(intent);
                }
                else{//else user does not exists
                    Intent intent = new Intent(_context, PersonalInformationActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    _context.startActivity(intent);
                }
            });
        }
        else{//user never log in before
            Intent intent = new Intent(this._context, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            this._context.startActivity(intent);
        }

    }

    private void loadUser(DocumentSnapshot documentSnapshot,FirebaseUser firebaseUser)
    {
        User user = new User();
        user.setUID(firebaseUser.getUid());
        user.setEmailAddress(firebaseUser.getEmail());
        user.setName(documentSnapshot.getString("name"));
        user.setGender(documentSnapshot.getString("gender"));
        user.setActivityLevel(documentSnapshot.getDouble("activityLevel"));
        user.setHeight(documentSnapshot.getLong("height").intValue());
        user.setStartWeight(documentSnapshot.getDouble("startWeight"));
        user.setWeight(documentSnapshot.getDouble("weight"));
        user.setTargetWeight(documentSnapshot.getDouble("targetWeight"));
        user.setYearOfBirth(documentSnapshot.getLong("yearOfBirth").intValue());

        setUser(user);
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
