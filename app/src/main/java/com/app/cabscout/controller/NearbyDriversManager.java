package com.app.cabscout.controller;

/*
 * Created by rishav on 21/2/17.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.app.cabscout.model.Constants;
import com.app.cabscout.model.Event;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NearbyDriversManager {
    private final String TAG = NearbyDriversManager.class.getSimpleName();
    public static final ArrayList<Double> latitudeList = new ArrayList<>();
    public static final ArrayList<Double> longitudeList = new ArrayList<>();

    public void getNearbyDrivers(Context context, String params) {
        new ExecuteApi().execute(params);
    }

    private class ExecuteApi extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            HttpHandler httpHandler = new HttpHandler();
            String response = httpHandler.makeServiceCall(params[0]);
            Log.e(TAG, "nearby_drivers response-- "+response);
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s != null) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray jsonArray = jsonObject.getJSONArray("response");

                    for (int i=0; i<jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        int id = Integer.parseInt(object.getString("id"));

                        if (id < 0) {
                            EventBus.getDefault().post(new Event(Constants.DRIVER_NEARBY_EMPTY, ""));
                            return;
                        }

                        String latitude = object.getString("latitude");
                        String longitude = object.getString("longitude");

                        latitudeList.add(Double.parseDouble(latitude));
                        longitudeList.add(Double.parseDouble(longitude));
                    }

                    EventBus.getDefault().post(new Event(Constants.DRIVER_NEARBY_SUCCESS, ""));

                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            } else {
                EventBus.getDefault().post(new Event(Constants.SERVER_ERROR, ""));
            }
        }
    }
}
