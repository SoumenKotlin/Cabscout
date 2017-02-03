package com.app.cabscout.views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.app.cabscout.R;
import com.app.cabscout.controller.ModelManager;
import com.app.cabscout.controller.PlaceParser;
import com.app.cabscout.controller.SearchAddressManager;
import com.app.cabscout.model.Beans.SearchAddressBeans;
import com.app.cabscout.model.CSPreferences;
import com.app.cabscout.model.Constants;
import com.app.cabscout.model.Event;
import com.app.cabscout.model.custom.RecyclerTouchListener;
import com.app.cabscout.views.adapters.SearchAddressAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

public class SearchAddressActivity extends AppCompatActivity implements View.OnClickListener{

   private final String TAG = SearchAddressActivity.class.getSimpleName();
    Toolbar toolbar;

    EditText edit_query;
    ImageView clearText;
    RecyclerView recyclerView;
    Activity activity = this;
    ArrayList<String> placeIdList;
    ArrayList<SearchAddressBeans> addressList;
    SearchAddressAdapter searchAddressAdapter;
   // String address, area;
    String str_address;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_address);

        initViews();
    }

    public void initViews() {
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("Search Address");
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        str_address = getIntent().getStringExtra("Address");

        edit_query = (EditText) findViewById(R.id.edit_query);
        clearText = (ImageView) findViewById(R.id.clearText);
        clearText.setOnClickListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(activity, recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                SearchAddressBeans searchAddressBeans = addressList.get(position);

                switch (str_address) {
                    case "Pickup": {
                        PlaceParser placeParser = new PlaceParser();
                        placeParser.getAddress(activity, placeIdList.get(position), "pickup");

                        Log.e(TAG, "pickup-- " + searchAddressBeans.getAddress());

                        CSPreferences.putString(activity, "pickup_address", searchAddressBeans.getAddress());
                        //finish();
                        break;
                    }
                    case "Destination_Book": {
                        PlaceParser placeParser = new PlaceParser();
                        placeParser.getAddress(activity, placeIdList.get(position), "drop_book");

                        CSPreferences.putString(activity, "drop_address", searchAddressBeans.getAddress());
                        break;
                    }
                    case "Destination": {
                        PlaceParser placeParser = new PlaceParser();
                        placeParser.getAddress(activity, placeIdList.get(position), "drop");

                        CSPreferences.putString(activity, "drop_address", searchAddressBeans.getAddress());

                        break;
                    }

                    case "Add Home": {
                        PlaceParser placeParser = new PlaceParser();
                        placeParser.getAddress(activity, placeIdList.get(position), "home");

                        CSPreferences.putString(activity, "add_home", searchAddressBeans.getAddress());
                        break;
                    }

                    case "Add Work": {
                        PlaceParser placeParser = new PlaceParser();
                        placeParser.getAddress(activity, placeIdList.get(position), "work");

                        CSPreferences.putString(activity, "add_work", searchAddressBeans.getAddress());
                        break;
                    }
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        edit_query.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ModelManager.getInstance().getSearchAddressManager().getAddress(activity, charSequence.toString());
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.clearText:
                edit_query.setText("");
                break;
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEvent(Event event) {
        switch (event.getKey()) {
            case Constants.ADDRESS_SUCCESS:
                progressBar.setVisibility(View.INVISIBLE);
                placeIdList = SearchAddressManager.placeIdList;
                addressList = SearchAddressManager.addressList;

                searchAddressAdapter = new SearchAddressAdapter(activity, addressList);
                recyclerView.setAdapter(searchAddressAdapter);
                break;

            case Constants.SOURCE_SUCCESS:
                finish();
                break;

            case Constants.DESTINATION_SUCCESS:
                Intent i = new Intent(activity, BookRideActivity.class);
                startActivity(i);
                finish();
                break;

            case Constants.DESTINATION_RIDE_SUCCESS:
                finish();
                break;

            case Constants.ADD_HOME_SUCCESS:
                finish();
                break;

            case Constants.ADD_WORK_SUCCESS:
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
}
