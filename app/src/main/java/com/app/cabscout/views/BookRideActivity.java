package com.app.cabscout.views;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.cabscout.R;
import com.app.cabscout.model.CSPreferences;
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

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class BookRideActivity extends AppCompatActivity implements OnMapReadyCallback, RoutingListener, View.OnClickListener {

    private static final String TAG = BookRideActivity.class.getSimpleName();
    Activity activity = this;
    Toolbar toolbar;
    // RelativeLayout cabSelectionLayout;
    TextView pickupAddress, destinationAddress;
    String src_lat, src_lng, dest_lat, dest_lng;
    private GoogleMap gMap;
    CardView pickupSearch, dropSearch;
    private TextView distance;

    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.colorAccent,R.color.colorAccent,
            R.color.colorAccent,R.color.colorAccent,R.color.colorAccent};

    protected LatLng start,end;

    private Dialog dialog, promoDialog;
    LinearLayout promoCodeLayout, paymentLayout;
    TextView promoCode, applyCode, cancel, paymentMethod;
    EditText editPromoCode;
    String promo_code;
    BottomSheetDialog bottomPromoCode, bottomPaymentMethod;
    TextView cash, creditCard, corpAccount;
    TextView txtBookRide;
    private double midLat, midLng;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_ride);
    }

    public void initViews() {
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("Book Ride");
        setSupportActionBar(toolbar);

        polylines = new ArrayList<>();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

       // View v = findViewById(R.id.cabBookLayout);
       // cabSelectionLayout = (RelativeLayout)v.findViewById(R.id.cabBookLayout);
        dialog = Utils.customProgressDialog(this, "Retrieving Path...");
        dialog.show();

        pickupAddress = (TextView)findViewById(R.id.pickupAddress);
        destinationAddress = (TextView)findViewById(R.id.dropAddress);
        distance = (TextView) findViewById(R.id.distance);

        pickupSearch = (CardView)findViewById(R.id.pickUpSearch);
        dropSearch = (CardView)findViewById(R.id.dropSearch);
        pickupSearch.setOnClickListener(this);
        dropSearch.setOnClickListener(this);

       // promoDialog = Utils.makeDialog(this, R.layout.bottom_promo_code);

        pickupAddress.setText(CSPreferences.readString(activity, "pickup_address"));
        destinationAddress.setText(CSPreferences.readString(activity, "drop_address"));

        src_lat = CSPreferences.readString(activity, "source_latitude");
        src_lng = CSPreferences.readString(activity, "source_longitude");
        dest_lat = CSPreferences.readString(activity, "destination_latitude");
        dest_lng = CSPreferences.readString(activity, "destination_longitude");

        txtBookRide = (TextView)findViewById(R.id.txtBookRide);
        txtBookRide.setOnClickListener(this);

        bottomPromoCodeSheet();
        bottomPaymentMethodSheet();

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void bottomPromoCodeSheet() {
        promoCodeLayout = (LinearLayout)findViewById(R.id.promoCodeLayout);
        promoCode = (TextView)findViewById(R.id.promoCode);
        promoCodeLayout.setOnClickListener(this);

        bottomPromoCode = Utils.createBottomSheetDialog(this, R.layout.bottom_promo_code);

        applyCode = (TextView)bottomPromoCode.findViewById(R.id.applyCode);
        cancel = (TextView)bottomPromoCode.findViewById(R.id.cancel);
        editPromoCode = (EditText)bottomPromoCode.findViewById(R.id.editPromoCode);

        applyCode.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    public void bottomPaymentMethodSheet() {
        paymentLayout = (LinearLayout)findViewById(R.id.paymentLayout);
        paymentMethod = (TextView)findViewById(R.id.paymentMethod);
        bottomPaymentMethod = Utils.createBottomSheetDialog(this, R.layout.bottom_payment_type);

        cash = (TextView) bottomPaymentMethod.findViewById(R.id.cash);
        creditCard = (TextView)bottomPaymentMethod.findViewById(R.id.creditCard);
        corpAccount = (TextView)bottomPaymentMethod.findViewById(R.id.corpAccount);

        paymentLayout.setOnClickListener(this);
        cash.setOnClickListener(this);
        creditCard.setOnClickListener(this);
        corpAccount.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();

        initViews();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.pickUpSearch:
                Intent i = new Intent(activity, SearchAddressActivity.class);
                i.putExtra("Address", "Pickup");
                startActivity(i);
                break;

            case R.id.dropSearch:
                Intent i2 = new Intent(activity, SearchAddressActivity.class);
                i2.putExtra("Address", "Destination_Book");
                startActivity(i2);
                break;

            case R.id.promoCodeLayout:
                //  promoDialog.show();
                bottomPromoCode.show();
                break;

            case R.id.applyCode:
                promo_code = editPromoCode.getText().toString().trim();

                if (promo_code.isEmpty())
                    Toast.makeText(activity, "Please enter the promo code", Toast.LENGTH_SHORT).show();
                else {
                    // promoDialog.dismiss();
                    bottomPromoCode.dismiss();
                    promoCode.setText(promo_code);
                }
                    break;

            case R.id.cancel:
                // promoDialog.dismiss();
                bottomPromoCode.dismiss();
                break;

            case R.id.paymentLayout:
                bottomPaymentMethod.show();
                break;

            case R.id.cash:
                bottomPaymentMethod.dismiss();
                paymentMethod.setText("Cash");
                break;

            case R.id.creditCard:
                bottomPaymentMethod.dismiss();
                paymentMethod.setText("Credit Card");
                break;

            case R.id.corpAccount:
                bottomPaymentMethod.dismiss();
                paymentMethod.setText("Corp. Account");
                break;

            case R.id.txtBookRide:
                Intent intent = new Intent(activity, CabArrivingActivity.class);
                intent.putExtra("driver_lat", midLat);
                intent.putExtra("driver_lng", midLng);
                intent.putExtra("customer_lat", src_lat);
                intent.putExtra("customer_lng", src_lng);
                startActivity(intent);
                finish();
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

        gMap.clear();

        start = new LatLng(Double.parseDouble(src_lat), Double.parseDouble(src_lng));
        end = new LatLng(Double.parseDouble(dest_lat), Double.parseDouble(dest_lng));

        Routing routing = new Routing.Builder()
                .travelMode(Routing.TravelMode.DRIVING)
                .withListener(this)
                .waypoints(start,  end)
                .build();
        routing.execute();
    }

    @Override
    public void onRoutingFailure(RouteException e) {
        dialog.dismiss();
        Toast.makeText(activity, "Invalid Request.", Toast.LENGTH_SHORT).show();
        Log.e(TAG, ""+e.getMessage());
        e.printStackTrace();
    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int j) {
         midLat = (Double.parseDouble(src_lat)+Double.parseDouble(dest_lat))/2;
         midLng = (Double.parseDouble(src_lng)+Double.parseDouble(dest_lng))/2;

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.20);

        dialog.dismiss();
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(new LatLng(Double.parseDouble(src_lat), Double.parseDouble(src_lng)));
        builder.include(new LatLng(Double.parseDouble(dest_lat), Double.parseDouble(dest_lng)));
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

            distance.setText(String.format("%s mi", String.valueOf(dt)));
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
    public void onRoutingCancelled() {
        dialog.dismiss();
    }
}
