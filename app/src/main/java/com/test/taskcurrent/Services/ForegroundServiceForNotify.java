package com.test.taskcurrent.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.test.taskcurrent.R;
import com.test.taskcurrent.taskOfCurrentDay;

import java.util.Calendar;

public class ForegroundServiceForNotify extends Service {

    public static String CHANNEL_ID = "NotifyChannel", CHANNEL_TITLE = "Notify";

    private NotificationManager managerNT;

    public ForegroundServiceForNotify() {
    }

    @Override
    public void onCreate() {
        Log.d("ONCREATE","!");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("ONSTARTCOMMAND","!");
        long input_time = intent.getLongExtra("time",0);
        String input = intent.getStringExtra("title");
        int input_id = intent.getIntExtra("id_day",-1);
        if(input_id == -1) onDestroy();

        createNotificationChannel();


        Intent intent_pend = new Intent(this, taskOfCurrentDay.class).putExtra("id",input_id);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0, intent_pend,0);
        Calendar calendar = Calendar.getInstance();
        calendar.set(2020, 3, 4,22,25);
        Notification notify = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(CHANNEL_TITLE)
                .setContentText(input)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setShowWhen(true)
                .setContentIntent(pendInt)
                .build();
        managerNT.notify(1,notify);
        stopSelf(startId);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        Log.d("ONDESTROY","!");
        super.onDestroy();
    }

    private void createNotificationChannel(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                        CHANNEL_ID,
                        CHANNEL_TITLE,
                        NotificationManager.IMPORTANCE_HIGH
                );
                channel.setDescription("Notify channel");
                managerNT = getSystemService(NotificationManager.class);
            assert managerNT != null;
            managerNT.createNotificationChannel(channel);
            Log.d("ONCREATENORIFYCHANNEL","!");
        }
    }
}
