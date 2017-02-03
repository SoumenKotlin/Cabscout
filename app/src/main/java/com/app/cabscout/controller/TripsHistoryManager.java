package com.app.cabscout.controller;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.app.cabscout.model.Beans.TripsHistoryBeans;
import com.app.cabscout.model.Constants;
import com.app.cabscout.model.Event;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/*
 * Created by rishav on 31/1/17.
 */

public class TripsHistoryManager {

    private static final String TAG = TripsHistoryManager.class.getSimpleName();
    public static ArrayList<TripsHistoryBeans> tripsList;

    public void getTripsHistory(Context context, String params) {
        new ExecuteApi(context).execute(params);
        tripsList = new ArrayList<>();
    }

    private class ExecuteApi extends AsyncTask<String, String, String> {
        Context mContext;

        ExecuteApi(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpHandler httpHandler = new HttpHandler();
            String response = httpHandler.makeServiceCall(strings[0]);
            Log.e(TAG, "trips_history response-- "+response);
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s != null) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray responseArray = jsonObject.getJSONArray("response");

                    if (responseArray.length() < 1) {
                        EventBus.getDefault().post(new Event(Constants.TRIPS_HISTORY_EMPTY, ""));
                        return;
                    }

                    for (int i=0; i<responseArray.length(); i++) {
                        JSONObject data = responseArray.getJSONObject(i);

                        String requestId = data.getString("ride_request_id");
                        String pickup_location = data.getString("pickup_location");
                        String drop_location = data.getString("drop_location");
                        String datetime = data.getString("datetime");
                        String price = data.getString("price");
                        String driver_name = data.getString("driver_name");
                        String profile_pic = data.getString("profile_pic");

                        TripsHistoryBeans tripsHistoryBeans = new TripsHistoryBeans(requestId, pickup_location, drop_location,
                                datetime, price, driver_name, profile_pic);
                        tripsList.add(tripsHistoryBeans);
                    }

                    EventBus.getDefault().post(new Event(Constants.TRIPS_HISTORY_SUCCESS, ""));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
