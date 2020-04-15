package com.test.taskcurrent.helpers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;

import androidx.core.app.NotificationCompat;

import com.test.taskcurrent.R;
import com.test.taskcurrent.taskOfCurrentDay;


public class BroadCastReceiverNotify extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        assert nm != null;
        nm.notify(intent.getIntExtra(context.getResources().getString(R.string.intentExtraId),0),getNotification(
                context,
                context.getResources().getString(R.string.notify_channel_id),
                intent.getStringExtra(context.getResources().getString(R.string.intentExtraDate)),
                intent.getIntExtra(context.getResources().getString(R.string.intentExtraId),1))
        );
    }


    public static Notification getNotification(Context context,String default_not_channel,String date,int id){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, default_not_channel);
        Intent intent = new Intent(context, taskOfCurrentDay.class);
        intent.putExtra(context.getResources().getString(R.string.intentExtraDate),date);
        intent.putExtra(context.getResources().getString(R.string.intentExtraId),id);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,id,intent,0);
        builder.setContentTitle(context.getResources().getString(R.string.notify_morning_title))
                .setContentText(context.getResources().getString(R.string.notify_morning_content))
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_launcher))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setShowWhen(true);
        return builder.build();

    }
}
