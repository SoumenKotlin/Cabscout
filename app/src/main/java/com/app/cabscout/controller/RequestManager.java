package com.app.cabscout.controller;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.app.cabscout.model.Constants;
import com.app.cabscout.model.Event;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

/*
 * Created by rishav on 24/1/17.
 */

public class RequestManager {

    private final String TAG = RequestManager.class.getSimpleName();

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
                        EventBus.getDefault().post(new Event(Constants.REQUEST_RIDE_SUCCESS, ""));
                    }
                    else {
                        EventBus.getDefault().post(new Event(Constants.REQUEST_RIDE_FAILED, ""));
                    }

                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
