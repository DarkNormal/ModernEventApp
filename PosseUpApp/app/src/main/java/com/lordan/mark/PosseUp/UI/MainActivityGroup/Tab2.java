package com.lordan.mark.PosseUp.UI.MainActivityGroup;

/**
 * Created by Mark on 7/14/2015.
 */
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.lordan.mark.PosseUp.Model.Constants;
import com.lordan.mark.PosseUp.Model.Coordinate;
import com.lordan.mark.PosseUp.R;
import com.lordan.mark.PosseUp.UI_Elements.CustomInfoWindow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Tab2 extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private SupportMapFragment fragment;
    private GoogleMap map;
    ArrayList<Coordinate> nearbyList = new ArrayList<>();
    ArrayList<Marker> markers = new ArrayList<>();

    RequestQueue queue;

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Represents a geographical location.
     */
    protected Location mLastLocation;

    private String mLatitudeLabel;
    private String mLongitudeLabel;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_2,container,false);
        return v;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentManager fm = getChildFragmentManager();
        fragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
        if (fragment == null) {
            fragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map, fragment).commit();
        }
        queue = Volley.newRequestQueue(getActivity());
        buildGoogleApiClient();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (map == null) {
            fragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {   //returns the map asynchronously when called - auto
                    map = googleMap;
                }
            });
        }
    }
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {

        // Provides a simple way of getting a device's location and is well suited for
        // applications that do not require a fine-grained location and that do not need location
        // updates. Gets the best and most recent location currently available, which may be null
        // in rare cases when a location is not available.
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(mLastLocation != null) {
            getNearbyEvents(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        }


    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {


    }
    private void getNearbyEvents(double latitude, double longitude) {

        String url = Constants.baseUrl + "api/Events";
        JSONObject location = new JSONObject();
        try {
            location.put("Latitude", latitude);
            location.put("Longitude", longitude);
        }
        catch(JSONException je){

        }
        JsonArrayRequest jsonRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for(int i = 0; i < response.length(); i++){
                        JSONObject event = response.getJSONObject(i);
                        Coordinate c = new Coordinate(event.getDouble("EventLocationLat"),
                                event.getDouble("EventLocationLng"), event.getString("EventTitle"),
                                event.getString("EventDescription"), event.getString("EventHost"));
                        nearbyList.add(c);
                        Log.i("JSON RESPONSE", event.toString());
                    }
                    addMarkers();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley error events", String.valueOf(error.getMessage()));
            }
        });
        queue.add(jsonRequest);


    }
    private void addMarkers(){

        if(nearbyList.size() > 0 && map !=null){
            map.setInfoWindowAdapter(new CustomInfoWindow(getLayoutInflater(null)));
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Coordinate c : nearbyList) {
                builder.include(new LatLng(c.getLatitude(),c.getLongitude()));
                map.addMarker(new MarkerOptions().position(new LatLng(c.getLatitude(),c.getLongitude()))
                    .title(c.getEventTitle())
                    .snippet(c.getEventDescription()));
            }
            LatLngBounds bounds = builder.build();
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 50);
            map.moveCamera(cu);

        }
    }
}