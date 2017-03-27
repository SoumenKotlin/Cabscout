package com.app.cabscout.views;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.app.cabscout.R;
import com.app.cabscout.controller.ModelManager;
import com.app.cabscout.model.CSPreferences;
import com.app.cabscout.model.Config;
import com.app.cabscout.model.Constants;
import com.app.cabscout.model.Event;
import com.app.cabscout.model.Operations;
import com.app.cabscout.model.Utils;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.app.cabscout.model.CSPreferences.readString;

@SuppressLint("NewApi")
public class CabArrivingActivity extends AppCompatActivity implements OnMapReadyCallback, RoutingListener,
        View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private static final String TAG = CabArrivingActivity.class.getSimpleName();
    double driver_lat, driver_lng, source_lat, source_lng, dest_lat, dest_lng;
    String driver_profile_pic, driver_mobile, driver_name;
    private GoogleMap gMap;
    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.colorAccent,R.color.colorAccent,
            R.color.colorAccent,R.color.colorAccent,R.color.colorAccent};

    protected LatLng start,end;
    TextView cancelRide, driverName, carType, callDriver;
    CircleImageView driverPic;
    AppCompatActivity activity = this;
    Dialog dialog, cancelRequestDialog;
    ArrayList<String> reasonsList;
    RadioButton radioButton;
    String cancelReason;
    TextView cancel, okay;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cab_arriving);

        initViews();
    }

    public void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("On Trip");
        setSupportActionBar(toolbar);

        polylines = new ArrayList<>();
        reasonsList = new ArrayList<>();

        driverName = (TextView)findViewById(R.id.driverName);
        callDriver = (TextView)findViewById(R.id.callDriver);
        carType = (TextView)findViewById(R.id.carType);
        driverPic = (CircleImageView)findViewById(R.id.driverPic);

        source_lat = Double.parseDouble(CSPreferences.readString(activity, "source_latitude"));
        source_lng = Double.parseDouble(CSPreferences.readString(activity, "source_longitude"));
        dest_lat = Double.parseDouble(CSPreferences.readString(activity, "destination_latitude"));
        dest_lng = Double.parseDouble(CSPreferences.readString(activity, "destination_longitude"));

        String driver_coordinates = CSPreferences.readString(activity, "driver_coordinates");
        String[] splitArray = driver_coordinates.split(",");
        driver_lat = Double.valueOf(splitArray[splitArray.length -2]);
        driver_lng = Double.valueOf(splitArray[splitArray.length -1]);

        driver_name = readString(activity, "driver_name");
        driver_profile_pic = readString(activity, "driver_profile_pic");
        driver_mobile = readString(activity, "driver_mobile");

        cancelRide = (TextView)findViewById(R.id.cancelRide);
        cancelRide.setOnClickListener(this);

        start = new LatLng(driver_lat, driver_lng);
        end = new LatLng(source_lat, source_lng);
        String car_type = CSPreferences.readString(this, "car_type");

        switch (car_type) {
            case "0":
                car_type = "Any Car";
                break;

            case "1":
                car_type = "Regular Car";
                break;

            case "2":
                car_type = "Deluxe Car";
                break;
        }

        driverName.setText(driver_name);
        carType.setText(car_type);
        Picasso.with(this).load(Config.user_pic_url+driver_profile_pic).into(driverPic);
        callDriver.setOnClickListener(this);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        reasons();
    }

    public void reasons() {
        reasonsList.add("I was not ready");
        reasonsList.add("Driver is late");
        reasonsList.add("Driver denies duty");
        reasonsList.add("Other reasons");
    }

    public void showDialog() {
        dialog = Utils.makeDialog(this, R.layout.cancel_ride_reasons);
        RadioGroup radioGroup = (RadioGroup)dialog.findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(this);

        cancel = (TextView)dialog.findViewById(R.id.cancel);
        okay = (TextView)dialog.findViewById(R.id.okay);

        cancel.setOnClickListener(this);
        okay.setOnClickListener(this);

        for (int i=0; i<reasonsList.size(); i++) {
            radioButton = new RadioButton(this);
            radioButton.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            radioButton.setLayoutParams(layoutParams);
            radioButton.setTextColor(Color.BLACK);
            radioButton.setPadding(10,10,10,10);
            radioButton.setId(i);

            Typeface custom_font = Typeface.createFromAsset(getAssets(),  "Mark Simonson - Proxima Nova Semibold_0.ttf");

            radioButton.setTypeface(custom_font);

            radioButton.setText(reasonsList.get(i));
            radioGroup.addView(radioButton);

            dialog.show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.callDriver:
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+driver_mobile));
                startActivity(intent);
                break;

            case R.id.cancelRide:
                //Utils.showAlert(activity, Constants.CANCEL_TRIP);
                showDialog();
                cancelReason = "";
                break;

            case R.id.cancel:
                dialog.dismiss();
                break;

            case R.id.okay:
                if (cancelReason.isEmpty()) {
                    Toast.makeText(activity, "Please select any of the reason", Toast.LENGTH_SHORT).show();
                    return;
                }

                String ride_request_id = CSPreferences.readString(activity, "ride_request_id");
                String customer_id = CSPreferences.readString(activity, "customer_id");

                ModelManager.getInstance().getCancelRequestManager().cancelRequest(activity,
                        Operations.cancelRequestTask(activity, customer_id, ride_request_id, cancelReason));

                cancelRequestDialog = Utils.customProgressDialog(this, "Canceling your request...");
                cancelRequestDialog.show();

                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        // EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(Event event) {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();

        if (cancelRequestDialog != null && cancelRequestDialog.isShowing())
            cancelRequestDialog.dismiss();

        switch (event.getKey()) {

            case Constants.CANCELLED_REQUEST_SUCCESS:
               //Toast.makeText(getApplicationContext(), "Request has been cancelled successfully", Toast.LENGTH_SHORT).show();
                finish();
                break;

            case Constants.CANCELLED_REQUEST_ERROR:
                Toast.makeText(activity, "Unable to process your request. Please try again.", Toast.LENGTH_SHORT).show();
                break;

            case Constants.TRIP_START_SUCCESS:

                gMap.clear();
                start = new LatLng(source_lat, source_lng);
                end = new LatLng(dest_lat, dest_lng);

                Routing routing = new Routing.Builder()
                        .travelMode(Routing.TravelMode.DRIVING)
                        .withListener(this)
                        .waypoints(start, end)
                        .build();
                routing.execute();

                break;

            case Constants.TRIP_STOP_SUCCESS:
                finish();
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;

        try {

            boolean success = gMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));
            if (!success) {
                Log.e("sorry try again", "Style parsing failed.");
            }
        }catch (Resources.NotFoundException e){
            e.printStackTrace();
        }

        Routing routing = new Routing.Builder()
                .travelMode(Routing.TravelMode.DRIVING)
                .withListener(this)
                .waypoints(start, end)
                .build();
        routing.execute();
    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int j) {

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.20);
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(start);
        builder.include(end);
        LatLngBounds bounds = builder.build();
        gMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding));


        if(polylines.size()>0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i <route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = gMap.addPolyline(polyOptions);
            polylines.add(polyline);

            Log.e(TAG, "distance-- "+route.get(i).getDistanceValue());
            Log.e(TAG, "time-- "+route.get(i).getDurationValue());
            double d = (route.get(i).getDistanceValue()) / 1000;
            d = Utils.convertKmToMi(d);
            double dt = Utils.round(d, 1);
        }

        // Start marker
        MarkerOptions options = new MarkerOptions();
        options.position(start);
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_icon_pin));
        gMap.addMarker(options);

        // End marker
        options = new MarkerOptions();
        options.position(end);
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_icon_pin));
        gMap.addMarker(options);
    }

    @Override
    public void onRoutingFailure(RouteException e) {
        Toast.makeText(activity, "Invalid request", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingCancelled() {

    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        cancelReason = reasonsList.get(checkedId);
    }
}
