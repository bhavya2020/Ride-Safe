package com.example.bhavya.safego;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by bhavya on 9/4/18.
 */

public class driversAdapter extends RecyclerView.Adapter<driversAdapter.driversAdapterViewHolder> {

    private SharedPreferences mPrefs;
    private JSONArray drivers;
    private Context mContext;
    private final static String ip = "192.168.43.170";
    private final static String port = "5555";

    public driversAdapter(JSONArray drivers, Context context) {
        this.drivers = drivers;
        mContext = context;
    }

    @Override
    public driversAdapter.driversAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = R.layout.driver;
        View view = LayoutInflater.from(mContext).inflate(layoutId, parent, false);

        view.setFocusable(true);

        return new driversAdapter.driversAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(driversAdapter.driversAdapterViewHolder holder, int position) {


        mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);

        String driverName = "";

        try {
            driverName = drivers.getString(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        boolean Show = mPrefs.getBoolean(driverName, false);
        Log.i(driverName, String.valueOf(Show));
        if (!Show) {
            holder.monitorDriverBtn.setVisibility(View.VISIBLE);
            holder.stopMonitorDriverBtn.setVisibility(View.GONE);
        } else {
            holder.monitorDriverBtn.setVisibility(View.GONE);
            holder.stopMonitorDriverBtn.setVisibility(View.VISIBLE
            );
        }
        holder.driverName.setText(driverName.substring(0, driverName.length() - 10));
        holder.monitorDriverBtn.setTag(R.id.email, driverName);
        holder.stopMonitorDriverBtn.setTag(R.id.email, driverName);


    }

    @Override
    public int getItemCount() {
        return drivers.length();
    }

    class driversAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView driverName;
        final Button monitorDriverBtn;
        final Button stopMonitorDriverBtn;


        driversAdapterViewHolder(View view) {
            super(view);

            driverName = view.findViewById(R.id.driverName);
            monitorDriverBtn = view.findViewById(R.id.monitorDriverBtn);
            stopMonitorDriverBtn = view.findViewById(R.id.StopMonitorDriverBtn);

            monitorDriverBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences.Editor ed = mPrefs.edit();
                    Log.i("true", monitorDriverBtn.getTag(R.id.email).toString());
                    ed.putBoolean(monitorDriverBtn.getTag(R.id.email).toString(), true);
                    ed.apply();

                    stopMonitorDriverBtn.setVisibility(View.VISIBLE);
                    monitorDriverBtn.setVisibility(View.GONE);

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
                                socket.emit("monitor", monitorDriverBtn.getTag(R.id.email).toString(), new Ack() {
                                    @Override
                                    public void call(Object... args) {
                                        Log.i("monitor", "monitoring");
                                    }
                                });
                            }

                        });
                        socket.on("fail", new Emitter.Listener() {
                            @Override
                            public void call(Object... args) {
                                Log.i("monitor","fail");
                            }
                        });
                        socket.connect();

                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }


                }
            });
            stopMonitorDriverBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences.Editor ed = mPrefs.edit();
                    Log.i("false", stopMonitorDriverBtn.getTag(R.id.email).toString());
                    ed.putBoolean(stopMonitorDriverBtn.getTag(R.id.email).toString(), false);
                    ed.apply();
                    stopMonitorDriverBtn.setVisibility(View.GONE);
                    monitorDriverBtn.setVisibility(View.VISIBLE);
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
                                socket.emit("stopMonitor", stopMonitorDriverBtn.getTag(R.id.email).toString(), new Ack() {
                                    @Override
                                    public void call(Object... args) {
                                        Log.i("stop-monitor", "stopping");
                                    }
                                });
                                socket.disconnect();
                            }

                        });
                        socket.connect();

                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }

                }
            });
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {


        }
    }
}
