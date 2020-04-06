package com.test.taskcurrent.helpers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.test.taskcurrent.R;
import com.test.taskcurrent.Services.ForegroundServiceForNotify;
import com.test.taskcurrent.mainTasks;

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
        nm.notify(intent.getIntExtra("id_notif",0),getNotification(context, ForegroundServiceForNotify.CHANNEL_ID,String.valueOf(intent.getIntExtra("number",-1))));
    }


    public static Notification getNotification(Context context,String default_not_channel,String content){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, default_not_channel);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(SystemClock.elapsedRealtime());
        builder.setContentTitle(CHANNEL_TITLE)
                .setContentText(content)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setShowWhen(true);
        return builder.build();

    }
}
