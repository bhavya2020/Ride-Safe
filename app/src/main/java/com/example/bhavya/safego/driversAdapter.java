package com.example.bhavya.safego;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by bhavya on 9/4/18.
 */

public class driversAdapter  extends RecyclerView.Adapter<driversAdapter.driversAdapterViewHolder> {

    private JSONArray drivers;
    private Context mContext;

    public driversAdapter(JSONArray drivers, Context context){
        this.drivers=drivers;
        mContext=context;
    }
    @Override
    public driversAdapter.driversAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId=R.layout.driver;
        View view = LayoutInflater.from(mContext).inflate(layoutId, parent, false);

        view.setFocusable(true);

        return new driversAdapter.driversAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(driversAdapter.driversAdapterViewHolder holder, int position) {


        String driverName="";

        try {
            driverName=drivers.getString(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.driverName.setText(driverName);
        holder.monitorDriverBtn.setTag(driverName);
    }

    @Override
    public int getItemCount() {
        return drivers.length();
    }

    class driversAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView driverName;
        final Button monitorDriverBtn;


        driversAdapterViewHolder(View view) {
            super(view);

            driverName=view.findViewById(R.id.driverName);
            monitorDriverBtn=view.findViewById(R.id.monitorDriverBtn);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
