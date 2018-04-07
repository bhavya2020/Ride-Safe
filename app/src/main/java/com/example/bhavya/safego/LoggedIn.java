package com.example.bhavya.safego;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.bhavya.safego.data.accelerometerContract;
import com.example.bhavya.safego.data.accelerometerDbHelper;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Date;

public class LoggedIn extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SensorEventListener {

    SensorManager mSensorManager;
    private final static String ip="192.168.1.12";
    private final static String port="6666";
    private String Email;
    private SQLiteDatabase accDB;
    private GoogleSignInClient mGoogleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        Intent intent = getIntent();
        TextView username;
        TextView logout;
        ImageView profilePic;
        username = header.findViewById(R.id.username);
        profilePic = header.findViewById(R.id.profile_pic);
        logout = header.findViewById(R.id.logout);
        GoogleSignInAccount account = intent.getParcelableExtra("account");
        if (account != null) {
            if (account.getDisplayName() != null)
                username.setText(account.getDisplayName());
        android.net.Uri profileUrl = account.getPhotoUrl();
        new LoadProfilePic(profilePic).execute(String.valueOf(profileUrl));
        }

        final Button monitorBtn= findViewById(R.id.monitorBtn);
        monitorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickMonitor(view);
            }
        });
        final Button stopMonitorBtn= findViewById(R.id.StopMonitorBtn);
        stopMonitorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickStopMonitor(view);
            }
        });

        TextView u2=findViewById(R.id.username);
        ImageView pic2=findViewById(R.id.profile_pic);
        TextView email=findViewById(R.id.email);

        if (account != null) {
            if (account.getDisplayName() != null)
                u2.setText(account.getDisplayName());
            email.setText(account.getEmail());
            Email=account.getEmail();
            android.net.Uri profileUrl = account.getPhotoUrl();
            new LoadProfilePic(pic2).execute(String.valueOf(profileUrl));
        }
        TextView isUnder18=findViewById(R.id.isUnder18);
        String IsUnder18=isUnder18(account.getPhotoUrl());
        if(IsUnder18.equals("1"))
            isUnder18.setVisibility(View.VISIBLE);
        try {
            setProfile();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final Button updateProfile=findViewById(R.id.update);
        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("up","updating");
                try {
                    UpdateProfile();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });
    }
    private String isUnder18(Uri photoUrl) {
        final StringBuffer response = new StringBuffer();
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    URL url = new URL("http://" + ip + ":" + port + "/isUnder18/"+Email);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.getResponseCode();
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(conn.getInputStream()));
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    conn.disconnect();
                }catch (Exception e)
                {
                    Log.d("error",e.toString());
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.i("under18",response.toString());
        return response.toString();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.logged_in, menu);
        return true;
    }

    private void setProfile() throws JSONException {
        EditText DLno=findViewById(R.id.DLno);
        EditText numberPlate=findViewById(R.id.numPlate);
        EditText gender=findViewById(R.id.gender);
        EditText phoneNo=findViewById(R.id.phoneno);
        EditText age=findViewById(R.id.age);

        final StringBuffer response = new StringBuffer();
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    URL url = new URL("http://" + ip + ":" + port + "/profile/"+Email);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.getResponseCode();
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(conn.getInputStream()));
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    conn.disconnect();
                }catch (Exception e)
                {
                    Log.d("error",e.toString());
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.i("res",response.toString());
        JSONObject obj = new JSONObject(response.toString());
//        if(!obj.getString("DLno").equals("undefined"))
            DLno.setText((obj.getString("DLno")));
//        if(!obj.getString("licencePlateNo").equals("undefined"))
            numberPlate.setText((obj.getString("licencePlateNo")));
//        if(!obj.getString("gender").equals("undefined"))
            gender.setText((obj.getString("gender")));
//        if(!obj.getString("phoneNo").equals("undefined"))
            phoneNo.setText((obj.getString("phoneNo")));
//        if(!obj.getString("age").equals("undefined"))
            age.setText((obj.getString("age")));

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent=new Intent(LoggedIn.this,LoggedOut.class);
                        startActivity(intent);
                    }
                });
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        LinearLayout home=findViewById(R.id.homeL);
        LinearLayout monitor=findViewById(R.id.monitorL);
        LinearLayout see=findViewById(R.id.seeL);
        LinearLayout distraction=findViewById(R.id.distractionL);
        LinearLayout report=findViewById(R.id.reportL);
        LinearLayout reportAgainst=findViewById(R.id.reportAgainstL);

        home.setVisibility(View.GONE);
        monitor.setVisibility(View.GONE);
        see.setVisibility(View.GONE);
        distraction.setVisibility(View.GONE);
        report.setVisibility(View.GONE);
        reportAgainst.setVisibility(View.GONE);

        if (id == R.id.monitor) {
            monitor.setVisibility(View.VISIBLE);
        } else if (id == R.id.home) {
            home.setVisibility(View.VISIBLE);
        } else if (id == R.id.see) {
            see.setVisibility(View.VISIBLE);
        } else if (id == R.id.distraction) {
            distraction.setVisibility(View.VISIBLE);
        } else if (id == R.id.report) {
            report.setVisibility(View.VISIBLE);
        } else if (id == R.id.reportAgainst) {
            reportAgainst.setVisibility(View.VISIBLE);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void clickMonitor(View view)
    {
        Button stopMonitor= findViewById(R.id.StopMonitorBtn);
        stopMonitor.setVisibility(View.VISIBLE);
        view.setVisibility(View.GONE);

        accelerometerDbHelper dbHelper = new accelerometerDbHelper(this);
        accDB = dbHelper.getWritableDatabase();
        Sensor accelerometer=null,linearAcceleration=null,gyroscope=null,magnetometer=null;

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        linearAcceleration = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        gyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        if(accelerometer!=null)
            mSensorManager.registerListener(LoggedIn.this,accelerometer,SensorManager.SENSOR_DELAY_NORMAL);
        if(linearAcceleration!=null)
            mSensorManager.registerListener(LoggedIn.this,linearAcceleration,SensorManager.SENSOR_DELAY_NORMAL);
        if(gyroscope!=null)
            mSensorManager.registerListener(LoggedIn.this,gyroscope,SensorManager.SENSOR_DELAY_NORMAL);
        if(magnetometer!=null)
            mSensorManager.registerListener(LoggedIn.this,magnetometer,SensorManager.SENSOR_DELAY_NORMAL);
    }
    private void clickStopMonitor(View view)
    {
        Button Monitor= findViewById(R.id.monitorBtn);
        Monitor.setVisibility(View.VISIBLE);
        view.setVisibility(View.GONE);
        mSensorManager.unregisterListener(LoggedIn.this);
        Cursor cursor=getAllValuesOfAccelerometer();
        if (cursor.moveToFirst()){
            while(!cursor.isAfterLast()){
                Float x = cursor.getFloat(cursor.getColumnIndex(accelerometerContract.accelerometer.X));
                Float y = cursor.getFloat(cursor.getColumnIndex(accelerometerContract.accelerometer.Y));
                Float z = cursor.getFloat(cursor.getColumnIndex(accelerometerContract.accelerometer.Z));
                String timeStamp= cursor.getString(cursor.getColumnIndex(accelerometerContract.accelerometer.COLUMN_TIMESTAMP));

                Log.i("x",String.valueOf(x));
                Log.i("y",String.valueOf(y));
                Log.i("z",String.valueOf(z));
                Log.i("time",timeStamp);
                cursor.moveToNext();
            }
        }
        cursor.close();
        long del=accDB.delete(accelerometerContract.accelerometer.TABLE_NAME, "1", null);
        Log.i("del",String.valueOf(del));

    }
    private void UpdateProfile() throws JSONException {
        EditText DLno=findViewById(R.id.DLno);
        EditText numberPlate=findViewById(R.id.numPlate);
        EditText gender=findViewById(R.id.gender);
        EditText phoneNo=findViewById(R.id.phoneno);
        EditText age=findViewById(R.id.age);

        final StringBuffer response=new StringBuffer();
        final String json = "{\"DLno\":\"" + DLno.getText() + "\",\"licencePlateNo\":\""+numberPlate.getText()+"\",\"gender\":\""+ gender.getText()+"\",\"age\":\""+age.getText()+"\",\"phoneNo\":\""+phoneNo.getText()+"\",\"email\":\""+Email+"\"}";
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://"+ip+":"+port+"/update");
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

                }catch (Exception e){
                    //error occured
                    Log.d("error",e.toString());
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(response.toString().equals("done")) {
          setProfile();
        }

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER) {
           long temp= insertAccValues(event.values[0],event.values[1],event.values[2]);
            Log.i("insert",String.valueOf(temp));
        }
        if(event.sensor.getType()==Sensor.TYPE_LINEAR_ACCELERATION) {
        }
        if(event.sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD) {

        }
        if(event.sensor.getType()==Sensor.TYPE_GYROSCOPE) {

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
    public long insertAccValues(float x,float y,float z)
    {
        ContentValues cv = new ContentValues();
        cv.put(accelerometerContract.accelerometer.X, x);
        cv.put(accelerometerContract.accelerometer.Y, y);
        cv.put(accelerometerContract.accelerometer.Z, z);

        //  DateFormat df=new SimpleDateFormat("mm-dd hh:mm:ss");
        Date date=new Date();
        cv.put(accelerometerContract.accelerometer.COLUMN_TIMESTAMP, String.valueOf(date));

        return (accDB.insert(accelerometerContract.accelerometer.TABLE_NAME, null, cv));
    }
    public Cursor getAllValuesOfAccelerometer(){
        return  accDB.query(accelerometerContract.accelerometer.TABLE_NAME,new String[] {accelerometerContract.accelerometer.X,accelerometerContract.accelerometer.Y,accelerometerContract.accelerometer.Z,accelerometerContract.accelerometer.COLUMN_TIMESTAMP},null,null,null,null,null,null);
    }

}
