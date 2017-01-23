package com.app.cabscout.controller;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.app.cabscout.model.CSPreferences;
import com.app.cabscout.model.Constants;
import com.app.cabscout.model.Event;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

/*
 * Created by rishav on 20/1/17.
 */

public class PlaceParser {

    private static final String TAG = PlaceParser.class.getSimpleName();
    private String input;

    public void getAddress(Context context, String params, String input) {
        this.input = input;
        new ExecuteAddress(context).execute(params);
    }

    private class ExecuteAddress extends AsyncTask<String, String, String> {

        Context mContext;

        ExecuteAddress(Context context) {
            mContext = context;
        }

        @Override
        protected String doInBackground(String... params) {
            String place_url = "https://maps.googleapis.com/maps/api/place/details/json?placeid=";

            HttpHandler httpHandler = new HttpHandler();
            String API_KEY = "AIzaSyAsE0edaQKl5wgqcfTibDmdUuHQgFEoldc";
            String json = httpHandler.makeServiceCall(place_url + params[0] + "&key=" + API_KEY);

            Log.w(TAG, "response-- " + json);
            return json;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {

                JSONObject jsonObj = new JSONObject(s);
                JSONObject jsonObject = jsonObj.getJSONObject("result");
                JSONObject jsonObject1 = jsonObject.getJSONObject("geometry");
                JSONObject jsonObject2 = jsonObject1.getJSONObject("location");
                String latitude = jsonObject2.getString("lat");
                String longitude = jsonObject2.getString("lng");

                CSPreferences.putString(mContext, "latitude", latitude);
                CSPreferences.putString(mContext, "longitude", longitude);

                Log.e(TAG, "searched latitude-- " + latitude);
                Log.e(TAG, "searched longitude-- " + longitude);

                switch (input) {
                    case "pickup":
                        CSPreferences.putString(mContext, "source_latitude", latitude);
                        CSPreferences.putString(mContext, "source_longitude", longitude);
                        EventBus.getDefault().post(new Event(Constants.SOURCE_SUCCESS, ""));

                        break;
                    case "drop_book":
                        CSPreferences.putString(mContext, "destination_latitude", latitude);
                        CSPreferences.putString(mContext, "destination_longitude", longitude);
                        EventBus.getDefault().post(new Event(Constants.DESTINATION_RIDE_SUCCESS, ""));

                        break;
                    case "drop":
                        CSPreferences.putString(mContext, "destination_latitude", latitude);
                        CSPreferences.putString(mContext, "destination_longitude", longitude);
                        EventBus.getDefault().post(new Event(Constants.DESTINATION_SUCCESS, ""));

                        break;
                }

            } catch (JSONException e) {

                Log.e("", "Cannot process JSON results", e);
            }
        }
    }
}