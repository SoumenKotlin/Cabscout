package com.app.cabscout.views;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.cabscout.R;
import com.app.cabscout.controller.ModelManager;
import com.app.cabscout.model.CSPreferences;
import com.app.cabscout.model.Config;
import com.app.cabscout.model.Constants;
import com.app.cabscout.model.Event;
import com.app.cabscout.model.Operations;
import com.app.cabscout.model.Utils;
import com.app.cabscout.model.custom.ImagePicker;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    Toolbar toolbar;
    Activity activity = this;
    LinearLayout homeLayout, workLayout;
    EditText editName, editEmail, editPhone;
    String user_name, user_email, user_mobile, profile_pic;
    TextView addHome, addWork, homeLocation, workLocation;
    ImageView editHome, deleteHome, editWork, deleteWork;
    String home, work;
    Dialog dialog;
    BottomSheetDialog bottomSheetDialog;
    CircleImageView showImage;
    ImageView updateImage;
    TextView openCamera, openGallery;
    Bitmap photo;
    private final int STORAGE_PERMISSION_CODE = 101;
    private String str_pic;
    BottomSheetDialog cabBottomDialog;
    TextView changeCarCompany, cancel, changeCompany, changePassword;
    String companyName, company_id, customer_id;
    EditText editCompanyName;
    CheckBox allowCabCheckBox;
    boolean isAllow;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initViews();
    }

    public void initViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Settings");
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        dialog = Utils.createDialog(activity);
        editName = (EditText) findViewById(R.id.editName);
        editEmail = (EditText) findViewById(R.id.editEmail);
        editPhone = (EditText) findViewById(R.id.editPhone);

        user_email = CSPreferences.readString(activity, "user_email");
        user_mobile = CSPreferences.readString(activity, "user_mobile");
        user_name = CSPreferences.readString(activity, "user_name");
        profile_pic = CSPreferences.readString(activity, "profile_pic");
        showImage = (CircleImageView) findViewById(R.id.showImage);
        updateImage = (ImageView) findViewById(R.id.updateImage);

        if (!profile_pic.startsWith("http")) {
            profile_pic = Config.user_pic_url + profile_pic;
        }

        if (!profile_pic.isEmpty()) {
            Picasso.with(activity)
                    .load(profile_pic)
                    .placeholder(R.drawable.ic__contact_picture_placeholder)
                    .into(showImage);
        }

        editName.setText(user_name);
        editEmail.setText(user_email);
        editPhone.setText(user_mobile);

        homeLayout = (LinearLayout) findViewById(R.id.layoutHome);
        workLayout = (LinearLayout) findViewById(R.id.layoutWork);

        addHome = (TextView) findViewById(R.id.addHome);
        addWork = (TextView) findViewById(R.id.addWork);

        addHome.setOnClickListener(this);
        addWork.setOnClickListener(this);

        homeLocation = (TextView) findViewById(R.id.homeLocation);
        workLocation = (TextView) findViewById(R.id.workLocation);

        editHome = (ImageView) findViewById(R.id.editHome);
        deleteHome = (ImageView) findViewById(R.id.deleteHome);
        editWork = (ImageView) findViewById(R.id.editWork);
        deleteWork = (ImageView) findViewById(R.id.deleteWork);

        editHome.setOnClickListener(this);
        deleteHome.setOnClickListener(this);
        editWork.setOnClickListener(this);
        deleteWork.setOnClickListener(this);

        updateImage.setOnClickListener(this);

        bottomSheetDialog = Utils.createBottomSheetDialog(this, R.layout.bottom_update_photo);

        changeCarCompany = (TextView) findViewById(R.id.changeCarCompany);
        changeCarCompany.setOnClickListener(this);
        cabBottomDialog = Utils.createBottomSheetDialog(this, R.layout.bottom_car_company_change);

        allowCabCheckBox = (CheckBox) findViewById(R.id.allowCabCheckBox);
        allowCabCheckBox.setOnCheckedChangeListener(this);

        changePassword = (TextView) findViewById(R.id.changePassword);
        changePassword.setOnClickListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        dialog.show();
        if (isChecked) {
            isAllow = true;
            ModelManager.getInstance().getAllowDriversManager().allowedDrivers(activity,
                    Operations.updateAllowedDrivers(activity, customer_id, "1"));
        } else {
            isAllow = false;
            ModelManager.getInstance().getAllowDriversManager().allowedDrivers(activity,
                    Operations.updateAllowedDrivers(activity, customer_id, "0"));
        }
    }

    public void changePicBottomSheet(BottomSheetDialog bottomSheetDialog) {
        openCamera = (TextView) bottomSheetDialog.findViewById(R.id.openCamera);
        openGallery = (TextView) bottomSheetDialog.findViewById(R.id.openGallery);

        openCamera.setOnClickListener(this);
        openGallery.setOnClickListener(this);
        bottomSheetDialog.show();
    }

    public void changeCarBottomSheet(BottomSheetDialog bottomSheetDialog) {
        cancel = (TextView) bottomSheetDialog.findViewById(R.id.cancel);
        changeCompany = (TextView) bottomSheetDialog.findViewById(R.id.changeCompany);

        editCompanyName = (EditText) bottomSheetDialog.findViewById(R.id.editCompanyName);

        cancel.setOnClickListener(this);
        changeCompany.setOnClickListener(this);

        bottomSheetDialog.show();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.updateImage:
                // changePicBottomSheet(bottomSheetDialog);
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);


                break;

               /* case R.id.openCamera:
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                str_pic = "camera";
                bottomSheetDialog.dismiss();

                break;

                  case R.id.openGallery:
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                str_pic = "gallery";
                bottomSheetDialog.dismiss();
                //Utils.openGallery(activity);
                break; */

            case R.id.addHome: {
                addHome();
                break;
            }

            case R.id.addWork: {
                addWork();
                break;
            }

            case R.id.editHome: {
                addHome();
                break;
            }

            case R.id.deleteHome: {
                dialog.show();
                ModelManager.getInstance().getAddHomeManager().addHomeLocation(activity, Operations.updateHomeDetails(activity,
                        "", "", CSPreferences.readString(activity, "customer_id"), ""));
                break;
            }

            case R.id.editWork: {
                addWork();
                break;
            }

            case R.id.deleteWork: {
                dialog.show();
                ModelManager.getInstance().getAddWorkManager().addWorkLocation(activity, Operations.updateWorkDetails(activity,
                        "", "", CSPreferences.readString(activity, "customer_id"), ""));
                break;
            }

            case R.id.changeCarCompany: {
                changeCarBottomSheet(cabBottomDialog);

                break;
            }

            case R.id.cancel:
                cabBottomDialog.dismiss();
                break;

            case R.id.changeCompany:
                customer_id = CSPreferences.readString(activity, "customer_id");
                companyName = editCompanyName.getText().toString();
                if (companyName.isEmpty()) {
                    Toast.makeText(activity, "Please enter the company code", Toast.LENGTH_SHORT).show();
                } else {
                    dialog.show();
                    ModelManager.getInstance().getCabCompaniesManager().getCabCompanies(activity,
                            Operations.getCabCompaniesTask(activity, companyName));
                }
                break;

            case R.id.changePassword:
                startActivity(new Intent(activity, ChangePasswordActivity.class));
                break;
        }
    }


    public void addHome() {
        Intent homeIntent = new Intent(activity, SearchAddressActivity.class);
        homeIntent.putExtra("Address", "Add Home");
        startActivity(homeIntent);
    }

    public void addWork() {
        Intent workIntent = new Intent(activity, SearchAddressActivity.class);
        workIntent.putExtra("Address", "Add Work");
        startActivity(workIntent);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        customer_id = CSPreferences.readString(activity, "customer_id");

        if (requestCode == 100 && resultCode == RESULT_OK) {

            //      Bundle extras = data.getExtras();
            photo = ImagePicker.getImageFromResult(this, resultCode, data);

            //    photo = (Bitmap) extras.get("data");

            String base64Image = Utils.base64Encode(photo);
            dialog.show();

            ModelManager.getInstance().getImageUploadManager().uploadImageToServer(activity, Config.update_profile_pic_url,
                    Operations.updateProfileImage(activity, customer_id, base64Image));

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

            case Constants.LOGIN_SUCCESS:
                dialog.dismiss();
                showData();
                break;

            case Constants.ADD_HOME_SUCCESS:
                dialog.dismiss();
                homeLayout.setVisibility(View.GONE);
                addHome.setText("Add Home");
                CSPreferences.putString(activity, "add_home", "");
                break;

            case Constants.ADD_WORK_SUCCESS:
                dialog.dismiss();
                workLayout.setVisibility(View.GONE);
                addWork.setText("Add Work");
                CSPreferences.putString(activity, "add_work", "");
                break;

            case Constants.UPLOAD_IMAGE_SUCCESS:
                dialog.dismiss();
                showImage.setImageBitmap(photo);
                break;

            case Constants.UPLOAD_IMAGE_FAILED:
                dialog.dismiss();
                Toast.makeText(activity, "Sorry, profile picture has not been updated. Please try again.",
                        Toast.LENGTH_SHORT).show();
                break;

            case Constants.CAB_COMPANIES_SUCCESS:
                company_id = event.getValue();
                ModelManager.getInstance().getCabCompaniesManager().updateCabCompany(activity,
                        Operations.updateCabCompany(activity, customer_id, company_id));
                break;

            case Constants.CAB_COMPANIES_EMPTY:
                dialog.dismiss();
                Toast.makeText(activity, "Please enter the valid company code", Toast.LENGTH_SHORT).show();
                break;

            case Constants.UPDATE_CAB_SUCCESS:
                dialog.dismiss();
                Toast.makeText(activity, "Cab has been updated successfully", Toast.LENGTH_SHORT).show();
                break;

            case Constants.UPDATE_CAB_FAILED:
                dialog.dismiss();
                Toast.makeText(activity, "You are already registered with this company", Toast.LENGTH_SHORT).show();
                break;

            case Constants.SERVER_ERROR:
                dialog.dismiss();
                Toast.makeText(activity, "Sorry, there is some error occurred. Please try again", Toast.LENGTH_SHORT).show();
                break;

            case Constants.ALLOW_CABS_SUCCESS:
                dialog.dismiss();

                if (isAllow) {
                    CSPreferences.putString(activity, "allow_drivers", "1");
                } else {
                    CSPreferences.putString(activity, "allow_drivers", "0");
                }

                Toast.makeText(activity, "Updated successfully", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // showData();
        customer_id = CSPreferences.readString(activity, "customer_id");
        dialog.show();

        ModelManager.getInstance().getLoginManager().getUserDetails(this, Operations.getUserDetails(this, customer_id));
    }

    public void showData() {
        home = CSPreferences.readString(activity, "add_home");
        work = CSPreferences.readString(activity, "add_work");
        profile_pic = CSPreferences.readString(activity, "profile_pic");

        if (!profile_pic.startsWith("http")) {
            profile_pic = Config.user_pic_url + profile_pic;
        }

        if (!profile_pic.isEmpty()) {
            Picasso.with(activity)
                    .load(profile_pic)
                    .placeholder(R.drawable.ic__contact_picture_placeholder)
                    .into(showImage);
        }

        if (home.isEmpty()) {
            homeLayout.setVisibility(View.GONE);
        } else {
            homeLayout.setVisibility(View.VISIBLE);
            addHome.setText("Home");
            homeLocation.setText(home);
        }

        if (work.isEmpty()) {
            workLayout.setVisibility(View.GONE);
        } else {
            workLayout.setVisibility(View.VISIBLE);
            addWork.setText("Work");
            workLocation.setText(work);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                /*if (str_pic.equals("camera")) {
                    Utils.openCamera(activity);
                } else if (str_pic.equals("gallery")) {
                    //Utils.openGallery(activity);
                    Toast.makeText(activity, "Working on it..", Toast.LENGTH_SHORT).show();
                }*/
                Intent chooseImageIntent = ImagePicker.getPickImageIntent(activity);
                startActivityForResult(chooseImageIntent, 100);
            } else {
                Toast.makeText(activity, "Please grant all the permissions first.", Toast.LENGTH_SHORT).show();
            }
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