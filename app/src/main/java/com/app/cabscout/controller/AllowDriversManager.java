package com.app.cabscout.controller;

/*
 * Created by rishav on 14/2/17.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.app.cabscout.model.Constants;
import com.app.cabscout.model.Event;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

public class AllowDriversManager {
    private final String TAG = AllowDriversManager.class.getSimpleName();

    public void allowedDrivers(Context context, String params) {
        new ExecuteApi().execute(params);
    }

    private class ExecuteApi extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            HttpHandler httpHandler = new HttpHandler();
            String response = httpHandler.makeServiceCall(params[0]);

            Log.e(TAG, "allowed_cabs response-- "+response);

            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s != null) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONObject response = jsonObject.getJSONObject("response");

                    String id = response.getString("id");

                    if (id.equals("1") || id.equals("0")) {
                        EventBus.getDefault().post(new Event(Constants.ALLOW_CABS_SUCCESS, ""));
                    } else {
                        EventBus.getDefault().post(new Event(Constants.ALLOW_CABS_FAILED, ""));
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                EventBus.getDefault().post(new Event(Constants.SERVER_ERROR, ""));
            }
        }
    }
}