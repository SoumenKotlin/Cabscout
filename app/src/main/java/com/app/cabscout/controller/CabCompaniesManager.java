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
 * Created by rishav on 17/1/17.
 */

public class CabCompaniesManager {

    private final String TAG = CabCompaniesManager.class.getSimpleName();
    // public static final HashMap<Integer, String> cabCompaniesList = new HashMap<>();

    public void getCabCompanies(Context context, String params) {
        new ExecuteApi(context).execute(params);
    }

    private class ExecuteApi extends AsyncTask<String, String, String> {

        Context mContext;
        ExecuteApi(Context context) {
            mContext = context;
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpHandler httpHandler = new HttpHandler();
            String response = httpHandler.makeServiceCall(strings[0]);

            Log.e(TAG, "company response-- "+response);
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
                        EventBus.getDefault().post(new Event(Constants.CAB_COMPANIES_SUCCESS, response.getString("id")));
                    } else {
                        EventBus.getDefault().post(new Event(Constants.CAB_COMPANIES_EMPTY, ""));
                    }

                        /*EventBus.getDefault().post(new Event(Constants.CAB_COMPANIES_SUCCESS, company_id, cab_alias));*/

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                EventBus.getDefault().post(new Event(Constants.SERVER_ERROR, ""));
            }

        }
    }

    public void updateCabCompany(Context context, String params) {
        new ExecuteApiUpdateCab().execute(params);
    }

    private class ExecuteApiUpdateCab extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            HttpHandler httpHandler = new HttpHandler();
            String response = httpHandler.makeServiceCall(params[0]);

            Log.e(TAG, "update_company response-- "+response);
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
                        EventBus.getDefault().post(new Event(Constants.UPDATE_CAB_SUCCESS, ""));
                    } else if (id == -1) {
                        EventBus.getDefault().post(new Event(Constants.UPDATE_CAB_FAILED, ""));
                    }
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            } else {
                EventBus.getDefault().post(new Event(Constants.SERVER_ERROR, ""));
            }
        }
    }
}
