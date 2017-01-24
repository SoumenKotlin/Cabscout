package com.app.cabscout.views;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.app.cabscout.R;
import com.app.cabscout.controller.ModelManager;
import com.app.cabscout.model.CSPreferences;
import com.app.cabscout.model.Constants;
import com.app.cabscout.model.Event;
import com.app.cabscout.model.Operations;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class ScheduleActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = ScheduleActivity.class.getSimpleName();
    Toolbar toolbar;
    TextView pickupDate, pickupTime, pickupLocation, dropLocation, carType, scheduleRide;

    ProgressDialog progressDialog;
    String customer_id, pickup_address, drop_address, src_latLng, dest_latLng, date, time;

    private Activity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        initViews();
    }

    public void initViews() {
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("Schedule Ride");
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        pickupDate = (TextView)findViewById(R.id.pickupDate);
        pickupTime = (TextView)findViewById(R.id.pickupTime);
        pickupLocation = (TextView)findViewById(R.id.pickupLocation);
        dropLocation = (TextView)findViewById(R.id.dropLocation);
        carType = (TextView)findViewById(R.id.carType);
        scheduleRide = (TextView)findViewById(R.id.scheduleRide);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Scheduling your ride..");
        progressDialog.setCancelable(false);

        pickupDate.setOnClickListener(this);
        pickupTime.setOnClickListener(this);
        pickupLocation.setOnClickListener(this);
        dropLocation.setOnClickListener(this);
        carType.setOnClickListener(this);
        scheduleRide.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!CSPreferences.readString(activity, "pickup_address").isEmpty()) {
            pickupLocation.setText(CSPreferences.readString(activity, "pickup_address"));
        }
        if (!CSPreferences.readString(activity, "drop_address").isEmpty()) {
            dropLocation.setText(CSPreferences.readString(activity, "drop_address"));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.pickupDate:
                ModelManager.getInstance().getDateTimeManager().getDate(activity);
                break;

            case R.id.pickupTime:
                ModelManager.getInstance().getDateTimeManager().getTime(activity);

                break;

            case R.id.pickupLocation:
                Intent pickupIntent = new Intent(activity, SearchAddressActivity.class);
                pickupIntent.putExtra("Address", "Pickup");
                startActivity(pickupIntent);
                break;

            case R.id.dropLocation:
                Intent dropIntent = new Intent(activity, SearchAddressActivity.class);
                dropIntent.putExtra("Address", "Destination_Book");
                startActivity(dropIntent);
                break;

            case R.id.carType:
                break;

            case R.id.scheduleRide:
                customer_id = CSPreferences.readString(activity, "customer_id");
                pickup_address = CSPreferences.readString(activity, "pickup_address");
                drop_address = CSPreferences.readString(activity, "drop_address");
                src_latLng = CSPreferences.readString(activity, "source_latitude") + ","
                           + CSPreferences.readString(activity, "source_longitude");

                dest_latLng = CSPreferences.readString(activity, "destination_latitude") + ","
                        + CSPreferences.readString(activity, "destination_longitude");

                if (pickup_address.isEmpty() || drop_address.isEmpty() || date.isEmpty() || time.isEmpty()) {
                    Toast.makeText(activity, "Please fill all the details", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    ModelManager.getInstance().getRequestManager().requestRide(activity, Operations.requestRideTask(activity,
                            customer_id, URLEncoder.encode(pickup_address, "utf-8"),
                            URLEncoder.encode(drop_address, "utf-8"), "0", src_latLng, dest_latLng, "1",
                            URLEncoder.encode(date, "utf-8"), URLEncoder.encode(time, "utf-8"), "0", "100"));

                }  catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                progressDialog.show();
                break;
        }
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
            case Constants.DATE_SUCCESS:
                date = event.getValue();
                pickupDate.setText(event.getValue());
                break;

            case Constants.TIME_SUCCESS:
                time = event.getValue();
                pickupTime.setText(event.getValue());
                break;

            case Constants.SOURCE_SUCCESS:
                Log.e(TAG, "pickup address");
                pickupLocation.setText(CSPreferences.readString(activity, "pickup_address"));
                break;

            case Constants.DESTINATION_RIDE_SUCCESS:
                dropLocation.setText(CSPreferences.readString(activity, "drop_address"));
                break;

            case Constants.REQUEST_RIDE_SUCCESS:
                progressDialog.dismiss();
                Toast.makeText(activity, "Your ride has been scheduled successfully", Toast.LENGTH_SHORT).show();
                break;

            case Constants.REQUEST_RIDE_FAILED:
                progressDialog.dismiss();
                Toast.makeText(activity, "No driver found", Toast.LENGTH_SHORT).show();
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
