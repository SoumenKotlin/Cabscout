package com.app.cabscout.views;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.cabscout.R;
import com.app.cabscout.controller.CabCompaniesManager;
import com.app.cabscout.model.CSPreferences;

import java.util.Map;

public class LoginFacebookActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = LoginFacebookActivity.class.getSimpleName();
    EditText editCompany, editName, editEmail, editPhone;
    String name, email, phone, company;
    TextView textRegister;
    LinearLayout linearLayout;
    TextInputLayout textInputCompany;
    boolean isCompany;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_facebook);

        initViews();
    }

    public void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Verify Profile");
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        editCompany = (EditText) findViewById(R.id.editCompanyName);
        editName = (EditText) findViewById(R.id.editName);
        editEmail = (EditText) findViewById(R.id.editEmail);
        editPhone = (EditText) findViewById(R.id.editPhone);
        textRegister = (TextView) findViewById(R.id.textRegister);
        textRegister.setOnClickListener(this);

        textInputCompany = (TextInputLayout) findViewById(R.id.textInputCompanyName);

        editCompany.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    for (Map.Entry<Integer, String> entry : CabCompaniesManager.cabCompaniesList.entrySet()) {
                        Log.e(TAG, entry.getValue());

                        if (editCompany.getText().toString().isEmpty()) {
                            textInputCompany.setError("Please enter the company code");
                        } else if (!editCompany.getText().toString().equals(entry.getValue())) {
                            textInputCompany.setError("Please enter the valid company code");
                        } else {
                            textInputCompany.setErrorEnabled(false);
                        }
                    }
                } else {

                    textInputCompany.setErrorEnabled(false);
                }
            }
        });

        linearLayout = (LinearLayout) findViewById(R.id.activity_login_facebook);

        name = CSPreferences.readString(this, "user_name");
        email = CSPreferences.readString(this, "user_email");

        editName.setText(name);

        if (!email.isEmpty())
            editEmail.setText(email);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textRegister:
                name = editName.getText().toString();
                email = editEmail.getText().toString();
                phone = editPhone.getText().toString();
                company = editCompany.getText().toString();

                if (company.isEmpty() || name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                    Snackbar.make(linearLayout, "Please fill all the details", Snackbar.LENGTH_LONG).show();
                    return;
                }

                for (Map.Entry<Integer, String> entry : CabCompaniesManager.cabCompaniesList.entrySet()) {

                    if (CabCompaniesManager.cabCompaniesList.containsValue(company)) {
                        Toast.makeText(this, "Ok", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Noooooooo", Toast.LENGTH_SHORT).show();
                    }

                }

                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setMessage("Are you sure you want to cancel? \nYour information will not be saved.");
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });

                alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();

                break;
        }
        return true;
    }
}
