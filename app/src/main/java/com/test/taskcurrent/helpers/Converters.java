package com.test.taskcurrent.helpers;

import android.content.Context;
import android.database.Cursor;
import android.util.TypedValue;

public class Converters {


    public static int getPixelFromDP(float dp, Context context){
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources()
                        .getDisplayMetrics());
    }

    static String[] getStringsArrayFromCursor(Cursor c,String columnName){
        c.moveToFirst();
        String[] strings = new String[c.getCount()];
        for(int i = 0;i<c.getCount();i++){
            strings[i] = c.getString(c.getColumnIndex(columnName));
            c.moveToNext();
        }
        return strings;
    }
}
