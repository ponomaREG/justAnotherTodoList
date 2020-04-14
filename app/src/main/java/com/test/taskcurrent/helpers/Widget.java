package com.test.taskcurrent.helpers;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import com.test.taskcurrent.R;
import com.test.taskcurrent.Services.ServiceRemoteForFactoryViews;

/**
 * Implementation of App Widget functionality.
 */
public class Widget extends AppWidgetProvider {

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
}

