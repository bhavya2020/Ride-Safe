package com.example.bhavya.safego;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.example.bhavya.safego.data.accelerometerContract;
import com.example.bhavya.safego.data.accelerometerDbHelper;
import com.example.bhavya.safego.data.gyroscopeContract;
import com.example.bhavya.safego.data.gyroscopeDbHelper;
import com.example.bhavya.safego.data.linearAccelerationContract;
import com.example.bhavya.safego.data.linearAccelerationDbHelper;
import com.example.bhavya.safego.data.magnetometerContract;
import com.example.bhavya.safego.data.magnetometerDbHelper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by bhavya on 7/4/18.
 */


public class stopMonitor extends AsyncTask<Void, Void, String> {

    private SQLiteDatabase accDB, laDB, magDB, gyrDB;
    private String email;
    private Context context;
    private final String port = "6666";
    private final String ip = "192.168.1.6";

    public stopMonitor(String Email, Context context) {
        this.email = Email;
        this.context = context;
    }

    @Override
    protected String doInBackground(Void... Void) {

        accelerometerDbHelper AdbHelper = new accelerometerDbHelper(context);
        accDB = AdbHelper.getWritableDatabase();
        gyroscopeDbHelper GdbHelper = new gyroscopeDbHelper(context);
        gyrDB = GdbHelper.getWritableDatabase();
        magnetometerDbHelper MdbHelper = new magnetometerDbHelper(context);
        magDB = MdbHelper.getWritableDatabase();
        linearAccelerationDbHelper LdbHelper = new linearAccelerationDbHelper(context);
        laDB = LdbHelper.getWritableDatabase();
        Cursor allACC = getAllValuesOfAccelerometer();
        Cursor allLA = getAllValuesOfLinearAcceleration();
        Cursor allMAG = getAllValuesOfMagnetometer();
        Cursor allGYR = getAllValuesOfGyroscope();

        String result = null;
        if (sendAccelerationData(allACC).equals("done"))
            if (sendLinearAccelerationData(allLA).equals("done"))
                if (sendGyroscopeData(allGYR).equals("done"))
                    if (sendMagnetometerData(allMAG).equals("done")) {
                        delete();
                        result = getResult();
                    }
        return result;
    }

