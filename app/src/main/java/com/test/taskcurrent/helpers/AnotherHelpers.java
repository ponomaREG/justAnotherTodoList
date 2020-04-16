package com.test.taskcurrent.helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;


import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AnotherHelpers {

    public static void vibrateWhenClickIsLong(Context context){
        long duration = 100;
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        assert vibrator != null;
        if(vibrator.hasVibrator()) if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(duration,VibrationEffect.EFFECT_HEAVY_CLICK));
        }else vibrator.vibrate(duration);
    }


    public static long getTimeInMillisOfYesterdayDayByMillisDay(long day_time_unix,int hour){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(day_time_unix-1000*60*60*(24-hour));
        //Log.d("TIME",String.format("%d.%d.%d %d:%d",calendar.get(calendar.DAY_OF_MONTH),calendar.get(Calendar.MONTH),calendar.get(Calendar.YEAR),calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE)));
        return day_time_unix-1000*60*60*(24-hour);
    }

    public static long getTimeInMillisOfDayByMillis(long day_time_unix,int hour){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(day_time_unix-1000*60*60*(24-hour));
        //Log.d("TIME",String.format("%d.%d.%d %d:%d",calendar.get(calendar.DAY_OF_MONTH),calendar.get(Calendar.MONTH),calendar.get(Calendar.YEAR),calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE)));
        return day_time_unix+1000*60*60*hour;
    }

    @SuppressLint("SimpleDateFormat")
    public static String convertLongToDate(long time){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return new SimpleDateFormat("dd.MM.yyyy").format(calendar.getTime());
    }

}
