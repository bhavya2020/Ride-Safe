package com.example.bhavya.safego;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

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
import java.util.concurrent.TimeUnit;


/**
 * Created by bhavya on 14/4/18.
 */

public class monitorResultAdapter extends RecyclerView.Adapter<monitorResultAdapter.monitorResultAdapterViewHolder>  {

    private GoogleMap mMap;
    private JSONArray monitorResult;
    private Context mContext;


    monitorResultAdapter(JSONArray monitorResult, Context context) {
        this.monitorResult = monitorResult;
        mContext = context;
    }

    @Override
    public monitorResultAdapter.monitorResultAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = R.layout.result_map;
        View view = LayoutInflater.from(mContext).inflate(layoutId, parent, false);

        return new monitorResultAdapter.monitorResultAdapterViewHolder(view);
    }


    @Override
    public void onBindViewHolder(monitorResultAdapter.monitorResultAdapterViewHolder holder, int position) {


        Log.i("maps","in on bind"+holder.getAdapterPosition());
        JSONObject newResult ;
        try {
            newResult=new JSONObject(monitorResult.getString(position));
            holder.result=newResult.getJSONArray("trip");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void onViewDetachedFromWindow(monitorResultAdapterViewHolder holder) {
        Log.i("maps","in on view detached"+holder.getAdapterPosition());
        holder.removeMapFragment();
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public void onViewAttachedToWindow(monitorResultAdapterViewHolder holder) {
        super.onViewAttachedToWindow(holder);

        Log.i("maps","in on view attached"+holder.getAdapterPosition());
        final JSONArray res=holder.result;
       holder.getMapFragmentAndCallback(new OnMapReadyCallback() {
           @Override
           public void onMapReady(GoogleMap map) {
               mMap = map;

               if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                   return;
               }

               try {
                   showResult(res);
               } catch (JSONException e) {
                   e.printStackTrace();
               }
           }
       });
    }

    @Override
    public int getItemCount() {
        return monitorResult.length();
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
                return Color.YELLOW;
            case 3:
                return Color.GREEN;
            case 4:
                return Color.MAGENTA;
            default:
                return Color.CYAN;
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



    class monitorResultAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public FrameLayout mapLayout;
        private SupportMapFragment mapFragment;
        public JSONArray result;

        public monitorResultAdapterViewHolder(View itemView) {
            super(itemView);
            mapLayout = itemView.findViewById(R.id.map);
        }
        public SupportMapFragment getMapFragmentAndCallback(OnMapReadyCallback callback) {
            if (mapFragment == null) {
                mapFragment = SupportMapFragment.newInstance();
                mapFragment.getMapAsync(callback);
            }
            FragmentManager fragmentManager =((FragmentActivity)mContext).getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.map, mapFragment).commit();
            return mapFragment;
        }
        public void removeMapFragment() {
            if (mapFragment != null) {
                FragmentManager fragmentManager = ((FragmentActivity)mContext).getSupportFragmentManager();
                fragmentManager.beginTransaction().remove(mapFragment).commitAllowingStateLoss();
                mapFragment = null;
            }
        }
        @Override
        public void onClick(View view) {

        }

    }

}

