package com.app.cabscout.views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.cabscout.R;
import com.app.cabscout.controller.ModelManager;
import com.app.cabscout.model.CSPreferences;
import com.app.cabscout.model.Constants;
import com.app.cabscout.model.Event;
import com.app.cabscout.model.Operations;
import com.app.cabscout.model.Utils;
import com.github.rahatarmanahmed.cpv.CircularProgressView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    CircularProgressView progressView;
    EditText editEmail, editPassword;
    String email, password;
    TextView textLogin, createAccount;
    Activity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
    }

    public void initViews() {
        progressView = (CircularProgressView)findViewById(R.id.progressView);
        editEmail = (EditText)findViewById(R.id.editEmail);
        editPassword = (EditText)findViewById(R.id.editPassword);
        createAccount = (TextView)findViewById(R.id.createAccount);
        createAccount.setOnClickListener(this);

        textLogin = (TextView)findViewById(R.id.textLogin);
        textLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.createAccount:
                Intent i = new Intent(activity, CabCompaniesActivity.class);
                startActivity(i);
                finish();
                break;
            case R.id.textLogin:
                email = editEmail.getText().toString();
                password = editPassword.getText().toString();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(this, "Please fill all the details", Toast.LENGTH_SHORT).show();
                }
                else if (!Utils.emailValidator(email)) {
                    Toast.makeText(this, "Please enter the valid email address", Toast.LENGTH_SHORT).show();
                }
                else {
                    progressView.setVisibility(View.VISIBLE);
                    ModelManager.getInstance().getLoginManager().doLogin(activity, Operations.loginTask(activity,
                            email, password, CSPreferences.readString(activity, "device_token")));
                }
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEvent(Event event) {
        progressView.setVisibility(View.GONE);
        switch (event.getKey()) {
            case Constants.LOGIN_SUCCESS:
                Toast.makeText(activity, "Logged in successfully", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(activity, MainActivity.class);
                CSPreferences.putString(activity, "login_status", "true");
                startActivity(i);
                break;
            case Constants.ACCOUNT_NOT_REGISTERED:
                Toast.makeText(activity, event.getValue(), Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
