package com.app.cabscout.views.adapters;

/*
 * Created by rishav on 2/3/17.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.app.cabscout.R;

import java.util.ArrayList;

@SuppressLint("NewApi")
public class CancelTripAdapter extends RecyclerView.Adapter<CancelTripAdapter.ViewHolder>{

    private Context context;
    private ArrayList<String> list;

    public CancelTripAdapter(Context context, ArrayList<String> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public CancelTripAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_cancel_ride_reasons, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CancelTripAdapter.ViewHolder holder, int position) {
        String reasons = list.get(position);

       /* RadioButton radioButton = new RadioButton(context);
        radioButton.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        radioButton.setText(reasons);*/

       /* holder.radioGroup.addView(radioButton);*/

        holder.radioButton.setText(reasons);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private RadioGroup radioGroup;
        private RadioButton radioButton;


        public ViewHolder(View itemView) {
            super(itemView);

            radioGroup = (RadioGroup)itemView.findViewById(R.id.radioGroup);

            radioButton = (RadioButton)itemView.findViewById(R.id.radioButton);

        }
    }
}
