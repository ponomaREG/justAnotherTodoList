package com.test.taskcurrent.Services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

import androidx.core.app.NotificationManagerCompat;

import com.test.taskcurrent.helpers.BroadCastReceiverNotify;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class MyIntentService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "com.test.taskcurrent.Services.action.FOO";
    private static final String ACTION_BAZ = "com.test.taskcurrent.Services.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.test.taskcurrent.Services.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.test.taskcurrent.Services.extra.PARAM2";

    public static String CHANNEL_ID = "NotifyChannel", CHANNEL_TITLE = "Notify";

    private NotificationManager managerNT;
    private int count = 0;

    public MyIntentService() {
        super("MyIntentService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, MyIntentService.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, MyIntentService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String action = intent.getStringExtra("action");
            Log.d("COUNT SERVICE",String.valueOf(count));
            if (createNotificationChannel(this)) {
                if (action != null)
                    if (action.equals("add")) {
                        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
                        Intent intent_broadcast = new Intent(this, BroadCastReceiverNotify.class);
                        intent_broadcast.putExtra("number", count);
                        PendingIntent pendIntent = PendingIntent.getBroadcast(this, count, intent_broadcast, PendingIntent.FLAG_UPDATE_CURRENT);
                        assert am != null;
                        am.set(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + count * 2000, pendIntent);
                    }
                count++;
            }
        }
    }
//    private boolean checkNotificationChannel(){
//        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            assert nm != null;
//            if(nm.getNotificationChannel(CHANNEL_ID) == null) createNotificationChannel(this);
//            return true;
//        } return NotificationManagerCompat.from(this).areNotificationsEnabled();
//    }

    private boolean createNotificationChannel(Context context){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_TITLE,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notify channel");
            managerNT = context.getSystemService(NotificationManager.class);
            assert managerNT != null;
            managerNT.createNotificationChannel(channel);
            Log.d("ONCREATENORIFYCHANNEL","!");
        }
        return true;
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
