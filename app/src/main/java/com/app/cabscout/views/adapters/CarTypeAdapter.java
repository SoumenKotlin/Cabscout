package com.app.cabscout.views.adapters;

/*
 * Created by rishav on 25/1/17.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.cabscout.R;

import java.util.ArrayList;

public class CarTypeAdapter extends RecyclerView.Adapter<CarTypeAdapter.ViewHolder>{

    private Context context;
    private ArrayList<String> list;

    public CarTypeAdapter(Context context, ArrayList<String> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public CarTypeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_car_type, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CarTypeAdapter.ViewHolder holder, int position) {
        String carType = list.get(position);
        switch (carType) {
            case "any":
                holder.carType.setText(R.string.any_car);
                holder.carimage.setImageResource(R.drawable.ic_icon_small_any);
                break;
            case "regular":
                holder.carType.setText(R.string.regular_car);
                holder.carimage.setImageResource(R.drawable.ic_icon_small_regular);
                break;
            case "deluxe":
                holder.carType.setText(R.string.deluxe_car);
                holder.carimage.setImageResource(R.drawable.ic_icon_small_deluxe);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

     class ViewHolder extends RecyclerView.ViewHolder{
         private TextView carType;
         private ImageView carimage;

        public ViewHolder(View itemView) {
            super(itemView);

            carType = (TextView)itemView.findViewById(R.id.carType);
            carimage = (ImageView)itemView.findViewById(R.id.carImage);
        }
    }
}
