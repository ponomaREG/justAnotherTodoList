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
        Log.d("ONCREATE","!");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getStringExtra("action");
        long time_set = intent.getLongExtra("time_set_notif",SystemClock.elapsedRealtime());
        int id_day = intent.getIntExtra("id",-1);
//        DBHelper dbhelper = intent.getParcelableExtra("dbhelper");
        if (checkNotificationChannel()) {
            if (action != null) {
                AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
                Intent intent_broadcast = new Intent(this, BroadCastReceiverNotify.class);
                intent_broadcast.putExtra("request_code", startId);
                intent_broadcast.putExtra("id",id_day);
                intent_broadcast.putExtra("date",intent.getStringExtra("date"));
                PendingIntent pendIntent = PendingIntent.getBroadcast(this, count, intent_broadcast, 0);
                if (action.equals("add")) {
                    assert am != null;
                    setExactAndAllowWhileIdle(am, AlarmManager.RTC_WAKEUP, time_set, pendIntent);
                } else if (action.equals("delete")) {
                    assert am != null;
                    am.cancel(pendIntent);
                }
            }
            count++;
//        long input_time = intent.getLongExtra("time",0);
//        String input = intent.getStringExtra("title");
//        int input_id = intent.getIntExtra("id_day",-1);
//        if(input_id == -1) onDestroy();
//
////        createNotificationChannel();
//        t = new Thread(() -> {
//
//        });
//        Intent intent_pend = new Intent(this, taskOfCurrentDay.class).putExtra("id",input_id);
//        PendingIntent pendInt = PendingIntent.getActivity(this, 0, intent_pend,0);
//        Calendar calendar = Calendar.getInstance();
//        calendar.set(2020, 3, 4,22,25);
//        Notification notify = new NotificationCompat.Builder(this, CHANNEL_ID)
//                .setContentTitle(CHANNEL_TITLE)
//                .setContentText(input)
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setShowWhen(true)
//                .setContentIntent(pendInt)
//                .build();
//        managerNT.notify(1,notify);
//        stopSelf(startId);
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
            channel.setDescription("Notify channel");
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager managerNT = context.getSystemService(NotificationManager.class);
            assert managerNT != null;
            managerNT.createNotificationChannel(channel);
            Log.d("ONCREATENORIFYCHANNEL","!");
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

