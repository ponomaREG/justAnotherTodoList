package com.test.taskcurrent.helpers;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

public class AnotherHelpers {

    public static void vibrateWhenClickIsLong(Context context){
        long duration = 100;
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if(vibrator.hasVibrator()) if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(duration,VibrationEffect.EFFECT_HEAVY_CLICK));
        }else vibrator.vibrate(duration);
    }

}
