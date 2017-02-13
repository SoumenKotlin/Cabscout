package com.app.cabscout.views;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.app.cabscout.R;

@SuppressLint("NewApi")
public class ExtraActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_rating);

    }
}
