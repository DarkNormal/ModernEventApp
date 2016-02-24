package com.lordan.mark.PosseUp.UI.EventDetailGroup;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.NetworkResponse;
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
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

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


public class EventDetailsActivity extends AbstractActivity {


    private static final String TAG = "EventDetailsActivity";
    protected GoogleApiClient mGoogleApiClient;
    private RequestQueue queue;
    private int eventID = -1;
    private Event event = new Event();
    private EventDetailsLayoutBinding binding;
    private GoogleMap map;
    private SupportMapFragment fragment;
    private LatLng location;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        queue = Volley.newRequestQueue(this);
        Bundle bundle = getIntent().getExtras();
        eventID = bundle.getInt("EventID");
        location = new LatLng(bundle.getDouble("EventLat"), bundle.getDouble("EventLng"));
        if (eventID != -1) {
            getEventDetails(eventID);
        }
        binding = DataBindingUtil.setContentView(this, R.layout.event_details_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.event_detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void getEventDetails(int id) {
        String url = Constants.baseUrl + "api/Events/" + id;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Gson gson = new Gson();
                event = gson.fromJson(response.toString(), Event.class);
                event.setEndingTime();
                event.setStartingTime();
                binding.setEvent(event);
                binding.setVenue(event.getPlaceDetails());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("GETEVENTDETAILS", error.toString());

            }
        });
        queue.add(jsonObjectRequest);
    }

    public void attendEvent(View v) {
        //Used to test data binding notifying changes
        //event.setEventName("Changed event name!");

        String url = Constants.baseUrl + "/AttendEvent?id=" + eventID + "&username=" + getCurrentUsername();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //Log.i(TAG, response.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString());
            }
        });
        queue.add(jsonObjectRequest);
    }

}
