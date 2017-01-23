package com.app.cabscout.controller;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.app.cabscout.model.Constants;
import com.app.cabscout.model.Event;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by rishav on 18/1/17.
 */

public class LoginManager {
    private static final String TAG = LoginManager.class.getSimpleName();

    public void doLogin(Context context, String params) {
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

            Log.e(TAG, "login response--" +response);

            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONObject response = jsonObject.getJSONObject("response");
                int id = response.getInt("id");
                String message = response.getString("message");
                if (id >= 1) {
                    EventBus.getDefault().post(new Event(Constants.LOGIN_SUCCESS, message));
                }
                else if (id == -1) {
                    EventBus.getDefault().post(new Event(Constants.ACCOUNT_NOT_REGISTERED, message));
                } else if (id == -2){
                    EventBus.getDefault().post(new Event(Constants.SERVER_ERROR, message));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

}
