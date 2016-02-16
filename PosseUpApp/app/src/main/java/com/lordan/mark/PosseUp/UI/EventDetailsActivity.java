package com.lordan.mark.PosseUp.UI;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.lordan.mark.PosseUp.AbstractActivity;
import com.lordan.mark.PosseUp.Model.Constants;
import com.lordan.mark.PosseUp.Model.Event;
import com.lordan.mark.PosseUp.R;
import com.lordan.mark.PosseUp.databinding.EventDetailsLayoutBinding;

import org.json.JSONObject;

/**
 * Created by Mark on 07/02/2016.
 */


public class EventDetailsActivity extends AbstractActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    protected GoogleApiClient mGoogleApiClient;
    private RequestQueue queue;
    private int eventID = -1;
    private Event event = new Event();
    private EventDetailsLayoutBinding binding;
    private GoogleMap map;
    private SupportMapFragment fragment;
    @Override
     public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        queue = Volley.newRequestQueue(this);
        Bundle bundle = getIntent().getExtras();
        eventID = bundle.getInt("EventID");
        if(eventID != -1){
            getEventDetails(eventID);
        }
        binding = DataBindingUtil.setContentView(this, R.layout.event_details_layout);


        buildGoogleMap();
    }


    private void buildGoogleMap() {
        FragmentManager fm = getSupportFragmentManager();
        fragment = (SupportMapFragment) fm.findFragmentById(R.id.event_map);
        if (fragment == null) {
            fragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map, fragment).commit();
        }
        queue = Volley.newRequestQueue(this);
        buildGoogleApiClient();

    }
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }



    public void getEventDetails(int id){
        String url = Constants.baseUrl + "api/Events/" + id;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Gson gson = new Gson();
                event = gson.fromJson(response.toString(), Event.class);
                event.setEndingTime();
                event.setStartingTime();
                binding.setEvent(event);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("GETEVENTDETAILS", error.toString());

            }
        });
        queue.add(jsonObjectRequest);
    }
    public void attendEvent(View v){
        //Used to test data binding notifying changes
        event.setEventName("Changed event name!");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (map == null) {

            fragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {   //returns the map asynchronously when called - auto
                    map = googleMap;
                    LatLng eventLocation = new LatLng(event.getEventLocationLat(), event.getEventLocationLng());
                    map.addMarker(new MarkerOptions().position(eventLocation ));

                    CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(eventLocation, 15);
                    map.moveCamera(cu);
                }
            });
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.disconnect();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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
}
