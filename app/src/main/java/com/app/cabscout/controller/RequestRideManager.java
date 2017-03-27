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
 * Created by rishav on 24/1/17.
 */

public class RequestRideManager {

    private final String TAG = RequestRideManager.class.getSimpleName();

    public void requestRide(Context context, String params) {
        new ExecuteApi(context).execute(params);
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

            Log.e(TAG, "request_ride response-- "+response);
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s != null) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONObject responseObject = jsonObject.getJSONObject("response");
                    String id = responseObject.getString("id");

                    if (id.equals("1")) {
                        EventBus.getDefault().post(new Event(Constants.SCHEDULE_RIDE_SUCCESS, ""));
                    }
                    else {
                        EventBus.getDefault().post(new Event(Constants.REQUEST_RIDE_FAILED, ""));
                    }

                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            } else {
                EventBus.getDefault().post(new Event(Constants.SERVER_ERROR, ""));
            }
        }
    }

    public void acceptedRequest(Context context, String params) {
        new ExecuteApiAcceptedRequest(context).execute(params);
    }

    private class ExecuteApiAcceptedRequest extends AsyncTask<String, String, String> {

        Context mContext;

        public ExecuteApiAcceptedRequest(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        protected String doInBackground(String... params) {
            HttpHandler httpHandler = new HttpHandler();

            String response = httpHandler.makeServiceCall(params[0]);

            Log.e(TAG, "accepted_ride response-- "+response);
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONObject response = jsonObject.getJSONObject("response");

                    String driver_coordinates = response.getString("driver_cordinates");
                    String ride_request_id = response.getString("ride_request_id");

                    String driver_name = response.getString("name");
                    String profile_pic = response.getString("profile_pic");
                    String mobile = response.getString("mobile");

                    CSPreferences.putString(mContext, "driver_coordinates", driver_coordinates);
                    CSPreferences.putString(mContext, "driver_name", driver_name);
                    CSPreferences.putString(mContext, "driver_profile_pic", profile_pic);
                    CSPreferences.putString(mContext, "driver_mobile", mobile);
                    CSPreferences.putString(mContext, "ride_request_id", ride_request_id);

                    EventBus.getDefault().post(new Event(Constants.REQUEST_RIDE_SUCCESS, ""));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                EventBus.getDefault().post(new Event(Constants.SERVER_ERROR, ""));
            }
        }
    }
}
