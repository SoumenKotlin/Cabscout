package com.app.cabscout.views;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.cabscout.R;
import com.app.cabscout.model.CSPreferences;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class BookRideActivity extends AppCompatActivity implements OnMapReadyCallback, RoutingListener {

    private static final String TAG = BookRideActivity.class.getSimpleName();
    Activity activity = this;
    Toolbar toolbar;
    RelativeLayout cabSelectionLayout;
    TextView pickupAddress, destinationAddress;
    String src_lat, src_lng, dest_lat, dest_lng;
    private GoogleMap gMap;

    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.colorAccent,R.color.colorAccent,R.color.colorAccent,R.color.colorAccent,R.color.colorAccent};

    protected LatLng start;
    protected LatLng end;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_ride);

        initViews();
    }

    public void initViews() {
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("Book Ride");
        setSupportActionBar(toolbar);
        polylines = new ArrayList<>();


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        View v = findViewById(R.id.cabBookLayout);
        cabSelectionLayout = (RelativeLayout)v.findViewById(R.id.cabBookLayout);

        pickupAddress = (TextView)cabSelectionLayout.findViewById(R.id.pickupAddress);
        destinationAddress = (TextView)cabSelectionLayout.findViewById(R.id.destinationAddress);

        pickupAddress.setText(CSPreferences.readString(activity, "pickup_address"));
        destinationAddress.setText(CSPreferences.readString(activity, "drop_address"));

        src_lat = CSPreferences.readString(activity, "source_latitude");
        src_lng = CSPreferences.readString(activity, "source_longitude");
        dest_lat = CSPreferences.readString(activity, "destination_latitude");
        dest_lng = CSPreferences.readString(activity, "destination_longitude");

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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

    @Override
    public void onMapReady(GoogleMap googleMap) {

        gMap = googleMap;

       /* CameraPosition position = CameraPosition.builder()
                .target( new LatLng( Double.parseDouble(src_lat),
                        Double.parseDouble(src_lng) ) )
                .zoom( 16f )
                .bearing( 0.0f )
                .tilt( 0.0f )
                .build();*/

        start = new LatLng(Double.parseDouble(src_lat), Double.parseDouble(src_lng));
        end = new LatLng(Double.parseDouble(dest_lat), Double.parseDouble(dest_lng));

       // ModelManager.getInstance().getLocationDirectionManager().getAddress(activity, source, destination, gMap);

        Routing routing = new Routing.Builder()
                .travelMode(Routing.TravelMode.DRIVING)
                .withListener(this)
                .waypoints(start,  end)
                .build();
        routing.execute();

        //gMap.moveCamera(CameraUpdateFactory.newCameraPosition(position));
    }

    @Override
    public void onRoutingFailure(RouteException e) {
        Log.e(TAG, ""+e.getMessage());
        e.printStackTrace();
    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int j) {
        double midLat = (Double.parseDouble(src_lat)+Double.parseDouble(dest_lat))/2;
        double midLng = (Double.parseDouble(src_lng)+Double.parseDouble(dest_lng))/2;
     //   CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(midLat, midLng));
     //   CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);

        /*CameraPosition position = CameraPosition.builder()
                .target( new LatLng(midLat, midLng) )
                .zoom( 16f )
                .bearing( 0.0f )
                .tilt( 0.0f )
                .build();*/

        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(midLat, midLng), gMap.getCameraPosition().zoom));


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
            Toast.makeText(getApplicationContext(),"Route "+ (i+1) +": distance - "+ route.get(i).getDistanceValue()+": duration - "+ route.get(i).getDurationValue(),Toast.LENGTH_SHORT).show();
        }

        // Start marker
        MarkerOptions options = new MarkerOptions();
        options.position(start);
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
        gMap.addMarker(options);

        // End marker
        options = new MarkerOptions();
        options.position(end);
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
        gMap.addMarker(options);
    }

    @Override
    public void onRoutingCancelled() {

    }
}
