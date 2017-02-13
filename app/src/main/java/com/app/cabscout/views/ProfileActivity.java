package com.app.cabscout.views;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
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
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

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
    String customer_id;
    private final int STORAGE_PERMISSION_CODE = 101;
    private String str_pic;
    BottomSheetDialog cabBottomDialog;
    TextView changeCarCompany;

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
            profile_pic = Config.user_pic_url+profile_pic;
        }

        if (!profile_pic.isEmpty()) {
            Picasso.with(activity)
                    .load(profile_pic)
                    .placeholder(R.drawable.ic_icon_profile_pic)
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

        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.bottom_update_photo);

        changeCarCompany = (TextView)findViewById(R.id.changeCarCompany);
        changeCarCompany.setOnClickListener(this);
        cabBottomDialog = Utils.createBottomSheetDialog(this, R.layout.bottom_car_company_change);

    }

    public void showBottomSheet(BottomSheetDialog bottomSheetDialog) {

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

        openCamera = (TextView) bottomSheetDialog.findViewById(R.id.openCamera);
        openGallery = (TextView) bottomSheetDialog.findViewById(R.id.openGallery);

        openCamera.setOnClickListener(this);
        openGallery.setOnClickListener(this);
        bottomSheetDialog.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.updateImage:
                showBottomSheet(bottomSheetDialog);
                break;

            case R.id.openCamera:
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
                break;

            case R.id.addHome: {
                Intent homeIntent = new Intent(activity, SearchAddressActivity.class);
                homeIntent.putExtra("Address", "Add Home");
                startActivity(homeIntent);
                break;
            }

            case R.id.addWork: {
                Intent workIntent = new Intent(activity, SearchAddressActivity.class);
                workIntent.putExtra("Address", "Add Work");
                startActivity(workIntent);
                break;
            }

            case R.id.editHome: {
                Intent homeIntent = new Intent(activity, SearchAddressActivity.class);
                homeIntent.putExtra("Address", "Add Home");
                startActivity(homeIntent);
                break;
            }

            case R.id.deleteHome: {
                dialog.show();
                ModelManager.getInstance().getAddHomeManager().addHomeLocation(activity, Operations.updateHomeDetails(activity,
                        "", "", CSPreferences.readString(activity, "customer_id"), ""));
                break;
            }

            case R.id.editWork: {
                Intent workIntent = new Intent(activity, SearchAddressActivity.class);
                workIntent.putExtra("Address", "Add Work");
                startActivity(workIntent);
                break;
            }

            case R.id.deleteWork: {
                dialog.show();
                ModelManager.getInstance().getAddWorkManager().addWorkLocation(activity, Operations.updateWorkDetails(activity,
                        "", "", CSPreferences.readString(activity, "customer_id"), ""));
                break;
            }

            case R.id.changeCarCompany: {
                cabBottomDialog.show();
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        customer_id = CSPreferences.readString(activity, "customer_id");

        if (requestCode == 100 && resultCode == RESULT_OK) {

            Bundle extras = data.getExtras();
            photo = (Bitmap) extras.get("data");

            String base64Image = Utils.base64Encode(photo);
            dialog.show();

            try {
                ModelManager.getInstance().getImageUploadManager().uploadImageToServer(activity,
                        Operations.updateProfileImage(activity, customer_id, URLEncoder.encode(base64Image, "utf-8")));

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        /*else if (requestCode == 200 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            photo = (Bitmap) extras.get("data");

            String base64Image = Utils.base64Encode(photo);

            dialog.show();

            try {
                ModelManager.getInstance().getImageUploadManager().uploadImageToServer(activity,
                        Operations.updateProfileImage(activity, customer_id, URLEncoder.encode(base64Image, "utf-8")));

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }*/
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
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        showData();
    }

    public void showData() {
        home = CSPreferences.readString(activity, "add_home");
        work = CSPreferences.readString(activity, "add_work");

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
                if (str_pic.equals("camera")) {
                    Utils.openCamera(activity);
                } else if (str_pic.equals("gallery")) {
                    //Utils.openGallery(activity);
                    Toast.makeText(activity, "Working on it..", Toast.LENGTH_SHORT).show();
                }
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