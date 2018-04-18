package com.example.bhavya.safego;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by bhavya on 18/4/18.
 */

public class imageAdapter extends RecyclerView.Adapter<imageAdapter.imageAdapterViewHolder> {

    private JSONArray images;
    private Context context;
    private final static String ip = "192.168.43.170";
    private final static String port = "5555";

    public imageAdapter(JSONArray images, Context context){
        this.images=images;
        this.context=context;
    }

    @Override
    public imageAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutID=R.layout.image;
        View view = LayoutInflater.from(context).inflate(layoutID, parent, false);

        view.setFocusable(true);

        return new imageAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(imageAdapter.imageAdapterViewHolder holder, int position) {

        String imageUrl;
        try {
            imageUrl = "http://"+ip+":"+port+images.getString(position);
            new LoadProfilePic(holder.image).execute(imageUrl);
        } catch (JSONException e) {
            e.printStackTrace();
        }



    }

    @Override
    public int getItemCount() {
        return images.length();
    }

    class imageAdapterViewHolder extends RecyclerView.ViewHolder{

        private ImageView image;
        public imageAdapterViewHolder(View itemView) {
            super(itemView);
            image=itemView.findViewById(R.id.image);
        }
    }
}
