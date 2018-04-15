package com.example.bhavya.safego;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by bhavya on 14/4/18.
 */

public class monitorResultAdapter extends RecyclerView.Adapter<monitorResultAdapter.monitorResultAdapterViewHolder> {


    private JSONArray monitorResult;
    private Context mContext;
    final private monitorResultAdapterOnClickHandler mClickHandler;

    interface monitorResultAdapterOnClickHandler {
        void onClick(JSONArray result,String tripDetails);
    }

    monitorResultAdapter(JSONArray monitorResult, Context context, monitorResultAdapterOnClickHandler clickHandler) {
        this.monitorResult = monitorResult;
        mContext = context;
        mClickHandler = clickHandler;
    }

    @Override
    public monitorResultAdapter.monitorResultAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = R.layout.result_trip;
        View view = LayoutInflater.from(mContext).inflate(layoutId, parent, false);

        view.setFocusable(true);
        return new monitorResultAdapter.monitorResultAdapterViewHolder(view);
    }


    @Override
    public void onBindViewHolder(monitorResultAdapter.monitorResultAdapterViewHolder holder, int position) {


        JSONObject newResult;
        try {
            newResult = new JSONObject(monitorResult.getString(position));
            holder.result = newResult.getJSONArray("trip");
            String timeStamp = new JSONObject(holder.result.getString(0)).getString("time");
            String date = timeStamp.substring(0, 10);
            String time = timeStamp.substring(11, 16);
            holder.trip.setText("TRIP " + (position + 1) + " : " + date + " ,  " + time);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        return monitorResult.length();
    }


    class monitorResultAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        public TextView trip;


        public FrameLayout mapLayout;
        public JSONArray result;

        public monitorResultAdapterViewHolder(View itemView) {
            super(itemView);

            mapLayout = itemView.findViewById(R.id.map);
            trip = itemView.findViewById(R.id.tripTime);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            mClickHandler.onClick(result, trip.getText().toString());

        }
    }

}

