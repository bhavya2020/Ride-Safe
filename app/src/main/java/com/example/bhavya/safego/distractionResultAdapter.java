package com.example.bhavya.safego;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by bhavya on 17/4/18.
 */

public class distractionResultAdapter extends RecyclerView.Adapter<distractionResultAdapter.distractionResultViewHolder> {

    private JSONArray results;
    private  Context context;
    private distractionOnClickListener clickListener;

    public  interface distractionOnClickListener{
        void onDriverClick(String driverName);
    }
    distractionResultAdapter(JSONArray results, Context context,distractionOnClickListener clickListener)
    {
        this.results=results;
        this.context=context;
        this.clickListener=clickListener;
    }

    @Override
    public distractionResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId=R.layout.result_driver_distraction;


        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);

        view.setFocusable(true);

        return new distractionResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(distractionResultViewHolder holder, int position) {

        try {
            JSONObject driverOffence=new JSONObject(results.getString(position));
            String driverName=driverOffence.getString("driverName");
            holder.name=driverName;
            holder.driverName.setText(driverName.substring(0, driverName.length() - 10));
            holder.offences.setText(driverOffence.getString("offences"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return results.length();
    }

    public  class distractionResultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView driverName;
        TextView offences;
        String name;
        public distractionResultViewHolder(View itemView) {
            super(itemView);
            driverName=itemView.findViewById(R.id.driverName);
            offences=itemView.findViewById(R.id.offences);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            clickListener.onDriverClick(name);
        }
    }
}
