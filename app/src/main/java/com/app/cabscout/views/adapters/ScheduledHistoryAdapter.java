package com.app.cabscout.views.adapters;

/*
 * Created by rishav on 25/1/17.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.cabscout.R;
import com.app.cabscout.model.Beans.ScheduleHistoryBeans;
import com.app.cabscout.model.Utils;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.util.ArrayList;

public class ScheduledHistoryAdapter extends RecyclerView.Adapter<ScheduledHistoryAdapter.ViewHolder> implements OnMapReadyCallback {
    private Context context;
    private ArrayList<ScheduleHistoryBeans> list;
    private String paymentMode;

    public ScheduledHistoryAdapter(Context context, ArrayList<ScheduleHistoryBeans> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public ScheduledHistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_scheduled_rides, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ScheduledHistoryAdapter.ViewHolder holder, int position) {
        ScheduleHistoryBeans historyBeans = list.get(position);

        holder.dateTime.setText(historyBeans.getDateTime());
        holder.carName.setText(historyBeans.getCar_name());

        holder.ridePrice.setText(historyBeans.getPrice());

        String pickup_address = Utils.getCompleteAddressString(context,
                Double.parseDouble(historyBeans.getPickup_lat()),
                Double.parseDouble(historyBeans.getPickup_lng()));

        String drop_address = Utils.getCompleteAddressString(context,
                Double.parseDouble(historyBeans.getDrop_lat()),
                Double.parseDouble(historyBeans.getDrop_lng()));

        holder.pickupAddress.setText(pickup_address);
        holder.dropAddress.setText(drop_address);


        switch (historyBeans.getPayment_type()) {
            case "0":
                paymentMode = "CASH";
                break;
            case "1":
                paymentMode = "CARD";
                break;
            case "2":
                paymentMode = "CORP. ACC.";
                break;
        }

        holder.paymentType.setText(paymentMode);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView dateTime, ridePrice, paymentType, carName, pickupAddress, dropAddress;

        public ViewHolder(View itemView) {
            super(itemView);

            dateTime = (TextView)itemView.findViewById(R.id.dateTime);
            ridePrice = (TextView)itemView.findViewById(R.id.ridePrice);
            paymentType = (TextView)itemView.findViewById(R.id.paymentType);
            carName = (TextView)itemView.findViewById(R.id.carName);

            pickupAddress = (TextView)itemView.findViewById(R.id.pickupAddress);
            dropAddress = (TextView)itemView.findViewById(R.id.dropAddress);
        }
    }
}
