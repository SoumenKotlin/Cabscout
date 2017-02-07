package com.app.cabscout.model;

/*
 * Created by rishav on 18/1/17.
 */

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.app.cabscout.R;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    private static final String TAG = Utils.class.getSimpleName();

    public static boolean emailValidator(String email) {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static String getCompleteAddressString(Context context, double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

              /*  for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i));
                }*/
                strReturnedAddress.append(returnedAddress.getAddressLine(0));

                if (returnedAddress.getSubLocality() != null)
                    strReturnedAddress.append(", ").append(returnedAddress.getSubLocality());

                strAdd = strReturnedAddress.toString();
                Log.e(TAG, "Current address-- "+strReturnedAddress.toString());
            } else {
                Log.e(TAG, "No address returned");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strAdd;
    }

    public static String getDirectionsUrl(LatLng origin, LatLng dest){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Sensor enabled
        String API_KEY = "AIzaSyCpxjdSXb9v61fadm7mUsmxrggE3KMCQD0";
        String key = "key="+ API_KEY;

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+key;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }

    public static String dateConverter(int year, int monthOfYear, int dayOfMonth) {
        String monthFormat = null;
        String dayFormat = null;

        Calendar calendar = new GregorianCalendar(year, monthOfYear, dayOfMonth);
        int result = calendar.get(Calendar.DAY_OF_WEEK);
        int m = calendar.get(Calendar.MONTH);

        switch (result) {
            case Calendar.SUNDAY:
                dayFormat = "Sun";
                break;
            case Calendar.MONDAY:
                dayFormat = "Mon";
                break;
            case Calendar.TUESDAY:
                dayFormat = "Tue";
                break;
            case Calendar.WEDNESDAY:
                dayFormat = "Wed";
                break;
            case Calendar.THURSDAY:
                dayFormat = "Thu";
                break;
            case Calendar.FRIDAY:
                dayFormat = "Fri";
                break;
            case Calendar.SATURDAY:
                dayFormat = "Sat";
                break;

        }

        switch (m) {
            case 0:
                monthFormat = "Jan";
                break;

            case 1:
                monthFormat = "Feb";
                break;

            case 2:
                monthFormat = "Mar";
                break;

            case 3:
                monthFormat = "Apr";
                break;

            case 4:
                monthFormat = "May";
                break;

            case 5:
                monthFormat = "Jun";
                break;

            case 6:
                monthFormat = "Jul";
                break;

            case 7:
                monthFormat = "Aug";
                break;

            case 8:
                monthFormat = "Sep";
                break;

            case 9:
                monthFormat = "Oct";
                break;

            case 10:
                monthFormat = "Nov";
                break;

            case 11:
                monthFormat = "Dec";
                break;
        }

        return monthFormat+","+dayFormat+ " "+ dayOfMonth;
    }

    public static String timeConverter(int minute, int hourOfDay) {
        String time = null;
        String min;

        if (minute < 10) {
            min = "0"+minute;
        }
        else {
            min = String.valueOf(minute);
        }

        if(hourOfDay>12) {
            time = "0"+String.valueOf(hourOfDay-12)+ ":"+(min+" pm");
            Log.e(TAG, time);
        } else if(hourOfDay==12) {
            time = "12"+ ":"+(min+" pm");
            Log.e(TAG, time);
        } else if(hourOfDay<12) {
            if(hourOfDay!=0) {
                time = "0"+String.valueOf(hourOfDay) + ":" + (min + " am");
                Log.e(TAG, time);
            } else {
                time = "12" + ":" + (min + " am");
                Log.e(TAG, time);
            }
        }

        return time;
    }

    public static double round(double d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Double.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        Log.e(TAG, "distance round up-- "+bd.doubleValue());
        return bd.doubleValue();
    }

    public static double convertKmToMi(double kilometers) {
        // Assume there are 0.621 miles in a kilometer.
        double miles = kilometers * 0.621;
        Log.e(TAG, "distance im miles-- "+miles);
        return miles;
    }

    @SuppressWarnings("ConstantConditions")
    public static Dialog createDialog(Context context) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        return dialog;
    }

    @SuppressWarnings("ConstantConditions")
    public static Dialog customProgressDialog(Context context, String message) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_progress_dialog);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView customMessage = (TextView)dialog.findViewById(R.id.customMessage);
        customMessage.setText(message);

        return dialog;
    }

    public static String base64Encode(Bitmap bm) {

        //Bitmap bm = BitmapFactory.decodeFile(picturePath);
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 90, bao);
        byte[] ba = bao.toByteArray();
        String ba1 = Base64.encodeToString(ba, Base64.DEFAULT);

        Log.e("base64", "-----" + ba1);

        return ba1;
    }

    public Bitmap base64decode(String base64) {
        try {
            byte[] encodeByte = Base64.decode(base64, Base64.DEFAULT);
            Bitmap bitmap;
            bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public static void openCamera(Activity context) {

        // Check Camera
       /* if (context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // Open default camera
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

            // start the image capture Intent
            context.startActivityForResult(intent, 100);

        } else {
            Toast.makeText(context, "Camera not supported", Toast.LENGTH_LONG).show();
        }*/

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivityForResult(takePictureIntent, 100);
        }
    }

    public static void openGallery(Activity context) {

        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        context.startActivityForResult(galleryIntent, 200);
    }

    public static String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key= itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));
        }
        return result.toString();
    }

    public static void makeSnackBar(Context context, View view, String message) {
        Snackbar snackbar;
        snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(Color.RED);
        TextView textView = (TextView)snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

}
