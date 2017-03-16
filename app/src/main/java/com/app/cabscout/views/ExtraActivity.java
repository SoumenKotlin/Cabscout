package com.app.cabscout.views;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.cabscout.R;
import com.app.cabscout.controller.TripsHistoryManager;
import com.app.cabscout.model.Beans.TripsHistoryBeans;
import com.app.cabscout.model.Constants;
import com.app.cabscout.model.Event;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

@SuppressLint("NewApi")
public class ExtraActivity extends AppCompatActivity {

    TextView driverName, tripDate, paymentType, tripPrice, pickupLocation, dropLocation;
    CircleImageView imageView;
    Toolbar toolbar;

    LinearLayout linearLayout;
    View itemView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.practice);


   //     linearLayout = (LinearLayout)findViewById(R.id.layout1);

        ImageView animationTarget = (ImageView) this.findViewById(R.id.testImage);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.cs__infinite_rotation);
        animationTarget.startAnimation(animation);


    }

    @Override
    protected void onResume() {
        super.onResume();

        /* ModelManager.getInstance().getTripsHistoryManager().getTripsHistory(this, Operations.getTripsHistory(
                this, CSPreferences.readString(this, "customer_id")))*/;
    }

    @Override
    protected void onStart() {
        super.onStart();

        // EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

      //  EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEvent(Event event) {

        switch (event.getKey()) {

            case Constants.TRIPS_HISTORY_SUCCESS:
                ArrayList<TripsHistoryBeans> list = TripsHistoryManager.tripsList;

               /* for (int i=0; i<list.size(); i++) {
                    TripsHistoryBeans tripsHistoryBeans = list.get(i);
                    driverName.setText(tripsHistoryBeans.getDriver_name());
                    tripDate.setText(tripsHistoryBeans.getDatetime());
                    paymentType.setText("Cash");
                    tripPrice.setText(tripsHistoryBeans.getPrice());
                    pickupLocation.setText(tripsHistoryBeans.getPickup_location());
                    dropLocation.setText(tripsHistoryBeans.getDrop_location());

                    Picasso.with(this)
                            .load(tripsHistoryBeans.getProfile_pic())
                            .placeholder(R.color.teal_A700)
                            .into(imageView);
                } */

                for (int i=0; i<10; i++) {
                    TripsHistoryBeans tripsHistoryBeans = list.get(0);
                    itemView = LayoutInflater.from(this).inflate(R.layout.view_practice, null);
                    LinearLayout linearLayout1 = (LinearLayout)itemView.findViewById(R.id.linearLayout);

                    driverName = (TextView)itemView.findViewById(R.id.driverName);
                    tripDate = (TextView)itemView.findViewById(R.id.tripDate);
                    paymentType = (TextView)itemView.findViewById(R.id.paymentType);
                    tripPrice = (TextView)itemView.findViewById(R.id.price);
                    pickupLocation = (TextView)itemView.findViewById(R.id.pickupLocation);
                    dropLocation = (TextView)itemView.findViewById(R.id.dropLocation);

                    imageView = (CircleImageView)itemView.findViewById(R.id.driverImage);

                    driverName.setText(tripsHistoryBeans.getDriver_name()+i);
                    tripDate.setText(tripsHistoryBeans.getDatetime());
                    paymentType.setText("Cash");
                    tripPrice.setText(tripsHistoryBeans.getPrice());
                    pickupLocation.setText(tripsHistoryBeans.getPickup_location());
                    dropLocation.setText(tripsHistoryBeans.getDrop_location());

                    Picasso.with(this)
                            .load(tripsHistoryBeans.getProfile_pic())
                            .placeholder(R.color.teal_A700)
                            .into(imageView);

                    linearLayout.addView(itemView);

                    linearLayout1.setOnClickListener(new OnMyClickListener(tripsHistoryBeans.getDriver_name()+i));

                }

                break;

            case Constants.TRIPS_HISTORY_EMPTY:

                break;
        }
    }

    public class OnMyClickListener implements View.OnClickListener{

        String name;

        public OnMyClickListener(String name) {
                this.name = name;
        }
        @Override
        public void onClick(View v) {
            Toast.makeText(ExtraActivity.this, ""+name, Toast.LENGTH_SHORT).show();
        }
    }
}
