package com.example.bhavya.safego;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by bhavya on 18/4/18.
 */

public class driverOffenceImages extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_offence_images);
        String offenceName=getIntent().getStringExtra("offence");
        TextView offence=findViewById(R.id.offenceName);
        offence.setText(offenceName);
        RecyclerView images_rv=findViewById(R.id.images);
        try {
            JSONArray images=new JSONArray(getIntent().getStringExtra("images"));

            LinearLayoutManager layoutManager =
                    new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

            images_rv.setLayoutManager(layoutManager);
            imageAdapter adapter = new imageAdapter(images, this);
            images_rv.setAdapter(adapter);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
