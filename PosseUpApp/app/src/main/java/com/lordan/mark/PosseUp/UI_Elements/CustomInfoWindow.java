package com.lordan.mark.PosseUp.UI_Elements;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.lordan.mark.PosseUp.R;

/**
 * Created by Mark on 05/01/2016.
 */
public class CustomInfoWindow implements GoogleMap.InfoWindowAdapter {
    LayoutInflater inflater = null;
    private TextView textViewTitle, textViewDesc;

    public CustomInfoWindow(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        View v = inflater.inflate(R.layout.info_window_bg, null);
        if (marker != null) {
            textViewTitle = (TextView) v.findViewById(R.id.textViewTitle);
            textViewTitle.setText(marker.getTitle());
            textViewDesc = (TextView) v.findViewById(R.id.textViewDesc);
            textViewDesc.setText(marker.getSnippet());
        }
        return (v);
    }

    @Override
    public View getInfoContents(Marker marker) {
        return (null);
    }
}
