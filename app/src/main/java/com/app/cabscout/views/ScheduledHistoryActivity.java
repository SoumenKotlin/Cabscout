package com.app.cabscout.views;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.app.cabscout.R;
import com.app.cabscout.controller.ModelManager;
import com.app.cabscout.controller.ScheduleHistoryManager;
import com.app.cabscout.model.Beans.ScheduleHistoryBeans;
import com.app.cabscout.model.CSPreferences;
import com.app.cabscout.model.Constants;
import com.app.cabscout.model.Event;
import com.app.cabscout.model.Operations;
import com.app.cabscout.views.adapters.ScheduledHistoryAdapter;
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ScheduledHistoryActivity extends AppCompatActivity {

    //private static final String TAG = ScheduledHistoryActivity.class.getSimpleName();
    Toolbar toolbar;
    private RecyclerView recyclerView;
    private CircleProgressBar progressView;
    private TextView noScheduledRides;
    Activity activity = this;
    ArrayList<ScheduleHistoryBeans> list;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheduled_history);

        initViews();
    }

    public void initViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Scheduled Rides");
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        progressView = (CircleProgressBar) findViewById(R.id.progressView);
        progressView.setColorSchemeColors(Color.RED, Color.YELLOW, Color.BLACK);
        progressView.setVisibility(View.VISIBLE);
        noScheduledRides = (TextView)findViewById(R.id.noRides);

        ModelManager.getInstance().getScheduleHistoryManager().getScheduleHistory(activity, Operations.getScheduledRides(activity,
                CSPreferences.readString(activity, "customer_id")));
    }

    @Override
    protected void onResume() {
        super.onResume();

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

            case Constants.SCHEDULED_HISTORY_SUCCESS:

                progressView.setVisibility(View.GONE);
                list = ScheduleHistoryManager.schedulesList;
                Log.e("Size of list: ", ""+list.size());
                ScheduledHistoryAdapter scheduledHistoryAdapter = new ScheduledHistoryAdapter(activity, list);
                recyclerView.setAdapter(scheduledHistoryAdapter);
               /* scheduledHistoryAdapter.notifyDataSetChanged();*/
                break;

            case Constants.SCHEDULED_HISTORY_EMPTY:
                progressView.setVisibility(View.GONE);
                noScheduledRides.setVisibility(View.VISIBLE);
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
