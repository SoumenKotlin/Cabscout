package com.app.cabscout.views;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.cabscout.R;
import com.app.cabscout.controller.ModelManager;
import com.app.cabscout.controller.NearbyDriversManager;
import com.app.cabscout.model.CSPreferences;
import com.app.cabscout.model.Config;
import com.app.cabscout.model.Constants;
import com.app.cabscout.model.Event;
import com.app.cabscout.model.Operations;
import com.app.cabscout.model.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener, View.OnClickListener{

    private static String TAG = MainActivity.class.getSimpleName();
    Toolbar toolbar;

    GoogleMap googleMap;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest = null;

    Activity activity=this;

    float zoomLevel;
    Location mLastLocation;
    LatLng latLng;
    double lat, lng;

    ImageView imageMarker;
    FloatingActionButton currentLocationButton;
    TextView pickupAddress, dropAddress;
    CardView scheduleRide, selectedCabLayout, pickupSearch, dropSearch;
    RelativeLayout cabSelectionLayout;
    ImageView anyCab, regularCab, deluxeCab;
    private int STORAGE_PERMISSION_CODE = 23;

    private int fingers = 0;
    private long lastZoomTime = 0;
    private float lastSpan = -1;
    private Handler handler = new Handler();

    private ScaleGestureDetector gestureDetector;
    //variable for storing the time of first click
    long startTime;
    //constant for defining the time duration between the click that can be considered as double-tap
    static final int MAX_DURATION = 200;
    RelativeLayout timeLayout;
    Intent serviceIntent;
    String car_type;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
    }

    public void initViews() {
        //startService();

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION}, STORAGE_PERMISSION_CODE);

        imageMarker = (ImageView)findViewById(R.id.marker);
        scheduleRide = (CardView)findViewById(R.id.scheduleRide);
        selectedCabLayout = (CardView)findViewById(R.id.selectedCabLayout);

        scheduleRide.setOnClickListener(this);
        selectedCabLayout.setOnClickListener(this);

        View v = findViewById(R.id.cabSelectionLayout);
        cabSelectionLayout = (RelativeLayout)v.findViewById(R.id.cabSelectionLayout);

        pickupAddress = (TextView)cabSelectionLayout.findViewById(R.id.pickupAddress);
        dropAddress = (TextView)cabSelectionLayout.findViewById(R.id.dropAddress);

        pickupSearch = (CardView)cabSelectionLayout.findViewById(R.id.pickUpSearch);
        dropSearch = (CardView)cabSelectionLayout.findViewById(R.id.dropSearch);

        anyCab = (ImageView)cabSelectionLayout.findViewById(R.id.anyCab);
        regularCab = (ImageView)cabSelectionLayout.findViewById(R.id.regularCab);
        deluxeCab = (ImageView)cabSelectionLayout.findViewById(R.id.deluxeCab);
        currentLocationButton = (FloatingActionButton) cabSelectionLayout.findViewById(R.id.currentLocation);
        currentLocationButton.setOnClickListener(this);

        timeLayout = (RelativeLayout)findViewById(R.id.timeLayout);

        pickupSearch.setOnClickListener(this);
        dropSearch.setOnClickListener(this);
        anyCab.setOnClickListener(this);
        regularCab.setOnClickListener(this);
        deluxeCab.setOnClickListener(this);

        CSPreferences.putString(activity, "car_type", "0");

        initNavigationDrawer();

        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(7000);
        mLocationRequest.setSmallestDisplacement(1);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.currentLocation:
                currentLocationButton.setVisibility(View.GONE);
                initCamera(mLastLocation);
