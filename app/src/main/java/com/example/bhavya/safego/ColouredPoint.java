package com.example.bhavya.safego;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by bhavya on 13/4/18.
 */

class ColouredPoint {
    public LatLng coords;
    public int color;

    public ColouredPoint(LatLng coords, int color) {
        this.coords = coords;
        this.color = color;
    }
}