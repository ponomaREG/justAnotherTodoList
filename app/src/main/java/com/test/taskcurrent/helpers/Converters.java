package com.test.taskcurrent.helpers;

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;

public class Converters {


    public static int getPixelFromDP(float dp, Context context){
        int marginInDp = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources()
                        .getDisplayMetrics());
        Log.d("MARGIN IN DP",marginInDp+" ");
        return marginInDp;
    }
}
