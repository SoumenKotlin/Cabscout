package com.app.cabscout.views;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.app.cabscout.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

@SuppressLint("NewApi")
public class ExtraActivity extends AppCompatActivity implements OnMapReadyCallback{

    SlidingPaneLayout mSlidingPanel;


    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.practice);


        initViews();

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    public void initViews() {
        //startService();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
/*
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);*/

        getSupportActionBar().setHomeButtonEnabled(true);

        mSlidingPanel = (SlidingPaneLayout) findViewById(R.id.SlidingPanel);

        mSlidingPanel.setPanelSlideListener(panelListener);
        mSlidingPanel.setParallaxDistance(200);

    }

        SlidingPaneLayout.PanelSlideListener panelListener = new SlidingPaneLayout.PanelSlideListener(){

        @Override
        public void onPanelClosed(View arg0) {
            // TODO Auto-genxxerated method stub
            // getActionBar().setTitle(getString(R.string.app_name));

        }

        @Override
        public void onPanelOpened(View arg0) {
            // TODO Auto-generated method stub
//            getActionBar().setTitle("Menu Titles");
        }

        @Override
        public void onPanelSlide(View arg0, float arg1) {
            // TODO Auto-generated method stub

        }

    };

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
}
