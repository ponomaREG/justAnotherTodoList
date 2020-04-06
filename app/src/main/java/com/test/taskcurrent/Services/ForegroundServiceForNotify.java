package com.test.taskcurrent.Services;

import android.app.AlarmManager;
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

import com.test.taskcurrent.helpers.BroadCastReceiverNotify;
import com.test.taskcurrent.helpers.DBHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class ForegroundServiceForNotify extends Service {

    public static String CHANNEL_ID = "NotifyChannel", CHANNEL_TITLE = "Notify";

    private NotificationManager managerNT;
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
        Log.d("ELAPSED",SystemClock.elapsedRealtime()+" ");
        Log.d("ELAPSED",time_set+" ");
        int id_day = intent.getIntExtra("id_day",-1);
//        DBHelper dbhelper = intent.getParcelableExtra("dbhelper");
        if (checkNotificationChannel()) {
            if (action != null) {
                AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
                Intent intent_broadcast = new Intent(this, BroadCastReceiverNotify.class);
                intent_broadcast.putExtra("request_code", startId);
                intent_broadcast.putExtra("id_day",id_day);
                PendingIntent pendIntent = PendingIntent.getBroadcast(this, count, intent_broadcast, 0);
                if (action.equals("add")) {
                    assert am != null;
                    Calendar calendar = Calendar.getInstance();
                    try {
                        calendar.setTime(new SimpleDateFormat("dd.MM.yy HH:mm:ss").parse("07.04.20 02:09:00"));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    setExactAndAllowWhileIdle(am, AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendIntent);
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
            if(nm.getNotificationChannel(CHANNEL_ID) == null) createNotificationChannel(this);
            return true;
        } return NotificationManagerCompat.from(this).areNotificationsEnabled();
    }

    private void createNotificationChannel(Context context){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_TITLE,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Notify channel");
            managerNT = context.getSystemService(NotificationManager.class);
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
        } else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            alarmManager.setExact(type, triggerAtMillis, operation);
        } else {
            alarmManager.set(type, triggerAtMillis, operation);
        }
    }

    @Override
    public void onDestroy() {
        Log.d("ONDESTROY","!");
        super.onDestroy();
    }


}

