package com.example.bhavya.safego;

import android.Manifest;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.example.bhavya.safego.data.accelerometerContract;
import com.example.bhavya.safego.data.accelerometerDbHelper;
import com.example.bhavya.safego.data.gyroscopeContract;
import com.example.bhavya.safego.data.gyroscopeDbHelper;
import com.example.bhavya.safego.data.linearAccelerationContract;
import com.example.bhavya.safego.data.linearAccelerationDbHelper;
import com.example.bhavya.safego.data.magnetometerContract;
import com.example.bhavya.safego.data.magnetometerDbHelper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Date;
import java.util.concurrent.Executor;

/**
 * Created by bhavya on 7/4/18.
 */

public class startMonitorService extends Service implements SensorEventListener {
    private SQLiteDatabase accDB, laDB, magDB, gyrDB;
    private  double longitude,latitude;
    private FusedLocationProviderClient mFusedLocationClient;

    SensorManager mSensorManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        accelerometerDbHelper AdbHelper = new accelerometerDbHelper(this);
        accDB = AdbHelper.getWritableDatabase();
        gyroscopeDbHelper GdbHelper = new gyroscopeDbHelper(this);
        gyrDB = GdbHelper.getWritableDatabase();
        magnetometerDbHelper MdbHelper = new magnetometerDbHelper(this);
        magDB = MdbHelper.getWritableDatabase();
        linearAccelerationDbHelper LdbHelper = new linearAccelerationDbHelper(this);
        laDB = LdbHelper.getWritableDatabase();
        Sensor accelerometer = null, linearAcceleration = null, gyroscope = null, magnetometer = null;

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        linearAcceleration = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        gyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        if (accelerometer != null)
            mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        if (linearAcceleration != null)
            mSensorManager.registerListener(this, linearAcceleration, SensorManager.SENSOR_DELAY_NORMAL);
        if (gyroscope != null)
            mSensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        if (magnetometer != null)
            mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
        Log.i("service","started");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSensorManager != null)
            mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        Log.i("event","listened");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {

                            longitude=location.getLongitude();
                            latitude=location.getLatitude();

                            Log.i("latitude", String.valueOf(location.getLatitude()));
                            Log.i("longitude", String.valueOf(location.getLongitude()));
                            // Logic to handle location object
                        }
                    }
                });


        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long temp = insertAccValues(event.values[0], event.values[1], event.values[2],latitude,longitude);
            Log.i("insertACC", String.valueOf(temp));
        }
        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            long temp = insertLAValues(event.values[0], event.values[1], event.values[2],latitude,longitude);
            Log.i("insertLA", String.valueOf(temp));
        }
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            long temp = insertMagValues(event.values[0], event.values[1], event.values[2],latitude,longitude);
            Log.i("insertMAG", String.valueOf(temp));
        }
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            long temp = insertGyrValues(event.values[0], event.values[1], event.values[2],latitude,longitude);
            Log.i("insertGYR", String.valueOf(temp));
        }

    }
    public long insertLAValues(float x, float y, float z,double latitude,double longitude) {
        ContentValues cv = new ContentValues();
        cv.put(linearAccelerationContract.linearAcceleration.X, x);
        cv.put(linearAccelerationContract.linearAcceleration.Y, y);
        cv.put(linearAccelerationContract.linearAcceleration.Z, z);
        cv.put(linearAccelerationContract.linearAcceleration.LATITUDE, latitude);
        cv.put(linearAccelerationContract.linearAcceleration.LONGITUDE, longitude);
        //  DateFormat df=new SimpleDateFormat("mm-dd hh:mm:ss");
        Date date = new Date();
        cv.put(linearAccelerationContract.linearAcceleration.COLUMN_TIMESTAMP, String.valueOf(date));

        return (laDB.insert(linearAccelerationContract.linearAcceleration.TABLE_NAME, null, cv));
    }

    public long insertMagValues(float x, float y, float z,double latitude,double longitude) {
        ContentValues cv = new ContentValues();
        cv.put(magnetometerContract.magnetometer.X, x);
        cv.put(magnetometerContract.magnetometer.Y, y);
        cv.put(magnetometerContract.magnetometer.Z, z);
        cv.put(magnetometerContract.magnetometer.LATITUDE, latitude);
        cv.put(magnetometerContract.magnetometer.LONGITUDE, longitude);

        //  DateFormat df=new SimpleDateFormat("mm-dd hh:mm:ss");
        Date date = new Date();
        cv.put(magnetometerContract.magnetometer.COLUMN_TIMESTAMP, String.valueOf(date));

        return (magDB.insert(magnetometerContract.magnetometer.TABLE_NAME, null, cv));
    }

    public long insertGyrValues(float x, float y, float z,double latitude,double longitude) {
        ContentValues cv = new ContentValues();
        cv.put(gyroscopeContract.gyroscope.X, x);
        cv.put(gyroscopeContract.gyroscope.Y, y);
        cv.put(gyroscopeContract.gyroscope.Z, z);
        cv.put(gyroscopeContract.gyroscope.LATITUDE, latitude);
        cv.put(gyroscopeContract.gyroscope.LONGITUDE, longitude);

        //  DateFormat df=new SimpleDateFormat("mm-dd hh:mm:ss");
        Date date = new Date();
        cv.put(gyroscopeContract.gyroscope.COLUMN_TIMESTAMP, String.valueOf(date));

        return (gyrDB.insert(gyroscopeContract.gyroscope.TABLE_NAME, null, cv));
    }

    public long insertAccValues(float x, float y, float z,double latitude,double longitude) {
        ContentValues cv = new ContentValues();
        cv.put(accelerometerContract.accelerometer.X, x);
        cv.put(accelerometerContract.accelerometer.Y, y);
        cv.put(accelerometerContract.accelerometer.Z, z);
        cv.put(accelerometerContract.accelerometer.LATITUDE, latitude);
        cv.put(accelerometerContract.accelerometer.LONGITUDE, longitude);

        //  DateFormat df=new SimpleDateFormat("mm-dd hh:mm:ss");
        Date date = new Date();
        cv.put(accelerometerContract.accelerometer.COLUMN_TIMESTAMP, String.valueOf(date));

        return (accDB.insert(accelerometerContract.accelerometer.TABLE_NAME, null, cv));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
