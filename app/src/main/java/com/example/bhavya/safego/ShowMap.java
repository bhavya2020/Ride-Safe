package com.example.bhavya.safego;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bhavya on 15/4/18.
 */

public class ShowMap extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private JSONArray result;
    private String tripDetails;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_map);
        SupportMapFragment mapFragment= (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        TextView tripDetailsTv=findViewById(R.id.tripDetails);

        tripDetails=getIntent().getStringExtra("tripDetails");
        tripDetailsTv.setText(tripDetails);
        try {
            result=new JSONArray(getIntent().getStringExtra("trip"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void showResult(JSONArray result) throws JSONException {


        Marker start, end;
        List<ColouredPoint> points = new ArrayList<>();
        for (int i = 0; i < result.length(); i++) {
            JSONObject obj = result.getJSONObject(i);
            LatLng lt = new LatLng(obj.getDouble("latitude"), obj.getDouble("longitude"));
            if (i == 0) {
                start = mMap.addMarker(new MarkerOptions()
                        .position(lt)
                        .title("start")
                );
                start.setTag(0);
            }
            if (i == result.length() - 1) {
                end = mMap.addMarker(new MarkerOptions()
                        .position(lt)
                        .title("end"));
                end.setTag(0);
            }
            ColouredPoint cp = new ColouredPoint(lt, getColour(obj.getInt("class")));

            points.add(cp);
        }

        showPolyline(points);
    }


    private int getColour(int aClass) {

        switch (aClass) {
            case 0:
                return Color.RED;
            case 1:
                return Color.BLUE;
            case 2:
                return Color.CYAN;
            case 3:
                return Color.GREEN;
            case 4:
                return Color.MAGENTA;
            default:
                return Color.YELLOW;
        }
    }

    private void showPolyline(List<ColouredPoint> points) {

        if (points.size() < 2)
            return;

        int ix = 0;
        ColouredPoint currentPoint = points.get(ix);
        int currentColor = currentPoint.color;
        List<LatLng> currentSegment = new ArrayList<>();

        currentSegment.add(currentPoint.coords);
        ix++;

        while (ix < points.size()) {
            currentPoint = points.get(ix);
            if(ix==points.size()/2)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPoint.coords, 15));
            if (currentPoint.color == currentColor) {
                currentSegment.add(currentPoint.coords);
            } else {
                currentSegment.add(currentPoint.coords);
                mMap.addPolyline(new PolylineOptions()
                        .addAll(currentSegment)
                        .color(currentColor)
                        .width(10));
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

    @Override
    public void onMapReady(GoogleMap map) {
        mMap=map;
        try {
            showResult(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
