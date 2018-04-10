package com.example.bhavya.safego;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by bhavya on 8/4/18.
 */

public class reportsAdapter extends RecyclerView.Adapter<reportsAdapter.reportsAdapterViewHolder> {

    private JSONArray reports;
    private Context mContext;

    public reportsAdapter(JSONArray reports,Context context){
        this.reports=reports;
        mContext=context;
    }
    @Override
    public reportsAdapter.reportsAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId=R.layout.report;
        View view = LayoutInflater.from(mContext).inflate(layoutId, parent, false);

        view.setFocusable(true);

        return new reportsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(reportsAdapter.reportsAdapterViewHolder holder, int position) {

        JSONObject report;
        String reporterID="";
        JSONArray categories;
        boolean c[]=new boolean[6];
        String time="";
        try {
            report=new JSONObject(reports.getString(position));
            reporterID=report.getString("reporterID");
            categories=report.getJSONArray("categories");
            for(int i=0;i<6;i++)
            {
                c[i]=categories.getBoolean(i);
            }
            time=report.getString("createdAt");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        holder.reporterId.setText(reporterID);
        holder.time.setText(time);
        if(!c[0])
            holder.suddenBreaks.setVisibility(View.GONE);
        if(!c[1])
            holder.wrongSide.setVisibility(View.GONE);
        if(!c[2])
            holder.inappropriateParking.setVisibility(View.GONE);
        if(!c[3])
            holder.sharpTurn.setVisibility(View.GONE);
        if(!c[4])
            holder.blockingRoad.setVisibility(View.GONE);
        if(!c[5])
            holder.overtakingLeft.setVisibility(View.GONE);


    }

    @Override
    public int getItemCount() {
        return reports.length();
    }

    static class reportsAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView reporterId;
        final TextView suddenBreaks;
        final TextView wrongSide;
        final TextView inappropriateParking;
        final TextView sharpTurn;
        final TextView blockingRoad;
        final TextView overtakingLeft;
        final TextView time;


        reportsAdapterViewHolder(View view) {
            super(view);

            reporterId=view.findViewById(R.id.reporterId);
            suddenBreaks=view.findViewById(R.id.vSuddenBreaks);
            wrongSide=view.findViewById(R.id.vWrongSide);
            inappropriateParking=view.findViewById(R.id.vInappropriateParking);
            sharpTurn=view.findViewById(R.id.vSharpTurn);
            blockingRoad=view.findViewById(R.id.vBlockingTheRoad);
            overtakingLeft=view.findViewById(R.id.vOvertakingFromLeft);
            time=view.findViewById(R.id.vTime);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }
}