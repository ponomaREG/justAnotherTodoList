package com.test.taskcurrent.helpers;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.test.taskcurrent.R;
import com.test.taskcurrent.Services.ServiceRemoteForFactoryViews;
import com.test.taskcurrent.mainTasks;
import com.test.taskcurrent.taskOfCurrentDay;

import java.util.Arrays;
import java.util.Objects;

/**
 * Implementation of App Widget functionality.
 */
public class Widget extends AppWidgetProvider {
    static final String ACTION_ON_CLICK = "com.test.taskcurrent.widget.itemonclick";
    final static String ITEM_POSITION = "item_position";

    static void updateAppWidget(Context context,SharedPreferences sp, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        String widgetText = sp.getString(Config.WIDGET_TEXT+appWidgetId,null);
        if(widgetText == null) return;
//        views.setTextViewText(R.id.appwidget_text, widgetText);
        // Instruct the widget manager to update the widget
        views.setTextViewText(R.id.widget_tv_title,widgetText);
        setList(views,context,appWidgetId);
        setClickList(views,context,appWidgetId);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        SharedPreferences sp = context.getSharedPreferences(Config.WIDGET_PREF,Context.MODE_PRIVATE);
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, sp,appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    static void setList(RemoteViews rv,Context context,int widgetID){
        Log.d("SETLIST0","!@3");
        Intent adapter = new Intent(context, ServiceRemoteForFactoryViews.class);
        adapter.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,widgetID);
        Uri data = Uri.parse(adapter.toUri(Intent.URI_INTENT_SCHEME));
        adapter.setData(data);
        Log.d("WIDGET ID ",widgetID+"");
        rv.setRemoteAdapter(R.id.widget_listview,adapter);
    }

    static void setClickList(RemoteViews rv, Context context, int appWidgetId){
        Intent intent = new Intent(context,Widget.class);
//        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,appWidgetId);
        intent.setAction(ACTION_ON_CLICK);
        PendingIntent pendIntent = PendingIntent.getBroadcast(context,0,intent,0);
        rv.setPendingIntentTemplate(R.id.widget_listview,pendIntent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.d("asd","1");
        if(Objects.equals(intent.getAction(), ACTION_ON_CLICK)){
            Log.d("asd","1");
            SharedPreferences sp = context.getSharedPreferences(Config.WIDGET_PREF,Context.MODE_PRIVATE);
            int pos = intent.getIntExtra(ITEM_POSITION,-1);
            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,-1);
            int dayID = sp.getInt(Config.WIDGET_DAY_ID+appWidgetId,-1);
            String date = sp.getString(Config.WIDGET_TEXT+appWidgetId,null);
            Log.d("p",pos+"");
            Log.d("a",appWidgetId+"");
            Log.d("d",dayID+"");
            Log.d("keyset", Arrays.toString(intent.getExtras().keySet().toArray()));
            Log.d("date",date+"");
            if((pos!=-1)&&(appWidgetId!=-1)&&(dayID!=-1)&&(date!=null)){
                Intent intent_for_tasks = new Intent(context, taskOfCurrentDay.class);
                intent_for_tasks.putExtra("id",dayID);
                intent_for_tasks.putExtra("date",date);
                intent_for_tasks.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                PendingIntent pend_intent = PendingIntent.getActivity(context,0,intent_for_tasks,0);
////                RemoteViews rv = new RemoteViews(context.getPackageName(),R.layout.widget_layout);
////                rv.setOnClickPendingIntent(R.id.widget_listview,pend_intent);
                TaskStackBuilder.create(context).addNextIntent(new Intent(context,mainTasks.class)).addNextIntent(intent_for_tasks).startActivities();
//                context.startActivity(intent_for_tasks);
        }
        }
    }
}

