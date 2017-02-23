package com.app.cabscout.controller;

/*
 * Created by rishav on 23/2/17.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.app.cabscout.model.Constants;
import com.app.cabscout.model.Event;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

public class ChangePasswordManager {

    private final String TAG = ChangePasswordManager.class.getSimpleName();

    public void getPasswordDetails(Context context, String params) {
        new ExecuteApi().execute(params);
    }

    private class ExecuteApi extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            HttpHandler httpHandler = new HttpHandler();
            String response = httpHandler.makeServiceCall(params[0]);
            Log.e(TAG, "change_password params-- "+response);
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s != null) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONObject response = jsonObject.getJSONObject("response");

                    int id = Integer.parseInt(response.getString("id"));
                    if (id >= 1) {
                        EventBus.getDefault().post(new Event(Constants.CHANGE_PASSWORD_SUCCESS, ""));
                    } else {
                        EventBus.getDefault().post(new Event(Constants.CHANGE_PASSWORD_FAILED, ""));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                EventBus.getDefault().post(new Event(Constants.SERVER_ERROR, ""));
            }
        }
    }
}
