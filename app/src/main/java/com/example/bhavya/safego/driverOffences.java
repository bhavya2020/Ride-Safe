package com.example.bhavya.safego;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by bhavya on 17/4/18.
 */

public class driverOffences extends AppCompatActivity implements OnChartValueSelectedListener {

    private String ip = "192.168.43.170";
    private String port = "5555";
    private String driverName;
    private Legend legend;
    private JSONArray offences[];

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_offences);
        driverName = getIntent().getStringExtra("driverName");
        TextView name = findViewById(R.id.driverName);
        name.setText(driverName.substring(0, driverName.length() - 10));
        try {
             offences = getOffenceArray();
            PieChart pieChart = findViewById(R.id.piechart);
            pieChart.setUsePercentValues(true);
            pieChart.setDrawHoleEnabled(false);
            pieChart.setDrawSliceText(false);
            legend = pieChart.getLegend();
            legend.setWordWrapEnabled(true);
            pieChart.setDescription(null);

            int sum = 0;
            for (JSONArray offence : offences) {
                sum += offence.length();
            }
            ArrayList<PieEntry> yvalues = new ArrayList<>();
            for (int i = 0; i < offences.length; i++) {
                if((float) offences[i].length() / sum * 100!=0)
                yvalues.add(new PieEntry((float) offences[i].length() / sum * 100, getClassName(i)));
            }

            PieDataSet dataSet = new PieDataSet(yvalues, "");
            dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
            dataSet.setHighlightEnabled(true);
            PieData data = new PieData(dataSet);
            data.setValueFormatter(new PercentFormatter());
            data.setValueTextSize(11);

            pieChart.setData(data);
            pieChart.setOnChartValueSelectedListener(this);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private String getClassName(int i) {
        switch (i) {
            case 0:
                return "texting";
            case 1:
                return "talking on phone";
            case 2:
                return "operating radio";
            case 3:
                return "drinking";
            case 4:
                return "reaching behind";
            case 5:
                return "hair and makeup";
            case 6:
                return "talking to passenger";
            default:
                return "invalid";
        }
    }

    private int getClassID(String className) {
        switch (className) {
            case "texting":
                return 0;
            case "talking on phone":
                return 1;
            case "operating radio":
                return 2;
            case "drinking":
                return 3;
            case "reaching behind":
                return 4;
            case "hair and makeup":
                return 5;
            case "talking to passenger":
                return 6;
            default:
                return -1;
        }
    }

    private JSONArray[] getOffenceArray() throws JSONException {
        final StringBuffer response = new StringBuffer();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://" + ip + ":" + port + "/distractionResult/" + driverName);
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
        JSONArray[] results = new JSONArray[7];
        JSONArray arr = obj.getJSONArray("c1");
        results[0] = arr;
        arr = obj.getJSONArray("c2");
        results[1] = arr;
        arr = obj.getJSONArray("c3");
        results[2] = arr;
        arr = obj.getJSONArray("c4");
        results[3] = arr;
        arr = obj.getJSONArray("c5");
        results[4] = arr;
        arr = obj.getJSONArray("c6");
        results[5] = arr;
        arr = obj.getJSONArray("c7");
        results[6] = arr;
        return results;

    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

        String lables[]=legend.getLabels();
        Intent intent=new Intent(driverOffences.this,driverOffenceImages.class);
        intent.putExtra("images",offences[getClassID(lables[(int)h.getX()])].toString());
        intent.putExtra("offence",lables[(int)h.getX()]);
        startActivity(intent);
    }

    @Override
    public void onNothingSelected() {
        Log.i("pie-chart", "nothing selected");
    }
}
