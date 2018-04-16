package com.example.bhavya.safego;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationManagerCompat;

/**
 * Created by bhavya on 16/4/18.
 */

public class notificationReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences mPrefs= PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = mPrefs.edit();
        ed.putBoolean("isMonitoring", false);
        ed.putBoolean("isMonitoringEnabled",false);
        ed.apply();
        String Email=intent.getStringExtra("email");
        Intent service = new Intent(context, startMonitorService.class);
        context.stopService(service);
        new stopMonitor(Email, context).execute();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        notificationManager.cancel(2002);
        Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.sendBroadcast(it);
    }
}
