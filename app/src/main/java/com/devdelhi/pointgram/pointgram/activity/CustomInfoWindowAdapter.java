package com.devdelhi.pointgram.pointgram.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.devdelhi.pointgram.pointgram.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final View mWindow;
    private Context mContext;

    public CustomInfoWindowAdapter(Context context) {
        mContext = context;
        mWindow = LayoutInflater.from(context).inflate(R.layout.custom_info_window,null);
    }

    private void rendowWindowText(Marker marker, View view) {
        String title = marker.getTitle();
        TextView tvTitle = view.findViewById(R.id.title_TV);

        if (!title.equals("")) {
            tvTitle.setText(title);
        }

        String snippet = marker.getTitle();
        TextView snippetTV = view.findViewById(R.id.snippet_TV);

        if (!snippet.equals("")) {
            snippetTV.setText(snippet);
        }
    }

    @Override
    public View getInfoWindow(Marker marker) {
        rendowWindowText(marker, mWindow);
        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {

        rendowWindowText(marker, mWindow);
        return mWindow;
    }
}
