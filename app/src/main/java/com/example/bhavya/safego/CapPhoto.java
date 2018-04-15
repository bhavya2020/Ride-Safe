package com.example.bhavya.safego;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.util.Base64;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Created by bhavya on 11/4/18.
 */

public class CapPhoto extends Service
{


    private SurfaceHolder sHolder;
    private Camera mCamera;
    private Camera.Parameters parameters;
    private String email;
    private SharedPreferences mPrefs;
    @Override
    public void onCreate()
    {
        super.onCreate();
        Log.i("CAM", "start");
    }
    @RequiresApi(api = Build.VERSION_CODES.M)

    @Override
    public int onStartCommand (Intent intent, int flags, int startId) {

        Log.i("cap-photo","started");
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        email=mPrefs.getString("uname","null");
        Toast.makeText(this,"service running "+startId,Toast.LENGTH_LONG).show();
        int cameras=Camera.getNumberOfCameras();
        if (cameras >= 2) {


            try {
                int id= Camera.CameraInfo.CAMERA_FACING_FRONT;
                mCamera = Camera.open(id);
                // mCamera=null;
            } catch (Exception e)
            {
                Toast.makeText(this,"err in open "+e.toString(), Toast.LENGTH_LONG).show();
                Log.i("err",e.toString());
            }
        }

        if (Camera.getNumberOfCameras() < 2) {

            mCamera = Camera.open(); }

        if(mCamera !=null) {
            SurfaceTexture st=new SurfaceTexture(0);
            try {
                mCamera.setPreviewTexture(st);
                parameters = mCamera.getParameters();
                mCamera.setParameters(parameters);
                mCamera.startPreview();
                Log.i("START","STARTED");
                mCamera.takePicture(null,null,null,mCall);

            } catch (Exception e) {


                e.printStackTrace();
            }

        }
        return  START_STICKY;
    }


    Camera.PictureCallback mCall = new Camera.PictureCallback()
    {
        public void onPictureTaken(final byte[] data, Camera camera)
        {
            final StringBuffer response=new StringBuffer();
            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    byte[] postData=null;
                    try  {
                        URL url = null;
                        try {
                            url = new URL("http://192.168.43.170:5555/click/"+email);
                            Log.i("data","data-sending");
                        } catch (MalformedURLException e) {
                            Log.i("err",e.toString());
                            e.printStackTrace();
                        }
                        HttpURLConnection conn = null;
                        try {
                            assert url != null;
                            conn = (HttpURLConnection) url.openConnection();
                        } catch (IOException e) {
                            Log.i("err",e.toString());
                            e.printStackTrace();
                        }

                        assert conn != null;
                        conn.setDoOutput(true);
                        try {
                            conn.setRequestMethod("POST");
                            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                            String json =java.net.URLEncoder.encode(Base64.encodeToString(data,Base64.DEFAULT), "ISO-8859-1");
                            String urlParameters  = "img="+json;
                            postData = urlParameters.getBytes( StandardCharsets.UTF_8 );
                            int postDataLength = postData.length;
                            conn.setRequestProperty("Content-Length", Integer.toString(postDataLength ));
                        } catch (Exception e) {
                            Log.i("err",e.toString());
                            e.printStackTrace();
                        }
                        OutputStream os = null;
                        try {
                            os = conn.getOutputStream();
                        } catch (Exception e) {
                            Log.i("err",e.toString());
                            e.printStackTrace();
                        }
                        try {
                            assert os != null;

                            os.write(postData);
                            Log.i("data","data-writing");
                        } catch (Exception e) {
                            Log.i("err",e.toString());
                            e.printStackTrace();
                        }
                        try {
                            os.flush();
                        } catch (IOException e) {
                            Log.i("err",e.toString());
                            e.printStackTrace();
                        }
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


                        conn.disconnect();
                    } catch (Exception e) {
                        Log.i("err",e.toString());
                        e.printStackTrace();
                    }

                    camkapa(sHolder);
                }
            });
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(response.toString().equals("got"))
            {
                Log.i("success","image sent");
            }
            Log.i("data","exit");
        }
    };


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("cap-photo","destroyed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void camkapa(SurfaceHolder sHolder) {

        if (null == mCamera)
            return;
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
        Log.i("CAM", " closed");
    }

}



