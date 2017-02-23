package com.app.cabscout.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.app.cabscout.R;
import com.app.cabscout.controller.ModelManager;
import com.app.cabscout.model.CSPreferences;
import com.app.cabscout.model.Constants;
import com.app.cabscout.model.Event;
import com.app.cabscout.model.Operations;
import com.app.cabscout.model.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ChangePasswordActivity extends AppCompatActivity {

    Toolbar toolbar;
    EditText editOldPassword, editNewPassword, editConfirmPassword;
    Dialog dialog;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        initViews();
    }

    public void initViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Change Password");
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        editOldPassword = (EditText) findViewById(R.id.editOldPassword);
        editNewPassword = (EditText) findViewById(R.id.editNewPassword);
        editConfirmPassword = (EditText) findViewById(R.id.editConfirmPassword);
        
        dialog = Utils.createDialog(this);
    }

    public void changePassword(View v) {
        String old_password = editOldPassword.getText().toString();
        String new_password = editNewPassword.getText().toString();
        String confirm_password = editConfirmPassword.getText().toString();
        String customer_id = CSPreferences.readString(this, "customer_id");

        if (old_password.isEmpty() || new_password.isEmpty() || confirm_password.isEmpty()) {
            Toast.makeText(this, "Please fill all the details", Toast.LENGTH_SHORT).show();
        } else if (!new_password.equals(confirm_password)) {
            Toast.makeText(this, "Password didn't match.", Toast.LENGTH_SHORT).show();
        } else if (old_password.equals(new_password)) {
            Toast.makeText(this, "Please choose different password", Toast.LENGTH_SHORT).show();
        } else {
            dialog.show();
            ModelManager.getInstance().getChangePasswordManager().getPasswordDetails(this,
                    Operations.changePasswordTask(this, customer_id, old_password, new_password));
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

        dialog.dismiss();

        switch (event.getKey()) {

            case Constants.CHANGE_PASSWORD_SUCCESS:
                Toast.makeText(this, "Password has been updated successfully", Toast.LENGTH_SHORT).show();
                break;
            
            case Constants.CHANGE_PASSWORD_FAILED:
                Toast.makeText(this, "Please enter the correct password", Toast.LENGTH_SHORT).show();
                break;

            case Constants.SERVER_ERROR:
                Toast.makeText(this, "Sorry, some error occurred. Please try again.", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}