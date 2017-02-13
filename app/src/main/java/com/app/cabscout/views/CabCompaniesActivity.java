package com.app.cabscout.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.cabscout.R;
import com.app.cabscout.model.Constants;
import com.app.cabscout.model.Event;
import com.app.cabscout.model.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Map;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.app.cabscout.controller.CabCompaniesManager.cabCompaniesList;

public class CabCompaniesActivity extends AppCompatActivity implements View.OnClickListener {

    Activity activity = this;
    Toolbar toolbar;
    RelativeLayout relativeLayout;
    TextView next_register;
    EditText selectCab;
   /* BottomSheetDialog bottomSheetDialog;
    ArrayList<String> cabCompaniesList;
    ArrayList<Integer> cabIdList;
    ArrayAdapter<String> arrayAdapter;*/
    ListView listView;
    String company_id, company;
    int cab_id;
    boolean isCompany;
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

      /*  bottomSheetDialog = new BottomSheetDialog(this);
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
        });*/

      /*  listView = (ListView) bottomSheetDialog.findViewById(R.id.listView);
        cabCompaniesList = new ArrayList<>();
        cabIdList = new ArrayList<>();*/

        /*for (Map.Entry<Integer, String> entry : CabCompaniesManager.cabCompaniesList.entrySet()) {
            Log.e(TAG, "cab id--" + entry.getKey());
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
        });*/
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
                for (Map.Entry<Integer, String> entry : cabCompaniesList.entrySet()) {
                    if (cabCompaniesList.containsValue(company)) {
                        company_id = String.valueOf(entry.getKey());
                        isCompany = true;

                    }
                    else {
                        isCompany = false;
                    }
                }
                if (company.isEmpty())
                    Utils.makeSnackBar(activity, relativeLayout, "Please enter your cab company");
                else if (!isCompany)
                    Utils.makeSnackBar(activity, relativeLayout, "Please enter valid cab company");
                else if (isCompany){
                    Intent i = new Intent(activity, RegistrationActivity.class);
                    i.putExtra("cab_id", String.valueOf(cab_id));
                    startActivity(i);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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
        switch (event.getKey()) {
            case Constants.CAB_COMPANIES_SUCCESS:

                break;
        }
    }

}
