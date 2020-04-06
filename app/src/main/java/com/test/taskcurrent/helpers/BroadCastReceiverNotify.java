package com.test.taskcurrent.helpers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.test.taskcurrent.R;
import com.test.taskcurrent.Services.ForegroundServiceForNotify;
import com.test.taskcurrent.mainTasks;
import com.test.taskcurrent.taskOfCurrentDay;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class BroadCastReceiverNotify extends BroadcastReceiver {

    private static String CHANNEL_ID = "DEFAULT ID OF CHANNEL", CHANNEL_TITLE = "Notify";
    private NotificationManager managerNT;
    public static String NOTIFICATION_ID = "notification-id" ;
    public static String NOTIFICATION = "notification" ;


    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        assert nm != null;
        Log.d("NOTIFY IN BROADCAST","1");
        nm.notify(intent.getIntExtra("id",0),getNotification(
                context,
                ForegroundServiceForNotify.CHANNEL_ID,
                intent.getStringExtra("date"),
                intent.getIntExtra("id",1))
        );
    }


    public static Notification getNotification(Context context,String default_not_channel,String date,int id){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, default_not_channel);
        Intent intent = new Intent(context, taskOfCurrentDay.class);
        intent.putExtra("date",date);
        intent.putExtra("id",id);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,id,intent,0);
        builder.setContentTitle(CHANNEL_TITLE)
                .setContentText("ПРОВЕРЬ ЗАДАЧИ НА СЕГОДНЯ")
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setShowWhen(true);
        return builder.build();

    }
}
