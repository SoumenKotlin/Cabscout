
package com.app.cabscout.views;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.cabscout.R;
import com.app.cabscout.controller.ModelManager;
import com.app.cabscout.model.Constants;
import com.app.cabscout.model.Event;
import com.app.cabscout.model.Operations;
import com.app.cabscout.model.Utils;
import com.google.firebase.iid.FirebaseInstanceId;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener{

    EditText editName, editEmail, editPhone, editPassword, editConfirmPassword;
    String name, email, phone, password, confirmPassword;
    TextView registerUser, alreadyAccount;
    CheckBox termsCheckBox, policyCheckBox;

    String deviceToken;
    //CircularProgressView progressView;
    Dialog dialog;
    Toolbar toolbar;
    RelativeLayout relativeLayout;
    String cab_id;
    Activity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        initViews();
    }

    public void initViews() {
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("CREATE ACCOUNT");
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        relativeLayout = (RelativeLayout)findViewById(R.id.activity_registration);

        dialog = Utils.createDialog(this);
        deviceToken = FirebaseInstanceId.getInstance().getToken();

        cab_id = getIntent().getStringExtra("cab_id");

        editName = (EditText)findViewById(R.id.editName);
        editEmail = (EditText)findViewById(R.id.editEmail);
        editPhone = (EditText)findViewById(R.id.editPhone);
        editPassword = (EditText)findViewById(R.id.editPassword);
        editConfirmPassword = (EditText)findViewById(R.id.editConfirmPassword);
        //progressView = (CircularProgressView)findViewById(progressView);
        termsCheckBox = (CheckBox)findViewById(R.id.termsCheckbox);
        policyCheckBox = (CheckBox)findViewById(R.id.privacyCheckbox);
        alreadyAccount = (TextView)findViewById(R.id.alreadyAccount);

        registerUser = (TextView)findViewById(R.id.textRegister);
        registerUser.setOnClickListener(this);
        alreadyAccount.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.alreadyAccount:
                Intent i = new Intent(activity, LoginActivity.class);
                startActivity(i);
                finish();
                break;
            case R.id.textRegister:
                name = editName.getText().toString().trim();
                email = editEmail.getText().toString().trim();
                phone = editPhone.getText().toString().trim();
                password = editPassword.getText().toString();
                confirmPassword = editConfirmPassword.getText().toString();

                if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    Snackbar.make(relativeLayout, "Please fill all the details", Snackbar.LENGTH_LONG).show();
                } else if (!password.equals(confirmPassword)) {
                    Snackbar.make(relativeLayout, "Password didn't match", Snackbar.LENGTH_LONG).show();
                }
                else if (!Utils.emailValidator(email)) {
                    Snackbar.make(relativeLayout, "Please enter the valid email address", Snackbar.LENGTH_LONG).show();
                }
                else if(!termsCheckBox.isChecked() || !policyCheckBox.isChecked()) {
                    Snackbar.make(relativeLayout, "You must be agree to all the terms and conditions", Snackbar.LENGTH_LONG).show();
                }
                else {
                 //   progressView.setVisibility(View.VISIBLE);
                    dialog.show();
                    ModelManager.getInstance().getRegistrationManager().registerUser(getApplicationContext(),
                            Operations.registrationTask(getApplicationContext(), email, password, cab_id, name, deviceToken, phone));
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
        //progressView.setVisibility(View.GONE);
        dialog.dismiss();
        switch (event.getKey()) {
            case Constants.REGISTRATION_SUCCESS:
                Intent i = new Intent(activity, LoginActivity.class);
                startActivity(i);
                Toast.makeText(this, event.getValue(), Toast.LENGTH_SHORT).show();
                break;
            case Constants.ALREADY_REGISTERED:
                Toast.makeText(this, event.getValue(), Toast.LENGTH_SHORT).show();
                break;
            case Constants.SERVER_ERROR:
                Toast.makeText(this, event.getValue(), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                overridePendingTransition(android.R.anim.fade_out, android.R.anim.fade_in);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
