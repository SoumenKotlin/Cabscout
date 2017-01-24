package com.app.cabscout.controller;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.app.cabscout.model.Constants;
import com.app.cabscout.model.Event;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/*
 * Created by rishav on 17/1/17.
 */

public class CabCompaniesManager {

    private final String TAG = CabCompaniesManager.class.getSimpleName();
    public static final HashMap<Integer, String> cabCompaniesList = new HashMap<>();

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

            Log.e(TAG, "companies list--"+response);
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s != null) {

                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray response = jsonObject.getJSONArray("response");

                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObject1 = response.getJSONObject(i);
                        String company_id = jsonObject1.getString("company_id");
                        String cab_alias = jsonObject1.getString("cab_alias");


                        cabCompaniesList.put(Integer.parseInt(company_id), cab_alias);
                        EventBus.getDefault().post(new Event(Constants.CAB_COMPANIES_SUCCESS, company_id, cab_alias));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }
    }
}
