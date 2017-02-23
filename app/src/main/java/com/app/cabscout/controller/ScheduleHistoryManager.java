package com.app.cabscout.controller;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.app.cabscout.model.Beans.ScheduleHistoryBeans;
import com.app.cabscout.model.Constants;
import com.app.cabscout.model.Event;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/*
 * Created by rishav on 25/1/17.
 */

public class ScheduleHistoryManager {

    private static final String TAG = ScheduleHistoryManager.class.getSimpleName();
    public static ArrayList<ScheduleHistoryBeans> schedulesList;

    public void getScheduleHistory(Context context, String params) {
        new ExecuteApi(context).execute(params);
        schedulesList = new ArrayList<>();
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
            Log.e(TAG, "schedule_history response-- "+response);
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
                        EventBus.getDefault().post(new Event(Constants.SCHEDULED_HISTORY_EMPTY, ""));
                        return;
                    }

                    for (int i=0; i<responseArray.length(); i++) {
                        JSONObject data = responseArray.getJSONObject(i);
                        String[] pickup_coordinates = data.getString("pickup_cordinates").split(",");
                        String[] drop_coordinates = data.getString("drop_cordinates").split(",");
                        String dateTime = data.getString("datetime");
                        String payment_type = data.getString("payment_type");
                        String price = data.getString("price");
                        String car_name = data.getString("car_name");

                        String pickup_latitude = pickup_coordinates[pickup_coordinates.length-2];
                        String pickup_longitude = pickup_coordinates[pickup_coordinates.length-1];

                        String drop_latitude = drop_coordinates[drop_coordinates.length-2];
                        String drop_longitude = drop_coordinates[drop_coordinates.length-1];

                        String pickup_address = data.getString("pickup_location");

                        String drop_address = data.getString("drop_location");

                        ScheduleHistoryBeans historyBeans = new ScheduleHistoryBeans(pickup_latitude, pickup_longitude,
                                drop_latitude, drop_longitude, dateTime, payment_type, price,
                                car_name, pickup_address, drop_address);

                        schedulesList.add(historyBeans);
                    }

                    EventBus.getDefault().post(new Event(Constants.SCHEDULED_HISTORY_SUCCESS, ""));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
