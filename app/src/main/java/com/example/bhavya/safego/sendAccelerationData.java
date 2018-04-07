package com.example.bhavya.safego;

import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.example.bhavya.safego.data.accelerometerContract;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by bhavya on 7/4/18.
 */


public class sendAccelerationData extends AsyncTask<Cursor, Void, String> {

    private String email;
    private final String port = "6666";
    private final String ip = "192.168.1.12";

    public sendAccelerationData(String Email) {
        this.email = Email;
    }

    @Override
    protected String doInBackground(Cursor... allACCs) {

        Cursor allACC = allACCs[0];
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
                final String json = "{\"email\":\"" + email + "\",\"ax\":\"" + String.valueOf(x)+"\",\"ay\":\"" + String.valueOf(y)+"\",\"az\":\"" + String.valueOf(z) + "\",\"atime\":\"" + timeStamp+"\"}";
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

                allACC.moveToNext();
            }
        }

        allACC.close();
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Log.i("a-back-result", s);
    }
}
