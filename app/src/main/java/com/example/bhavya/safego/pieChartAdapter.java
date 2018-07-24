package com.example.bhavya.safego;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by bhavya on 15/4/18.
 */

public class pieChartAdapter extends RecyclerView.Adapter<pieChartAdapter.pieChartViewHolder> {

    private JSONArray trips;
    private Context mContext;

    public pieChartAdapter(JSONArray trips, Context context){
        this.trips=trips;
        this.mContext=context;
    }
    @Override
    public pieChartAdapter.pieChartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId=R.layout.result_pie_chart;
        View view = LayoutInflater.from(mContext).inflate(layoutId, parent, false);

        view.setFocusable(true);

        return new pieChartAdapter.pieChartViewHolder(view);

    }

    @Override
    public void onBindViewHolder(pieChartAdapter.pieChartViewHolder holder, int position) {

       int countClass[]=new int[5];int s=1 ;
        ArrayList<PieEntry> yvalues = new ArrayList<>();
        try {
            JSONObject newResult = new JSONObject(trips.getString(position));
          JSONArray trip = newResult.getJSONArray("trip");
            String timeStamp = new JSONObject(trip.getString(0)).getString("time");
            String date = timeStamp.substring(0, 10);
            String time = timeStamp.substring(11, 16);
            holder.tripDetails.setText("TRIP " + (position + 1) + " : " + date + " ,  " + time);
            s=trip.length();
            for (int i=0;i<trip.length();i++)
            {
                countClass[new JSONObject(trip.getString(i)).getInt("class")]++;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        for(int i=0;i<5;i++)
        {
            if((float)countClass[i]/s*100!=0)
            yvalues.add(new PieEntry((float)countClass[i]/s*100,getClassName(i)));
        }
        PieDataSet dataSet = new PieDataSet(yvalues,"");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet.setHighlightEnabled(true);
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11);

        holder.pieChart.setData(data);


    }

    private String getClassName(int i){
        switch (i){
            case 0: return "Aggressive Acceleration";
            case 1: return "Aggressive Break";
            case 2: return "Aggressive Right";
            case 3: return "Aggressive Left";
            case 4: return "Non Aggressive";
            default:return "invalid";
        }
    }

    @Override
    public int getItemCount() {
        return trips.length();
    }

    static class pieChartViewHolder extends RecyclerView.ViewHolder{

        PieChart pieChart;
        TextView tripDetails;
        public pieChartViewHolder(View itemView) {
            super(itemView);
            tripDetails=itemView.findViewById(R.id.tripDetailsPie);
             pieChart = itemView.findViewById(R.id.piechart);
             pieChart.setUsePercentValues(true);
             pieChart.setDrawHoleEnabled(false);
            pieChart.setDrawSliceText(false);
            Legend legend = pieChart.getLegend();
            legend.setWordWrapEnabled(true);
            Description d=null;
            pieChart.setDescription(d);

        }
    }
}

