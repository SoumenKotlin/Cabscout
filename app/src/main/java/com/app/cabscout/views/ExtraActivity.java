package com.app.cabscout.views;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.app.cabscout.R;

@SuppressLint("NewApi")
public class ExtraActivity extends AppCompatActivity {
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cab_selection_layout);

        imageView = (ImageView)findViewById(R.id.imageView);

        Animation a = AnimationUtils.loadAnimation(ExtraActivity.this,
                R.anim.rotate_around_center_point);
       // a.setDuration(3000);
        imageView.startAnimation(a);
    }
}
