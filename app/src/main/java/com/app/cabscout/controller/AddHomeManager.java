package com.app.cabscout.controller;

/*
 * Created by rishav on 31/1/17.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.app.cabscout.model.Constants;
import com.app.cabscout.model.Event;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

public class AddHomeManager {
    private static final String TAG = AddHomeManager.class.getSimpleName();

    public void addHomeLocation(Context context, String params) {
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
            Log.e(TAG, "add_home response-- "+response);

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
                    if (id.equals("1")) {
                        EventBus.getDefault().post(new Event(Constants.ADD_HOME_SUCCESS, ""));
                    }
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }

            }
        }
    }
}
