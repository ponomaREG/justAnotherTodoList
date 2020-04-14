package com.test.taskcurrent.helpers;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.Toast;

import com.test.taskcurrent.R;

import java.util.ArrayList;

public class FactoryForWidget implements RemoteViewsService.RemoteViewsFactory {
    private ArrayList<String> tasks;
    private Context context;
    private int widgetID;
    private DBHelper dbhelper;
    private SharedPreferences sp;

    public FactoryForWidget(Context context, Intent intent){
        this.context = context;
        this.widgetID = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,AppWidgetManager.INVALID_APPWIDGET_ID);
    }


    @Override
    public void onCreate() {
        tasks = new ArrayList<>();
        dbhelper = new DBHelper(context);
        sp = context.getSharedPreferences(Config.WIDGET_PREF,Context.MODE_PRIVATE);
    }

    @Override
    public void onDataSetChanged() {
        tasks.clear();
        int dayID = sp.getInt(Config.WIDGET_DAY_ID+widgetID,-1);
        Log.d("DAYID",dayID+"");
        if(dayID!=-1) {
            Cursor c = dbhelper.getTasksByID(dayID);
            for(int i = 0;i<c.getCount();i++) {
                tasks.add(c.getString(c.getColumnIndex("task")));
                c.moveToNext();
            }
        }else{
            Toast.makeText(context,"У Вас нет заданий",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return tasks.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_list_item);
        rv.setTextViewText(R.id.widget_list_item_tv,tasks.get(position));
        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
