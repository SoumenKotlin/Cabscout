package com.app.cabscout.controller;

/*
 * Created by rishav on 1/2/17.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.app.cabscout.model.CSPreferences;
import com.app.cabscout.model.Constants;
import com.app.cabscout.model.Event;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

public class ImageUploadManager {
    private static final String TAG = ImageUploadManager.class.getSimpleName();

    public void uploadImageToServer(Context context, String url, String params) {
        new ExecuteApi(context).execute(url, params);
    }

    private class ExecuteApi extends AsyncTask<String, String, String>{
        private Context mContext;

         ExecuteApi(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpHandler httpHandler = new HttpHandler();

            String response = httpHandler.getResponse(strings[0], strings[1]);

            Log.e(TAG, "upload_image response-- "+response);
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
                    String profile_pic = response.getString("profile_pic");
                    CSPreferences.putString(mContext, "profile_pic", profile_pic);

                    if (id.equals("1")) {
                        EventBus.getDefault().post(new Event(Constants.UPLOAD_IMAGE_SUCCESS, ""));
                    } else {
                        EventBus.getDefault().post(new Event(Constants.UPLOAD_IMAGE_FAILED, ""));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else {
                EventBus.getDefault().post(new Event(Constants.UPLOAD_IMAGE_FAILED, ""));
            }
        }
    }
}
