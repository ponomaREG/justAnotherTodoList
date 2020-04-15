package com.test.taskcurrent.helpers;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.RemoteViews;

import com.test.taskcurrent.R;
import com.test.taskcurrent.Services.ServiceRemoteForFactoryViews;
import com.test.taskcurrent.taskOfCurrentDay;

import java.util.Objects;

/**
 * Implementation of App Widget functionality.
 */
public class Widget extends AppWidgetProvider {

    static void updateAppWidget(Context context,SharedPreferences sp, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        String widgetText = sp.getString(context.getResources().getString(R.string.widget_text)+appWidgetId,null);
        if(widgetText == null) return;
        views.setTextViewText(R.id.widget_tv_title,widgetText);
        setList(views,context,appWidgetId);
        setClickList(views,context);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        SharedPreferences sp = context.getSharedPreferences(context.getResources().getString(R.string.widget_pref),Context.MODE_PRIVATE);
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
        Intent adapter = new Intent(context, ServiceRemoteForFactoryViews.class);
        adapter.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,widgetID);
        Uri data = Uri.parse(adapter.toUri(Intent.URI_INTENT_SCHEME));
        adapter.setData(data);
        rv.setRemoteAdapter(R.id.widget_listview,adapter);
    }

    static void setClickList(RemoteViews rv, Context context){
        Intent intent = new Intent(context,Widget.class);
        intent.setAction(context.getResources().getString(R.string.widget_action_kistclick));
        PendingIntent pendIntent = PendingIntent.getBroadcast(context,0,intent,0);
        rv.setPendingIntentTemplate(R.id.widget_listview,pendIntent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if(Objects.equals(intent.getAction(), context.getResources().getString(R.string.widget_action_kistclick))){
            SharedPreferences sp = context.getSharedPreferences(context.getResources().getString(R.string.widget_pref),Context.MODE_PRIVATE);
            int pos = intent.getIntExtra(context.getResources().getString(R.string.widget_list_itemposition),-1);
            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,-1);
            int dayID = sp.getInt(context.getResources().getString(R.string.widget_dayID)+appWidgetId,-1);
            String date = sp.getString(context.getResources().getString(R.string.widget_text)+appWidgetId,null);
            if((pos!=-1)&&(appWidgetId!=-1)&&(dayID!=-1)&&(date!=null)){
                Intent intent_for_tasks = new Intent(context, taskOfCurrentDay.class);
                intent_for_tasks.putExtra(context.getResources().getString(R.string.intentExtraId),dayID);
                intent_for_tasks.putExtra(context.getResources().getString(R.string.intentExtraDate),date);
                intent_for_tasks.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent_for_tasks);
//                TaskStackBuilder.create(context).addNextIntent(new Intent(context,mainTasks.class)).addNextIntent(intent_for_tasks).startActivities();
        }
        }
    }
}

