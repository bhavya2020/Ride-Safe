package com.example.bhavya.safego;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.Loader;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bhavya.safego.data.accelerometerContract;
import com.example.bhavya.safego.data.accelerometerDbHelper;
import com.example.bhavya.safego.data.gyroscopeContract;
import com.example.bhavya.safego.data.gyroscopeDbHelper;
import com.example.bhavya.safego.data.linearAccelerationContract;
import com.example.bhavya.safego.data.linearAccelerationDbHelper;
import com.example.bhavya.safego.data.magnetometerContract;
import com.example.bhavya.safego.data.magnetometerDbHelper;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoggedIn extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    Boolean isMonitoring = false;
    Boolean isImageServiceStarted = false;
    private reportsAdapter mAdapter;
    private GoogleMap mMap;
    private driversAdapter dAdapter;
    private RecyclerView mRecyclerView;
    private RecyclerView driversView;
    private final static String ip = "192.168.43.170";
    private final static String port = "5555";
    private String Email;
    private static final int MY_REQUEST_CODE = 3;
    private String key;
    private GoogleSignInClient mGoogleSignInClient;

    private SharedPreferences mPrefs;

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("start", "starting activity");
        final Button monitorBtn = findViewById(R.id.monitorBtn);
        final Button stopMonitorBtn = findViewById(R.id.StopMonitorBtn);

        if (isMonitoring) {
            monitorBtn.setVisibility(View.GONE);
            stopMonitorBtn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i("restart", "restarting activity");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("stop", "stopping activity");

    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor ed = mPrefs.edit();
        ed.putBoolean("isMonitoring", isMonitoring);
        ed.apply();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("resume", "resuming activity");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("destroy", "destroying activity");
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Now user should be able to use camera
                Log.i("permission", "granted");
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);
        Log.i("create", "creating activity");
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        isImageServiceStarted = mPrefs.getBoolean("isImageServiceStarted", false);
        isMonitoring = mPrefs.getBoolean("isMonitoring", false);

        if (checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, MY_REQUEST_CODE);
        }


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

        final Button monitorBtn = findViewById(R.id.monitorBtn);
        monitorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickMonitor(view);
            }
        });
        final Button stopMonitorBtn = findViewById(R.id.StopMonitorBtn);
        stopMonitorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickStopMonitor(view);
            }
        });

        if (isMonitoring) {
            monitorBtn.setVisibility(View.GONE);
            stopMonitorBtn.setVisibility(View.VISIBLE);
        }

        TextView u2 = findViewById(R.id.username);
        ImageView pic2 = findViewById(R.id.profile_pic);
        TextView email = findViewById(R.id.email);

        if (account != null) {
            if (account.getDisplayName() != null)
                u2.setText(account.getDisplayName());
            email.setText(account.getEmail());
            Email = account.getEmail();
            android.net.Uri profileUrl = account.getPhotoUrl();
            new LoadProfilePic(pic2).execute(String.valueOf(profileUrl));
        }
        key = getKey();
        //TODO: make it not image service started
        if (!isImageServiceStarted) {
            SharedPreferences.Editor ed = mPrefs.edit();
            ed.putBoolean("isImageServiceStarted", true);
            ed.putString("uname", Email);
            ed.apply();
            Intent service = new Intent(getBaseContext(), ImageService.class);
            startService(service);
        }
        TextView isUnder18 = findViewById(R.id.isUnder18);
        String IsUnder18 = isUnder18();
        if (IsUnder18.equals("1"))
            isUnder18.setVisibility(View.VISIBLE);
        try {
            setProfile();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final Button editProfile = findViewById(R.id.edit);
        final Button updateProfile = findViewById(R.id.update);
        editProfile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                editProfile.setVisibility(View.GONE);
                updateProfile.setVisibility(View.VISIBLE);
                EditText DLno = findViewById(R.id.DLno);
                EditText numberPlate = findViewById(R.id.numPlate);
                EditText gender = findViewById(R.id.gender);
                EditText phoneNo = findViewById(R.id.phoneno);
                EditText age = findViewById(R.id.age);

                DLno.setEnabled(true);
                numberPlate.setEnabled(true);
                gender.setEnabled(true);
                phoneNo.setEnabled(true);
                age.setEnabled(true);

            }
        });

        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editProfile.setVisibility(View.VISIBLE);
                updateProfile.setVisibility(View.GONE);
                Log.i("up", "updating");
                try {
                    UpdateProfile();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        Button reportButton = findViewById(R.id.reportBtn);
        final EditText reportLicencePlate = findViewById(R.id.ReportLicencePlateNo);
        final CheckBox categories[] = new CheckBox[6];
        categories[0] = findViewById(R.id.suddenBreaks);
        categories[1] = findViewById(R.id.wrongSide);
        categories[2] = findViewById(R.id.inappropriateParking);
        categories[3] = findViewById(R.id.sharpTurn);
        categories[4] = findViewById(R.id.blockingTheRoad);
        categories[5] = findViewById(R.id.overtakingFromLeft);

        final boolean categoriesToReport[] = new boolean[6];

        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < 6; i++) {
                    categoriesToReport[i] = categories[i].isChecked();
                    categories[i].setChecked(false);
                }
                report(reportLicencePlate.getText().toString(), categoriesToReport);
                reportLicencePlate.setText("");

            }
        });
        final Button addDriverButton = findViewById(R.id.addDriverBtn);
        addDriverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    addDriver();
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


    private String getKey() {
        final StringBuffer response = new StringBuffer();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://" + ip + ":" + port + "/key/" + Email);
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
                } catch (Exception e) {
                    Log.d("error", e.toString());
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ;
        String KEY = response.toString();
        KEY = KEY.substring(1, KEY.length() - 1);
        Log.i("key", response.toString());
        return KEY;
    }

    private void report(String licencePlate, boolean[] categoriesToReport) {

        final StringBuffer response = new StringBuffer();
        String json = "{\"reporterID\":\"" + Email + "\",\"plateNo\":\"" + licencePlate + "\"";
        for (int i = 0; i < 6; i++) {
            json = json.concat(",\"category" + i + "\":\"" + categoriesToReport[i] + "\"");
        }
        final String JSON = json.concat("}");
        Log.i("json", JSON);
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://" + ip + ":" + port + "/report");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoOutput(true);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    OutputStream os;
                    os = conn.getOutputStream();
                    os.write(JSON.getBytes());
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
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (response.toString().equals("done")) {
            Toast.makeText(this, "reported successfully", Toast.LENGTH_SHORT).show();
        }


    }

    private String isUnder18() {
        final StringBuffer response = new StringBuffer();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://" + ip + ":" + port + "/isUnder18/" + Email);
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
                } catch (Exception e) {
                    Log.d("error", e.toString());
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.i("under18", response.toString());
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
        EditText DLno = findViewById(R.id.DLno);
        EditText numberPlate = findViewById(R.id.numPlate);
        EditText gender = findViewById(R.id.gender);
        EditText phoneNo = findViewById(R.id.phoneno);
        EditText age = findViewById(R.id.age);

        final StringBuffer response = new StringBuffer();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://" + ip + ":" + port + "/profile/" + Email);
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
                } catch (Exception e) {
                    Log.d("error", e.toString());
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.i("res", response.toString());
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
        if (id == R.id.action_key) {
            Log.i("key", key);
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("key", key);
            clipboard.setPrimaryClip(clip);
        }

        return super.onOptionsItemSelected(item);
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(LoggedIn.this, LoggedOut.class);
                        startActivity(intent);
                    }
                });
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        LinearLayout home = findViewById(R.id.homeL);
        LinearLayout profile = findViewById(R.id.profileL);
        LinearLayout about = findViewById(R.id.aboutL);
        LinearLayout monitor = findViewById(R.id.monitorL);
        LinearLayout addDriver = findViewById(R.id.addDriverL);
        LinearLayout distraction = findViewById(R.id.distractionL);
        LinearLayout report = findViewById(R.id.reportL);
        LinearLayout reportAgainst = findViewById(R.id.reportAgainstL);

        home.setVisibility(View.GONE);
        profile.setVisibility(View.GONE);
        about.setVisibility(View.GONE);
        monitor.setVisibility(View.GONE);
        addDriver.setVisibility(View.GONE);
        distraction.setVisibility(View.GONE);
        report.setVisibility(View.GONE);
        reportAgainst.setVisibility(View.GONE);

        if (id == R.id.monitor) {
            monitor.setVisibility(View.VISIBLE);
        } else if (id == R.id.home) {
            home.setVisibility(View.VISIBLE);
        } else if (id == R.id.see) {
            addDriver.setVisibility(View.VISIBLE);
            try {
                getDrivers();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (id == R.id.distraction) {
            distraction.setVisibility(View.VISIBLE);
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            if (mapFragment != null)
                mapFragment.getMapAsync(this);




        } else if (id == R.id.report) {
            report.setVisibility(View.VISIBLE);
        } else if (id == R.id.reportAgainst) {
            reportAgainst.setVisibility(View.VISIBLE);
            try {
                getReports();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (id == R.id.profile) {
            profile.setVisibility(View.VISIBLE);
        } else if (id == R.id.about) {
            about.setVisibility(View.VISIBLE);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void getDrivers() throws JSONException {
        final StringBuffer response = new StringBuffer();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://" + ip + ":" + port + "/drivers/" + Email);
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
                } catch (Exception e) {
                    Log.d("error", e.toString());
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.i("drivers-res", response.toString());
        JSONObject obj = new JSONObject(response.toString());
        JSONArray arr = obj.getJSONArray("drivers");
        driversView = findViewById(R.id.drivers);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        driversView.setLayoutManager(layoutManager);
        dAdapter = new driversAdapter(arr, this);
        driversView.setAdapter(dAdapter);
    }

    private void getReports() throws JSONException {
        final StringBuffer response = new StringBuffer();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://" + ip + ":" + port + "/report/" + Email);
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
                } catch (Exception e) {
                    Log.d("error", e.toString());
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        JSONObject obj = new JSONObject(response.toString());
        JSONArray arr = obj.getJSONArray("reports");
        for (int i = 0; i < arr.length(); i++) {
            Log.i("reports" + i, String.valueOf(new JSONObject(arr.getString(i))));
        }
        mRecyclerView = findViewById(R.id.rv_reports);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new reportsAdapter(arr, this);
        mRecyclerView.setAdapter(mAdapter);

    }

    private void clickMonitor(View view) {
        isMonitoring = true;
        Button stopMonitor = findViewById(R.id.StopMonitorBtn);
        stopMonitor.setVisibility(View.VISIBLE);
        view.setVisibility(View.GONE);
        Intent service = new Intent(getBaseContext(), startMonitorService.class);
        startService(service);
    }

    private void clickStopMonitor(View view) {
        isMonitoring = false;
        Button Monitor = findViewById(R.id.monitorBtn);
        Monitor.setVisibility(View.VISIBLE);
        view.setVisibility(View.GONE);
        Intent service = new Intent(getBaseContext(), startMonitorService.class);
        stopService(service);
        new stopMonitor(Email, this).execute();
    }

    private void addDriver() throws JSONException {

        EditText driver = findViewById(R.id.driverKey);
        String driversKey = driver.getText().toString();
        final StringBuffer response = new StringBuffer();
        final String json = "{\"manager\":\"" + Email + "\",\"driverKey\":\"" + driversKey + "\"}";
        Log.i("add", json);
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://" + ip + ":" + port + "/addDriver");
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
                    //error occurred
                    Log.d("error", e.toString());
                }
            }
        });
        thread.start();
        driver.setText("");
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (response.toString().equals("done")) {
            getDrivers();
        } else if (response.toString().equals("notDone")) {
            Toast.makeText(this, "Incorrect key", Toast.LENGTH_LONG).show();
        }

    }

    private void UpdateProfile() throws JSONException {
        EditText DLno = findViewById(R.id.DLno);
        EditText numberPlate = findViewById(R.id.numPlate);
        EditText gender = findViewById(R.id.gender);
        EditText phoneNo = findViewById(R.id.phoneno);
        EditText age = findViewById(R.id.age);
        DLno.setEnabled(false);
        numberPlate.setEnabled(false);
        gender.setEnabled(false);
        phoneNo.setEnabled(false);
        age.setEnabled(false);
        final StringBuffer response = new StringBuffer();
        final String json = "{\"DLno\":\"" + DLno.getText() + "\",\"licencePlateNo\":\"" + numberPlate.getText() + "\",\"gender\":\"" + gender.getText() + "\",\"age\":\"" + age.getText() + "\",\"phoneNo\":\"" + phoneNo.getText() + "\",\"email\":\"" + Email + "\"}";
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://" + ip + ":" + port + "/update");
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
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (response.toString().equals("done")) {
            setProfile();
        }

    }

    @Override
    public void onMapReady(GoogleMap map) {

        mMap = map;

//        LatLng sydney = new LatLng(-33.867, 151.206);
//
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
 map.setMyLocationEnabled(true);
        JSONArray result=null;
        try {
            result= getResults();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            showResult(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }

//
//        map.addMarker(new MarkerOptions()
//                .title("Sydney")
//                .snippet("The most populous city in Australia.")
//                .position(sydney));


    }


    private JSONArray getResults() throws JSONException {

        final StringBuffer response = new StringBuffer();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://" + ip + ":" + port + "/result/" + Email);
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
                } catch (Exception e) {
                    Log.d("error", e.toString());
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        JSONObject obj = new JSONObject(response.toString());

        return obj.getJSONArray("trip");

    }

    private void showResult(JSONArray result) throws JSONException {


        List<ColouredPoint> points=new ArrayList<>();
        for(int i=0;i<result.length();i++)
        {
            JSONObject obj=result.getJSONObject(i);
            LatLng lt=new LatLng(obj.getDouble("latitude"),obj.getDouble("longitude"));
            if(i==0)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lt,13));
            ColouredPoint cp=new ColouredPoint(lt,getColour(obj.getInt("class")));

            points.add(cp);
        }

        showPolyline(points);

//        List<ColouredPoint> sourcePoints = new ArrayList<>();
//        sourcePoints.add(new ColouredPoint(new LatLng(-35.27801,149.12958), Color.RED));
//        sourcePoints.add(new ColouredPoint(new LatLng(-35.28032,149.12907),  Color.RED));
//        sourcePoints.add(new ColouredPoint(new LatLng(-35.28099,149.12929),  Color.RED));
//        sourcePoints.add(new ColouredPoint(new LatLng(-35.28144,149.12984),  Color.RED));
//        sourcePoints.add(new ColouredPoint(new LatLng(-35.28194,149.13003), Color.RED));
//        sourcePoints.add(new ColouredPoint(new LatLng(-35.28282,149.12956), Color.RED));
//        sourcePoints.add(new ColouredPoint(new LatLng(-35.28302,149.12881), Color.RED));
//        sourcePoints.add(new ColouredPoint(new LatLng(-35.28473,149.12836), Color.RED));
//
//        showPolyline(sourcePoints);

    }

    private int getColour(int aClass) {

        switch (aClass)
        {
            case 0: return Color.RED;
            case 1: return Color.BLUE;
            case 2: return Color.YELLOW;
            case 3: return Color.GREEN;
            case 4: return Color.MAGENTA;
            default: return  Color.CYAN;
        }
    }

    private void showPolyline(List<ColouredPoint> points) {

        if (points.size() < 2)
            return;

        int ix = 0;
        ColouredPoint currentPoint  = points.get(ix);
        int currentColor = currentPoint.color;
        List<LatLng> currentSegment = new ArrayList<>();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPoint.coords,15));
        currentSegment.add(currentPoint.coords);
        ix++;

        while (ix < points.size()) {
            currentPoint = points.get(ix);

            if (currentPoint.color == currentColor) {
                currentSegment.add(currentPoint.coords);
            } else {
                currentSegment.add(currentPoint.coords);
                mMap.addPolyline(new PolylineOptions()
                        .addAll(currentSegment)
                        .color(currentColor)
                        .width(20));
                currentColor = currentPoint.color;
                currentSegment.clear();
                currentSegment.add(currentPoint.coords);
            }

            ix++;
        }

        mMap.addPolyline(new PolylineOptions()
                .addAll(currentSegment)
                .color(currentColor)
                .width(20));

    }


}
