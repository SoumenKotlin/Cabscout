package com.app.cabscout.controller;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.app.cabscout.model.Beans.SearchAddressBeans;
import com.app.cabscout.model.Constants;
import com.app.cabscout.model.Event;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by rishav on 20/1/17.
 */

public class SearchAddressManager {

    private static final String TAG = SearchAddressManager.class.getSimpleName();
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static String API_KEY = "AIzaSyDW4hwt4oKL-B64uDuwZ3LwEsoBLEcHwgw";

    public static ArrayList<String> placeIdList;
    public static ArrayList<SearchAddressBeans> addressList;

    public void getAddress(Context context, String params) {
        new ExecuteTask(context).execute(params);

    }

    private class ExecuteTask extends AsyncTask<String, String, String> {
        Context mContext;
        StringBuilder sb;
        private String address, area;

         ExecuteTask(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpHandler httpHandler = new HttpHandler();
            String response="";
            placeIdList = new ArrayList<>();
            addressList = new ArrayList<>();

            try {
                sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
                sb.append("?key=").append(API_KEY);
                sb.append("&input=").append(URLEncoder.encode(strings[0], "utf8"));

                Log.e(TAG, "URL-- "+sb.toString());

                response = httpHandler.makeServiceCall(sb.toString());
                try {

                    JSONObject jsonObj = new JSONObject(response);
                    JSONArray jsonArray = jsonObj.getJSONArray("predictions");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        JSONArray array = jsonObject.getJSONArray("terms");

                        int length = array.length();
                        if (length >= 3) {
                            length = length - 1;
                        }

                        StringBuilder s3 = new StringBuilder();

                        for (int j = 0; j < length; j++) {
                            JSONObject terms = array.getJSONObject(j);

                            if (j == 0) {
                                address = terms.getString("value");
                            } else {

                                s3.append(terms.getString("value")).append(", ");
                                area = s3.toString().substring(0, s3.toString().length() - 2);
                            }
                        }

                        SearchAddressBeans searchAddressBeans = new SearchAddressBeans(address, area);

                        addressList.add(searchAddressBeans);

                        String place_id = jsonObject.getString("place_id");

                        placeIdList.add(place_id);
                    }

                } catch (JSONException e) {
                    Log.e("", "Cannot process JSON results", e);

                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e(TAG, "response--"+s);

            EventBus.getDefault().post(new Event(Constants.ADDRESS_SUCCESS, ""));

        }
    }
}
