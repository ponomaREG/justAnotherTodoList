package com.test.taskcurrent.Services;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import androidx.core.app.NotificationManagerCompat;

import com.test.taskcurrent.R;
import com.test.taskcurrent.helpers.BroadCastReceiverNotify;



public class ForegroundServiceForNotify extends Service {

    private int count = 0;

    public ForegroundServiceForNotify() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getStringExtra(getResources().getString(R.string.intentExtraAction));
        long time_set = intent.getLongExtra(getResources().getString(R.string.intentExtraTimeForNotify),SystemClock.elapsedRealtime());
        int id_day = intent.getIntExtra(getResources().getString(R.string.intentExtraId),-1);
        if (checkNotificationChannel()) {
            if (action != null) {
                AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
                Intent intent_broadcast = new Intent(this, BroadCastReceiverNotify.class);
                intent_broadcast.putExtra(getResources().getString(R.string.intentExtraRequestCode), startId);
                intent_broadcast.putExtra(getResources().getString(R.string.intentExtraId),id_day);
                intent_broadcast.putExtra(getResources().getString(R.string.intentExtraDate),intent.getStringExtra(getResources().getString(R.string.intentExtraDate)));
                PendingIntent pendIntent = PendingIntent.getBroadcast(this, count, intent_broadcast, 0);
                if (action.equals(getResources().getString(R.string.intentExtraActionAdd))) {
                    assert am != null;
                    setExactAndAllowWhileIdle(am, AlarmManager.RTC_WAKEUP, time_set, pendIntent);
                } else if (action.equals(getResources().getString(R.string.intentExtraActionDelete))) {
                    assert am != null;
                    am.cancel(pendIntent);
                }
            }
            count++;
        }else Log.d("ERROR","!");
        stopSelf(startId);
        return START_STICKY;
    }

    private boolean checkNotificationChannel(){
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            assert nm != null;
            if(nm.getNotificationChannel(getResources().getString(R.string.notify_channel_title)) == null) createNotificationChannel(this);
            return true;
        } return NotificationManagerCompat.from(this).areNotificationsEnabled();
    }

    private void createNotificationChannel(Context context){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    context.getResources().getString(R.string.notify_channel_id),
                    context.getResources().getString(R.string.notify_channel_title),
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription(context.getResources().getString(R.string.notify_channel_desc));
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager managerNT = context.getSystemService(NotificationManager.class);
            assert managerNT != null;
            managerNT.createNotificationChannel(channel);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public static void setExactAndAllowWhileIdle(AlarmManager alarmManager, int type, long triggerAtMillis, PendingIntent operation) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M){
            alarmManager.setExactAndAllowWhileIdle(type, triggerAtMillis, operation);
        } else {
            alarmManager.setExact(type, triggerAtMillis, operation);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}

