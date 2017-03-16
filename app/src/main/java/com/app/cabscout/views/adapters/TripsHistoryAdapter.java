package com.app.cabscout.views.adapters;

/*
 * Created by rishav on 31/1/17.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.cabscout.R;
import com.app.cabscout.model.Beans.TripsHistoryBeans;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class TripsHistoryAdapter extends RecyclerView.Adapter<TripsHistoryAdapter.ViewHolder> {
    private Context context;
    private ArrayList<TripsHistoryBeans> list;

    public TripsHistoryAdapter(Context context, ArrayList<TripsHistoryBeans> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public TripsHistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_trips_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TripsHistoryAdapter.ViewHolder holder, int position) {
        TripsHistoryBeans tripsHistoryBeans = list.get(position);
        holder.driverName.setText(tripsHistoryBeans.getDriver_name());
        holder.tripDate.setText(tripsHistoryBeans.getDatetime());
        holder.paymentType.setText("Cash");
        holder.tripPrice.setText(tripsHistoryBeans.getPrice());
        holder.pickupLocation.setText(tripsHistoryBeans.getPickup_location());
        holder.dropLocation.setText(tripsHistoryBeans.getDrop_location());

         Picasso.with(context)
                 .load(tripsHistoryBeans.getProfile_pic())
                 .placeholder(R.color.teal_A700)
                 .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

     class ViewHolder extends RecyclerView.ViewHolder {
        private TextView driverName, tripDate, paymentType, tripPrice, pickupLocation, dropLocation;
        private CircleImageView imageView;

        private ViewHolder(View itemView) {
            super(itemView);

            driverName = (TextView)itemView.findViewById(R.id.driverName);
            tripDate = (TextView)itemView.findViewById(R.id.tripDate);
            paymentType = (TextView)itemView.findViewById(R.id.paymentType);
            tripPrice = (TextView)itemView.findViewById(R.id.price);
            pickupLocation = (TextView)itemView.findViewById(R.id.pickupLocation);
            dropLocation = (TextView)itemView.findViewById(R.id.dropLocation);

            imageView = (CircleImageView)itemView.findViewById(R.id.driverImage);
        }
    }
}
