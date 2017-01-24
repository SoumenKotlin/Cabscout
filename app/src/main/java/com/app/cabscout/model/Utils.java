package com.app.cabscout.model;

/*
 * Created by rishav on 18/1/17.
 */

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    private static final String TAG = Utils.class.getSimpleName();
    private static String API_KEY = "AIzaSyDW4hwt4oKL-B64uDuwZ3LwEsoBLEcHwgw";

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
                strReturnedAddress.append(returnedAddress.getAddressLine(0)).append(", ");

                if (returnedAddress.getSubLocality() != null)
                    strReturnedAddress.append(returnedAddress.getSubLocality());

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
        String key = "key="+API_KEY;

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+key;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }

    public static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
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

}
