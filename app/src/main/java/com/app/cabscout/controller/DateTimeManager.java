package com.app.cabscout.controller;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.app.cabscout.model.Constants;
import com.app.cabscout.model.Event;
import com.app.cabscout.model.Utils;

import org.greenrobot.eventbus.EventBus;

import java.util.Calendar;

/*
 * Created by rishav on 24/1/17.
 */

public class DateTimeManager {

    private static String TAG = DateTimeManager.class.getSimpleName();

    public void getDate(Context context) {


        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        String date = Utils.dateConverter(year, monthOfYear, dayOfMonth);
                        Log.e(TAG, "date--"+date);

                        EventBus.getDefault().post(new Event(Constants.DATE_SUCCESS, date));

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }


    public void getTime(Context context) {

        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        String time = Utils.timeConverter(minute, hourOfDay);

                        EventBus.getDefault().post(new Event(Constants.TIME_SUCCESS, time));
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }
}
