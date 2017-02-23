package com.app.cabscout.controller;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.app.cabscout.model.CSPreferences;
import com.app.cabscout.model.Constants;
import com.app.cabscout.model.Event;
import com.app.cabscout.model.Operations;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

/*
 * Created by rishav on 18/1/17.
 */

public class LoginManager {
    private static final String TAG = LoginManager.class.getSimpleName();

    public void doLogin(Context context, String url, String params) {
        new ExecuteApi(context).execute(url, params);
    }

    private class ExecuteApi extends AsyncTask<String, String, String> {
        Context mContext;

         ExecuteApi(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpHandler httpHandler = new HttpHandler();
            String response = httpHandler.getResponse(strings[0], strings[1]);
         //   String response = httpHandler.makeServiceCall(strings[0]);
            Log.e(TAG, "login response--" +response);

            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s != null) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONObject response = jsonObject.getJSONObject("response");
                    int id = response.getInt("id");
                    String message = response.getString("message");
                    if (id >= 1) {
                        CSPreferences.putString(mContext, "customer_id", String.valueOf(id));
                        new ExecuteApiUserDetails(mContext).execute(Operations.getUserDetails(mContext, String.valueOf(id)));
                    } else if (id == -1) {
                        EventBus.getDefault().post(new Event(Constants.ACCOUNT_NOT_REGISTERED, message));
                    } else if (id == -2) {
                        EventBus.getDefault().post(new Event(Constants.SERVER_ERROR, message));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                EventBus.getDefault().post(new Event(Constants.SERVER_ERROR, ""));
            }

        }
    }

    public void getUserDetails(Context context, String params) {
        new ExecuteApiUserDetails(context).execute(params);
    }

    private class ExecuteApiUserDetails extends AsyncTask<String, String, String> {
        private Context mContext;

         ExecuteApiUserDetails(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpHandler httpHandler = new HttpHandler();
            String response = httpHandler.makeServiceCall(strings[0]);

            Log.e(TAG, "user_details response--" +response);

            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
             if (s != null) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONObject response = jsonObject.getJSONObject("response");
                    String email = response.getString("email");
                    String name = response.getString("name");
                    String mobile = response.getString("mobile");
                    String home_address = response.getString("home_address");
                    String work_address = response.getString("work_address");
                    String profile_pic = response.getString("profile_pic");

                    CSPreferences.putString(mContext, "user_email", email);
                    CSPreferences.putString(mContext, "user_name", name);
                    CSPreferences.putString(mContext, "user_mobile", mobile);
                    CSPreferences.putString(mContext, "add_home", home_address);
                    CSPreferences.putString(mContext, "add_work", work_address);
                    CSPreferences.putString(mContext, "profile_pic", profile_pic);

                    EventBus.getDefault().post(new Event(Constants.LOGIN_SUCCESS, ""));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

             } else {
                 EventBus.getDefault().post(new Event(Constants.SERVER_ERROR, ""));
             }

        }
    }

}
