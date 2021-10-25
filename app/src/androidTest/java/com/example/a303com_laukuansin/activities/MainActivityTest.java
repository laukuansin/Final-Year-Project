package com.example.a303com_laukuansin.activities;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.example.a303com_laukuansin.R;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.annotation.Nullable;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest{
    private MainActivity activity;

    @Rule
    public ActivityTestRule<MainActivity> rule  = new  ActivityTestRule<>(MainActivity.class);

    @Test
    public void start()
    {
        activity = rule.getActivity();
        Button loginButton = null;
        Espresso.onView(ViewMatchers.withId(R.id.loginButton)).perform(ViewActions.click());
        assertThat(loginButton, Matchers.notNullValue());

    }
}