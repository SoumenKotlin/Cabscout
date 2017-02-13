package com.app.cabscout.views;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.app.cabscout.R;
import com.app.cabscout.controller.ModelManager;
import com.app.cabscout.controller.TripsHistoryManager;
import com.app.cabscout.model.CSPreferences;
import com.app.cabscout.model.Constants;
import com.app.cabscout.model.Event;
import com.app.cabscout.model.Operations;
import com.app.cabscout.views.adapters.TripsHistoryAdapter;
import com.github.rahatarmanahmed.cpv.CircularProgressView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class TripsHistoryActivity extends AppCompatActivity {

    Toolbar toolbar;
    private RecyclerView recyclerView;
    private CircularProgressView progressView;
    private TextView noRides;
    Activity activity = this;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trips_history);

        initViews();
    }

    public void initViews() {
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("Trips History");
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        progressView = (CircularProgressView)findViewById(R.id.progressView);
        progressView.setVisibility(View.VISIBLE);
        noRides = (TextView)findViewById(R.id.noRides);
    }

    @Override
    protected void onResume() {
        super.onResume();

        ModelManager.getInstance().getTripsHistoryManager().getTripsHistory(activity, Operations.getTripsHistory(
                activity, CSPreferences.readString(activity, "customer_id")));
    }

    @Override
    protected void onStart() {
        super.onStart();

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEvent(Event event) {

        switch (event.getKey()) {

            case Constants.TRIPS_HISTORY_SUCCESS:
                progressView.setVisibility(View.GONE);
                TripsHistoryAdapter tripsHistoryAdapter = new TripsHistoryAdapter(activity, TripsHistoryManager.tripsList);
                recyclerView.setAdapter(tripsHistoryAdapter);
                break;

            case Constants.TRIPS_HISTORY_EMPTY:
                progressView.setVisibility(View.GONE);
                noRides.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}
