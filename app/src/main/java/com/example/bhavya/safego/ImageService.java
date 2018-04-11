package com.example.bhavya.safego;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.net.URISyntaxException;
import java.util.Calendar;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by bhavya on 11/4/18.
 */

public class ImageService extends Service {
    private final static String ip = "192.168.43.170";
    private final static String port = "5555";

    Intent service;
    AlarmManager alarm;
    PendingIntent pintent;
    SharedPreferences mPrefs;
    private String email;

    @Nullable
    @Override

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        service = new Intent(getBaseContext(), CapPhoto.class);
        Log.i("service","starting service");
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        email=mPrefs.getString("uname","null");
        final Socket socket;
        try {
            socket = IO.socket("http://" + ip + ":" + port);
            Log.i("socket", socket.toString());
            socket.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    Log.i("ss", args[0].toString());
                }
            });
            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    socket.emit("uname",email);
                }
            });
            socket.on("click", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.i("clicking","images clicked");

                        startService(service);
                        Calendar cal = Calendar.getInstance();
                        cal.add(Calendar.SECOND, 5);
                        pintent = PendingIntent.getService(ImageService.this, 0, service, 0);
                        alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                        alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                                5 * 1000, pintent);
                    }
            });
            socket.on("stopClick", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.i("clicking","stop");
                    stopService(service);
                    if(alarm !=null)
                    {
                        alarm.cancel(pintent);
                    }
                }
            });
            socket.connect();

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }


        return START_STICKY;
    }
}