//                startService();
                break;

            case R.id.scheduleRide:
                Intent scheduleIntent = new Intent(activity, ScheduleActivity.class);
                startActivity(scheduleIntent);
                break;

            case R.id.selectedCabLayout:
                break;

            case R.id.pickUpSearch:
                Intent i = new Intent(activity, SearchAddressActivity.class);
                i.putExtra("Address", "Pickup");
                startActivity(i);
                break;

            case R.id.dropSearch:
                Intent i2 = new Intent(activity, SearchAddressActivity.class);
                i2.putExtra("Address", "Destination");
                startActivity(i2);
                break;

            case R.id.anyCab:
                CSPreferences.putString(activity, "car_type", "0");
                anyCab.setImageResource(R.drawable.ic_icon_any_car_selected);
                regularCab.setImageResource(R.drawable.ic_icon_regular_car);
                deluxeCab.setImageResource(R.drawable.ic_icon_deluxe_car);
                break;

            case R.id.regularCab:
                CSPreferences.putString(activity, "car_type", "1");
                regularCab.setImageResource(R.drawable.ic_icon_regular_car_selected);
                deluxeCab.setImageResource(R.drawable.ic_icon_deluxe_car);
                anyCab.setImageResource(R.drawable.ic_icon_any_car);
                break;

            case R.id.deluxeCab:
                CSPreferences.putString(activity, "car_type", "2");
                deluxeCab.setImageResource(R.drawable.ic_icon_deluxe_car_selected);
                anyCab.setImageResource(R.drawable.ic_icon_any_car);
                regularCab.setImageResource(R.drawable.ic_icon_regular_car);
                break;
        }
    }

    public void initNavigationDrawer() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        String profile_pic = CSPreferences.readString(this, "profile_pic");
        View header = navigationView.getHeaderView(0);

        TextView customerName = (TextView)header.findViewById(R.id.customerName);
        CircleImageView customerImage = (CircleImageView)header.findViewById(R.id.nav_image);

        customerName.setText(CSPreferences.readString(activity, "user_name"));
        if (!profile_pic.startsWith("http")) {
            profile_pic = Config.user_pic_url+profile_pic;
        }

        Picasso.with(this)
                .load(profile_pic)
                .placeholder(R.drawable.ic__contact_picture_placeholder)
                .into(customerImage);

        Log.e(TAG, "profile_pic url-- "+profile_pic);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
            }

            @Override
            public void onDrawerClosed(View v) {
                super.onDrawerClosed(v);
            }

            @Override
            public void onDrawerOpened(View v) {
                super.onDrawerOpened(v);
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation == null)
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        double latitude = 0, longitude = 0;

        if (!CSPreferences.readString(activity, "source_latitude").isEmpty() ||
                !CSPreferences.readString(activity, "source_longitude").isEmpty()) {
             latitude = Double.parseDouble(CSPreferences.readString(activity, "source_latitude"));
             longitude = Double.parseDouble(CSPreferences.readString(activity, "source_latitude"));
        }

        car_type = CSPreferences.readString(activity, "car_type");

        if (!CSPreferences.readString(activity, "pickup_address").isEmpty()) {
            pickupAddress.setText(CSPreferences.readString(activity, "pickup_address"));
            dropSearch.setVisibility(View.VISIBLE);
            if (googleMap != null) {
                double lat= Double.parseDouble(CSPreferences.readString(activity, "source_latitude"));
                double lng= Double.parseDouble(CSPreferences.readString(activity, "source_longitude"));
                CameraPosition position = CameraPosition.builder()
                        .target( new LatLng(lat, lng) )
                        .zoom( 16f )
                        .bearing( 0.0f )
                        .tilt( 0.0f )
                        .build();
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(position));

                ModelManager.getInstance().getNearbyDriversManager().getNearbyDrivers(this,
                        Operations.nearbyDriversTask(this, latitude, longitude, car_type));
            }
        }

        if (CSPreferences.readString(activity, "drop_address").isEmpty()) {
            dropAddress.setText(R.string.destination);
        }
        else {
            //dropAddress.setText(CSPreferences.readString(activity, "drop_address"));
            dropAddress.setText(R.string.destination);
        }
    }


    /*public void startService() {


        serviceIntent = new Intent(activity, LocationService.class);
        startService(serviceIntent);
    }

    public void stopService() {


        serviceIntent = new Intent(activity, LocationService.class);
        stopService(serviceIntent);
    }*/

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        EventBus.getDefault().unregister(this);

        if (mGoogleApiClient!=null && mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();
        }
    }

    @Subscribe
    public void onEvent(Event event) {
        switch (event.getKey()) {

            case Constants.LOCATION_SUCCESS:
                getLocation(event.getLatitude(), event.getLongitude());
                break;

            case Constants.DRIVER_NEARBY_SUCCESS:
                for (int i=0; i< NearbyDriversManager.latitudeList.size(); i++) {
                    googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(NearbyDriversManager.latitudeList.get(i), NearbyDriversManager.longitudeList.get(i))))
                            .setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_icon_car));
                }
                break;
        }
    }

    private void initCamera(Location mLocation) {

        /* LocationService locationService = new LocationService();
            mLocation = locationService.getLocation(activity); */

        Log.e(TAG, "current location before--- "+ mLocation);

        if (mLocation == null) {
            mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            lat = 30.708444;
            lng = 76.692290;
        } else {
            lat = mLocation.getLatitude();
            lng = mLocation.getLongitude();
        }

        ModelManager.getInstance().getNearbyDriversManager().getNearbyDrivers(this,
                Operations.nearbyDriversTask(this, lat, lng, CSPreferences.readString(this, "car_type")));

        Log.e(TAG, "current location after--- "+ mLocation);

        CameraPosition position = CameraPosition.builder()
                .target(new LatLng(lat,lng))
                .zoom(16f)
                .bearing(0.0f)
                .tilt(0.0f)
                .build();


        googleMap.getUiSettings().setMyLocationButtonEnabled(false);

        latLng = new LatLng(lat, lng);

        zoomLevel = position.zoom;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(position));


        googleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                currentLocationButton.setVisibility(View.VISIBLE);

                //     stopService();

                latLng = googleMap.getCameraPosition().target;
                zoomLevel = googleMap.getCameraPosition().zoom;

                timeLayout.setVisibility(View.GONE);
            }
        });

        googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                zoomLevel = googleMap.getCameraPosition().zoom;

                latLng = googleMap.getCameraPosition().target;

                ModelManager.getInstance().getNearbyDriversManager().getNearbyDrivers(activity,
                        Operations.nearbyDriversTask(activity, latLng.latitude, latLng.longitude, car_type));

                CSPreferences.putString(activity, "source_latitude", String.valueOf(latLng.latitude));
                CSPreferences.putString(activity, "source_longitude", String.valueOf(latLng.longitude));

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        timeLayout.setVisibility(View.VISIBLE);
                    }
                }, 2000);

                Log.e(TAG, "lng--"+latLng.longitude);
                String address = Utils.getCompleteAddressString(activity, latLng.latitude, latLng.longitude);
                pickupAddress.setText(address);
                CSPreferences.putString(activity, "pickup_address", address);

            }
        });

        googleMap.getUiSettings().setRotateGesturesEnabled(false);

        gestureDetector = new ScaleGestureDetector(MainActivity.this, new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                if (lastSpan == -1) {
                    lastSpan = detector.getCurrentSpan();
                } else if (detector.getEventTime() - lastZoomTime >= 50) {
                    lastZoomTime = detector.getEventTime();
                    googleMap.animateCamera(CameraUpdateFactory.zoomBy(getZoomValue(detector.getCurrentSpan(), lastSpan)), 50, null);
                    lastSpan = detector.getCurrentSpan();
                }
                return false;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                lastSpan = -1;
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                lastSpan = -1;

            }
        });
    }

    private float getZoomValue(float currentSpan, float lastSpan) {
        double value = (Math.log(currentSpan / lastSpan) / Math.log(1.55d));
        return (float) value;
    }


    private void enableScrolling() {
        if (googleMap != null && !googleMap.getUiSettings().isScrollGesturesEnabled()) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    googleMap.getUiSettings().setAllGesturesEnabled(true);
                }
            }, 50);
        }
    }

    private void disableScrolling() {
        handler.removeCallbacksAndMessages(null);
        if (googleMap != null && googleMap.getUiSettings().isScrollGesturesEnabled()) {
            googleMap.getUiSettings().setAllGesturesEnabled(false);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        switch (ev.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_POINTER_DOWN:
                fingers = fingers + 1;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                fingers = fingers - 1;
                break;
            case MotionEvent.ACTION_UP:
                startTime = System.currentTimeMillis();
                fingers = 0;
                break;
            case MotionEvent.ACTION_DOWN:
                fingers = 1;
                if(System.currentTimeMillis() - startTime <= MAX_DURATION) {
                    Log.e(TAG, "Double tapped");
                    zoomLevel++;
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,
                            zoomLevel));
                    googleMap.getUiSettings().setZoomGesturesEnabled(false);

                }

                break;
        }

        if (fingers > 1) {
            disableScrolling();
        } else if (fingers < 1) {
            enableScrolling();

        }
        if (fingers > 1) {
            return gestureDetector.onTouchEvent(ev);
        } else {
            return super.dispatchTouchEvent(ev);
        }
    }

   /* public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    } */

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                SupportMapFragment mapFragment =
                        (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                mapFragment.getMapAsync(this);
            } else {
                SupportMapFragment mapFragment =
                        (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                mapFragment.getMapAsync(this);
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        try {

            boolean success = this.googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));
            if (!success) {
                Log.e("sorry try again", "Style parsing failed.");
            }
        }catch (Resources.NotFoundException e){
            e.printStackTrace();
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        initCamera(mLastLocation);

        this.googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        this.googleMap.setMyLocationEnabled(true);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        switch (item.getItemId()) {

            case R.id.logout:
                Intent i = new Intent(activity, CabCompaniesActivity.class);
                startActivity(i);
                finish();
                CSPreferences.clearPref(activity);
                CSPreferences.putString(activity, "login_status", "false");
                break;

            case R.id.settings:
                Intent settingsIntent = new Intent(activity, ProfileActivity.class);
                startActivity(settingsIntent);
                break;

            case R.id.schedule_ride:
                Intent scheduledIntent = new Intent(activity, ScheduledHistoryActivity.class);
                startActivity(scheduledIntent);
                break;

            case R.id.help:
                Intent helpIntent = new Intent(activity, HelpActivity.class);
                startActivity(helpIntent);
                break;

            case R.id.history:
                Intent historyIntent = new Intent(activity, TripsHistoryActivity.class);
                startActivity(historyIntent);
                break;

            case R.id.payment:
                Intent paymentIntent = new Intent(activity, PaymentActivity.class);
                startActivity(paymentIntent);
                break;
        }
        return true;
    }

    public void getLocation(double latitude, double longitude) {

        CameraPosition position = CameraPosition.builder()
                .target(new LatLng(latitude, longitude))
                .zoom(zoomLevel)
                .bearing(0.0f)
                .tilt(0.0f)
                .build();

        if (googleMap != null)
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(position));

    }


    @Override
    public void onLocationChanged(Location location) {
        Log.e(TAG, "Current lat: "+location.getLatitude());
        mLastLocation = location;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