    private String sendMagnetometerData(Cursor allMAG) {
        String Response = "notDone";
        if (allMAG.moveToFirst()) {
            while (!allMAG.isAfterLast()) {
                Float x = allMAG.getFloat(allMAG.getColumnIndex(magnetometerContract.magnetometer.X));
                Float y = allMAG.getFloat(allMAG.getColumnIndex(magnetometerContract.magnetometer.Y));
                Float z = allMAG.getFloat(allMAG.getColumnIndex(magnetometerContract.magnetometer.Z));
                String timeStamp = allMAG.getString(allMAG.getColumnIndex(magnetometerContract.magnetometer.COLUMN_TIMESTAMP));

                Log.i("mx", String.valueOf(x));
                Log.i("my", String.valueOf(y));
                Log.i("mz", String.valueOf(z));
                Log.i("mtime", timeStamp);
                final StringBuffer response = new StringBuffer();
                final String json = "{\"email\":\"" + email + "\",\"x\":\"" + String.valueOf(x) + "\",\"y\":\"" + String.valueOf(y) + "\",\"z\":\"" + String.valueOf(z) + "\",\"time\":\"" + timeStamp + "\"}";
                try {
                    URL url = new URL("http://" + ip + ":" + port + "/magnetometer");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoOutput(true);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    OutputStream os;
                    os = conn.getOutputStream();
                    os.write(json.getBytes());
                    os.flush();
                    os.close();
                    conn.getResponseCode();
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(conn.getInputStream()));
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    conn.disconnect();

                } catch (Exception e) {
                    //error occured
                    Log.d("error", e.toString());
                }

                Response = response.toString();
                allMAG.moveToNext();
            }
        }
        allMAG.close();
        return Response;
    }

    private String sendGyroscopeData(Cursor allGYR) {
        String Response = "notDone";
        if (allGYR.moveToFirst()) {
            while (!allGYR.isAfterLast()) {
                Float x = allGYR.getFloat(allGYR.getColumnIndex(gyroscopeContract.gyroscope.X));
                Float y = allGYR.getFloat(allGYR.getColumnIndex(gyroscopeContract.gyroscope.Y));
                Float z = allGYR.getFloat(allGYR.getColumnIndex(gyroscopeContract.gyroscope.Z));
                String timeStamp = allGYR.getString(allGYR.getColumnIndex(gyroscopeContract.gyroscope.COLUMN_TIMESTAMP));

                Log.i("gx", String.valueOf(x));
                Log.i("gy", String.valueOf(y));
                Log.i("gz", String.valueOf(z));
                Log.i("gtime", timeStamp);
                final StringBuffer response = new StringBuffer();
                final String json = "{\"email\":\"" + email + "\",\"x\":\"" + String.valueOf(x) + "\",\"y\":\"" + String.valueOf(y) + "\",\"z\":\"" + String.valueOf(z) + "\",\"time\":\"" + timeStamp + "\"}";
                try {
                    URL url = new URL("http://" + ip + ":" + port + "/gyroscope");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoOutput(true);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    OutputStream os;
                    os = conn.getOutputStream();
                    os.write(json.getBytes());
                    os.flush();
                    os.close();
                    conn.getResponseCode();
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(conn.getInputStream()));
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    conn.disconnect();

                } catch (Exception e) {
                    //error occured
                    Log.d("error", e.toString());
                }

                Response = response.toString();
                allGYR.moveToNext();
            }
        }
        allGYR.close();
        return Response;
    }

    private String sendLinearAccelerationData(Cursor allLA) {
        String Response = "notDone";
        if (allLA.moveToFirst()) {
            while (!allLA.isAfterLast()) {
                Float x = allLA.getFloat(allLA.getColumnIndex(linearAccelerationContract.linearAcceleration.X));
                Float y = allLA.getFloat(allLA.getColumnIndex(linearAccelerationContract.linearAcceleration.Y));
                Float z = allLA.getFloat(allLA.getColumnIndex(linearAccelerationContract.linearAcceleration.Z));
                String timeStamp = allLA.getString(allLA.getColumnIndex(linearAccelerationContract.linearAcceleration.COLUMN_TIMESTAMP));

                Log.i("lax", String.valueOf(x));
                Log.i("lay", String.valueOf(y));
                Log.i("laz", String.valueOf(z));
                Log.i("latime", timeStamp);
                final StringBuffer response = new StringBuffer();
                final String json = "{\"email\":\"" + email + "\",\"x\":\"" + String.valueOf(x) + "\",\"y\":\"" + String.valueOf(y) + "\",\"z\":\"" + String.valueOf(z) + "\",\"time\":\"" + timeStamp + "\"}";
                try {
                    URL url = new URL("http://" + ip + ":" + port + "/linearAcceleration");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoOutput(true);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    OutputStream os;
                    os = conn.getOutputStream();
                    os.write(json.getBytes());
                    os.flush();
                    os.close();
                    conn.getResponseCode();
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(conn.getInputStream()));
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    conn.disconnect();

                } catch (Exception e) {
                    //error occured
                    Log.d("error", e.toString());
                }

                Response = response.toString();
                allLA.moveToNext();
            }
        }
        allLA.close();
        return Response;
    }

    private String sendAccelerationData(Cursor allACC) {
        String Response = "notDone";
        if (allACC.moveToFirst()) {
            while (!allACC.isAfterLast()) {
                Float x = allACC.getFloat(allACC.getColumnIndex(accelerometerContract.accelerometer.X));
                Float y = allACC.getFloat(allACC.getColumnIndex(accelerometerContract.accelerometer.Y));
                Float z = allACC.getFloat(allACC.getColumnIndex(accelerometerContract.accelerometer.Z));
                String timeStamp = allACC.getString(allACC.getColumnIndex(accelerometerContract.accelerometer.COLUMN_TIMESTAMP));

                Log.i("ax", String.valueOf(x));
                Log.i("ay", String.valueOf(y));
                Log.i("az", String.valueOf(z));
                Log.i("atime", timeStamp);

                final StringBuffer response = new StringBuffer();
                final String json = "{\"email\":\"" + email + "\",\"x\":\"" + String.valueOf(x) + "\",\"y\":\"" + String.valueOf(y) + "\",\"z\":\"" + String.valueOf(z) + "\",\"time\":\"" + timeStamp + "\"}";
                try {
                    URL url = new URL("http://" + ip + ":" + port + "/accelerometer");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoOutput(true);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    OutputStream os;
                    os = conn.getOutputStream();
                    os.write(json.getBytes());
                    os.flush();
                    os.close();
                    conn.getResponseCode();
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(conn.getInputStream()));
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    conn.disconnect();

                } catch (Exception e) {
                    //error occured
                    Log.d("error", e.toString());
                }

                Response = response.toString();
                allACC.moveToNext();

            }
        }
        allACC.close();
        return Response;
    }

    private void delete() {
        long del = accDB.delete(accelerometerContract.accelerometer.TABLE_NAME, "1", null);
        Log.i("ACCdel", String.valueOf(del));
        del = laDB.delete(linearAccelerationContract.linearAcceleration.TABLE_NAME, "1", null);
        Log.i("LAdel", String.valueOf(del));
        del = magDB.delete(magnetometerContract.magnetometer.TABLE_NAME, "1", null);
        Log.i("MAGdel", String.valueOf(del));
        del = gyrDB.delete(gyroscopeContract.gyroscope.TABLE_NAME, "1", null);
        Log.i("GYRdel", String.valueOf(del));

    }

    private Cursor getAllValuesOfAccelerometer() {
        return accDB.query(accelerometerContract.accelerometer.TABLE_NAME, new String[]{accelerometerContract.accelerometer.X, accelerometerContract.accelerometer.Y, accelerometerContract.accelerometer.Z, accelerometerContract.accelerometer.COLUMN_TIMESTAMP}, null, null, null, null, null, null);
    }

    private Cursor getAllValuesOfLinearAcceleration() {
        return laDB.query(linearAccelerationContract.linearAcceleration.TABLE_NAME, new String[]{linearAccelerationContract.linearAcceleration.X, linearAccelerationContract.linearAcceleration.Y, linearAccelerationContract.linearAcceleration.Z, linearAccelerationContract.linearAcceleration.COLUMN_TIMESTAMP}, null, null, null, null, null, null);
    }

    private Cursor getAllValuesOfMagnetometer() {
        return magDB.query(magnetometerContract.magnetometer.TABLE_NAME, new String[]{magnetometerContract.magnetometer.X, magnetometerContract.magnetometer.Y, magnetometerContract.magnetometer.Z, magnetometerContract.magnetometer.COLUMN_TIMESTAMP}, null, null, null, null, null, null);
    }

    private Cursor getAllValuesOfGyroscope() {
        return gyrDB.query(gyroscopeContract.gyroscope.TABLE_NAME, new String[]{gyroscopeContract.gyroscope.X, gyroscopeContract.gyroscope.Y, gyroscopeContract.gyroscope.Z, gyroscopeContract.gyroscope.COLUMN_TIMESTAMP}, null, null, null, null, null, null);
    }

    private String getResult() {

        //get request to get csvMonitorResult Data
        return "done";
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Log.i("stop-monitor-result", s);
        //after getting the data display it on the monitor page
    }
}
