package com.app.cabscout.views.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.cabscout.R;
import com.app.cabscout.model.Beans.SearchAddressBeans;

import java.util.ArrayList;

/*
 * Created by rishav on 19/1/17.
 */

public class SearchAddressAdapter extends RecyclerView.Adapter<SearchAddressAdapter.ViewHolder> {

    private Context context;
    private ArrayList<SearchAddressBeans> list;

    public SearchAddressAdapter(Context context, ArrayList<SearchAddressBeans> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public SearchAddressAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_places_search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchAddressAdapter.ViewHolder holder, int position) {
        SearchAddressBeans searchAddressBeans = list.get(position);


        holder.textAddress.setText(searchAddressBeans.getAddress());
        holder.textArea.setText(searchAddressBeans.getArea());

        /*list.set(position, searchAddressBeans);
        notifyItemInserted(position);
        notifyItemRangeChanged(position, list.size());*/
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

     class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textAddress, textArea;

         ViewHolder(View itemView) {
            super(itemView);

            textAddress = (TextView)itemView.findViewById(R.id.textAddress);
            textArea = (TextView)itemView.findViewById(R.id.textArea);
        }
    }
}
