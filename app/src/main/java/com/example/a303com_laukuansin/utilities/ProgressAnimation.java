package com.example.a303com_laukuansin.utilities;

import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.google.android.material.progressindicator.LinearProgressIndicator;

//animation for progress bar
public class ProgressAnimation extends Animation {
    private LinearProgressIndicator progressIndicator;
    private float from;
    private float to;

    public ProgressAnimation(LinearProgressIndicator progressIndicator, float from, float to) {
        super();
        this.progressIndicator = progressIndicator;
        this.from = from;
        this.to = to;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        float value = from+(to-from)*interpolatedTime;
        progressIndicator.setProgress((int)value);
    }
}
