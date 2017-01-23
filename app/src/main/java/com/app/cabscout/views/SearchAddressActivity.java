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
  //  String response;
    ArrayList<String> placeIdList;
    ArrayList<SearchAddressBeans> addressList;
    SearchAddressAdapter searchAddressAdapter;
   // String address, area;
    String str_address;

    /* private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    public static String API_KEY = "AIzaSyAsE0edaQKl5wgqcfTibDmdUuHQgFEoldc"; */

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

                if (str_address.equals("Pickup")) {
                    PlaceParser placeParser = new PlaceParser();
                    placeParser.getAddress(activity, placeIdList.get(position), "pickup");

                    Log.e(TAG, "pickup-- "+searchAddressBeans.getAddress());

                    CSPreferences.putString(activity, "pickup_address", searchAddressBeans.getAddress());
                    finish();
                }
                else {
                    Intent i = new Intent(activity, BookRideActivity.class);
                    startActivity(i);
                    finish();

                    PlaceParser placeParser = new PlaceParser();
                    placeParser.getAddress(activity, placeIdList.get(position), "drop");

                    CSPreferences.putString(activity, "drop_address", searchAddressBeans.getAddress());

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
                recyclerView.setAdapter(null);
                ModelManager.getInstance().getSearchAddressManager().getAddress(activity, charSequence.toString());
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
                placeIdList = SearchAddressManager.placeIdList;
                addressList = SearchAddressManager.addressList;

                searchAddressAdapter = new SearchAddressAdapter(activity, addressList);
                recyclerView.setAdapter(searchAddressAdapter);
                break;
        }
    }

    /*class ExecuteTask extends AsyncTask<String, String, String> {

        StringBuilder sb;

        @Override
        protected String doInBackground(String... strings) {
            HttpHandler httpHandler = new HttpHandler();

            try {
                sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
                sb.append("?key=").append(API_KEY);
                sb.append("&input=").append(URLEncoder.encode(strings[0], "utf8"));

                Log.e(TAG, "URL-- "+sb.toString());

                response = httpHandler.makeServiceCall(sb.toString());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e(TAG, "response--"+s);


            try {

                JSONObject jsonObj = new JSONObject(s);
                JSONArray jsonArray = jsonObj.getJSONArray("predictions");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject  jsonObject = jsonArray.getJSONObject(i);

                    JSONArray array = jsonObject.getJSONArray("terms");

                    area = "";
                    StringBuilder s3 = new StringBuilder();
                    for (int j=0; j< array.length()-1; j++) {
                        JSONObject terms = array.getJSONObject(j);

                        if (j==0) {
                            address = terms.getString("value");
                        }
                        else {

                            s3.append(terms.getString("value")).append(", ");
                            area = s3.toString().substring(0,s3.toString().length()-2);
                        }
                    }

                    SearchAddressBeans searchAddressBeans = new SearchAddressBeans(address, area);

                    addressList.add(searchAddressBeans);

                    searchAddressAdapter.notifyDataSetChanged();

                    String place_id = jsonObject.getString("place_id");

                    placeIdList.add(place_id);

                }

            } catch (JSONException e) {
                Log.e("", "Cannot process JSON results", e);

            }
        }
    }*/

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
