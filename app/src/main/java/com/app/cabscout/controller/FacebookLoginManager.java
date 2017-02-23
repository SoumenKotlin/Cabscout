package com.app.cabscout.controller;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.app.cabscout.model.CSPreferences;
import com.app.cabscout.model.Constants;
import com.app.cabscout.model.Event;
import com.app.cabscout.model.Operations;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginResult;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/*
 * Created by rishav on 02/02/17.
 */

public class FacebookLoginManager {

    private static final String TAG = FacebookLoginManager.class.getSimpleName();

    public void doFacebookLogin(final Activity context, CallbackManager callbackManager) {

        com.facebook.login.LoginManager.getInstance().logInWithReadPermissions(context,
                Arrays.asList("email", "user_friends", "public_profile")
        );

        com.facebook.login.LoginManager.getInstance().registerCallback(
                callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        String user_id = loginResult.getAccessToken().getUserId();

                        Log.e(TAG, "Id: " + user_id);
                        Log.e(TAG, "Token: " + loginResult.getAccessToken().getToken());
                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(FacebookException exception) {
                        exception.printStackTrace();
                    }
                }
        );

    }

    public void getFacebookData(final Activity context) {
        Bundle params = new Bundle();
        params.putString("fields", "id,name,email,picture.type(large)");
        new GraphRequest(AccessToken.getCurrentAccessToken(), "me", params, HttpMethod.GET,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        if (response != null) {
                            try {
                                JSONObject data = response.getJSONObject();
                                Log.e(TAG, "Data: " + data);
                                String id = data.getString("id");
                                String name = data.getString("name");
                                String email = "";

                                try {
                                    email = data.getString("email");
                                } catch (JSONException ex) {
                                    ex.printStackTrace();
                                }

                                String profilePicUrl = "";
                                Log.e(TAG, "user_id-- " + id);
                                if (data.has("picture")) {
                                    profilePicUrl = data.getJSONObject("picture").getJSONObject("data").getString("url");

                                    Log.e(TAG, "Image: " + profilePicUrl);
                                }

                                CSPreferences.putString(context, "user_name", name);
                                CSPreferences.putString(context, "fb_id", id);
                                CSPreferences.putString(context, "user_email", email);
                                CSPreferences.putString(context, "profile_pic", profilePicUrl);

                                new ExecuteApi(context).execute(Operations.facebookLoginTask(context, id));

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).executeAsync();
    }

    class ExecuteApi extends AsyncTask<String, String, String> {
        private Context context;

        public ExecuteApi(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... params) {
            HttpHandler httpHandler = new HttpHandler();
            String response = httpHandler.makeServiceCall(params[0]);
            Log.e(TAG, "facebook_login response-- " + response);

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
                        CSPreferences.putString(context, "customer_id", String.valueOf(id));
                        new ExecuteApiUserDetails(context).execute(Operations.getUserDetails(context, String.valueOf(id)));


                    } else {
                        EventBus.getDefault().post(new Event(Constants.FACEBOOK_LOGIN_EMPTY, ""));
                    }

                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            } else {
                EventBus.getDefault().post(new Event(Constants.SERVER_ERROR, ""));
            }
        }
    }

    public void registerUser(Context context, String url, String params) {
        new ExecuteApiRegisterUser(context).execute(url, params);
    }

    private class ExecuteApiRegisterUser extends AsyncTask<String, String, String> {
        Context mContext;

        ExecuteApiRegisterUser(Context mContext) {
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
                        EventBus.getDefault().post(new Event(Constants.ALREADY_REGISTERED, message));
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
                    String name = response.getString("name").replace("+", " ");
                    String mobile = response.getString("mobile");
                    String home_address = response.getString("home_address");
                    String work_address = response.getString("work_address");
                    String profile_pic = response.getString("profile_pic");
                    String allow_drivers = response.getString("allow_search");

                    CSPreferences.putString(mContext, "user_email", email);
                    CSPreferences.putString(mContext, "user_name", name);
                    CSPreferences.putString(mContext, "user_mobile", mobile);
                    CSPreferences.putString(mContext, "add_home", home_address);
                    CSPreferences.putString(mContext, "add_work", work_address);
                    CSPreferences.putString(mContext, "profile_pic", profile_pic);
                    CSPreferences.putString(mContext, "allow_drivers", allow_drivers);

                    EventBus.getDefault().post(new Event(Constants.FACEBOOK_LOGIN_SUCCESS, ""));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                EventBus.getDefault().post(new Event(Constants.SERVER_ERROR, ""));
            }

        }
    }

}