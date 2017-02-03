package com.app.cabscout.views;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.cabscout.R;
import com.app.cabscout.controller.CabCompaniesManager;
import com.app.cabscout.model.Constants;
import com.app.cabscout.model.Event;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Map;

public class CabCompaniesActivity extends AppCompatActivity implements View.OnClickListener{

    private final String TAG = CabCompaniesActivity.class.getSimpleName();
    Activity activity = this;
    Toolbar toolbar;
    RelativeLayout relativeLayout;
    TextView next_register, selectCab;
    BottomSheetDialog bottomSheetDialog;
    ArrayList<String> cabCompaniesList;
    ArrayList<Integer> cabIdList;
    ArrayAdapter<String> arrayAdapter;
    ListView listView;
    String selected_cab = "";
    int cab_id;

    TextView alreadyAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cab_companies);

        initViews();
    }

    public void initViews() {
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("Create Account");
        setSupportActionBar(toolbar);

        relativeLayout = (RelativeLayout)findViewById(R.id.activity_cab_companies);
        next_register = (TextView)findViewById(R.id.next_register);
        selectCab = (TextView)findViewById(R.id.selectCab);
        alreadyAccount = (TextView)findViewById(R.id.alreadyAccount);
        alreadyAccount.setOnClickListener(this);
        next_register.setOnClickListener(this);
        selectCab.setOnClickListener(this);

        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet);

        bottomSheetDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
                FrameLayout bottomSheet = (FrameLayout) bottomSheetDialog
                        .findViewById(android.support.design.R.id.design_bottom_sheet);
                assert bottomSheet != null;
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        listView = (ListView)bottomSheetDialog.findViewById(R.id.listView);
        cabCompaniesList = new ArrayList<>();
        cabIdList = new ArrayList<>();

        for (Map.Entry<Integer,String> entry : CabCompaniesManager.cabCompaniesList.entrySet()) {
            Log.e(TAG, "cab id--"+ entry.getKey());
            cabCompaniesList.add(entry.getValue());
            cabIdList.add(entry.getKey());
        }

        arrayAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, cabCompaniesList);

        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                selectCab.setText(cabCompaniesList.get(position));
                selected_cab = cabCompaniesList.get(position);
                cab_id = cabIdList.get(position);
                bottomSheetDialog.dismiss();
            }
        });
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
                if (selected_cab.isEmpty())
                    Snackbar.make(relativeLayout, "Please select your cab company", Snackbar.LENGTH_LONG).show();
                else {
                    Intent i = new Intent(activity, RegistrationActivity.class);
                    i.putExtra("cab_id", String.valueOf(cab_id));
                    startActivity(i);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
                break;

            case R.id.selectCab:
                bottomSheetDialog.show();
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

                break;
        }

    }


}
