package com.app.cabscout.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.cabscout.R;
import com.app.cabscout.controller.ModelManager;
import com.app.cabscout.model.Constants;
import com.app.cabscout.model.Event;
import com.app.cabscout.model.Operations;
import com.app.cabscout.model.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CabCompaniesActivity extends AppCompatActivity implements View.OnClickListener {

    Activity activity = this;
    Toolbar toolbar;
    RelativeLayout relativeLayout;
    TextView next_register;
    EditText selectCab;
    String company_id, company;
    int cab_id;
    TextView alreadyAccount;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cab_companies);

        initViews();
    }

    public void initViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Create Account");
        setSupportActionBar(toolbar);

        relativeLayout = (RelativeLayout) findViewById(R.id.activity_cab_companies);
        next_register = (TextView) findViewById(R.id.next_register);
        selectCab = (EditText) findViewById(R.id.selectCab);
        alreadyAccount = (TextView) findViewById(R.id.alreadyAccount);
        alreadyAccount.setOnClickListener(this);
        next_register.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.alreadyAccount:
                Intent intent = new Intent(activity, LoginActivity.class);
                startActivity(intent);
                finish();
                break;

            case R.id.next_register:
                company = selectCab.getText().toString().trim();

                if (company.isEmpty())
                    Utils.makeSnackBar(activity, relativeLayout, "Please enter your cab company");

                else
                    ModelManager.getInstance().getCabCompaniesManager().getCabCompanies(activity,
                            Operations.getCabCompaniesTask(activity,company));

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
        switch (event.getKey()) {
            case Constants.CAB_COMPANIES_SUCCESS:
                Intent i = new Intent(activity, RegistrationActivity.class);
                i.putExtra("cab_id", event.getValue());
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;

            case Constants.CAB_COMPANIES_EMPTY:
                Utils.makeSnackBar(activity, relativeLayout, "Please enter the valid cab company");
                break;
        }
    }

}
