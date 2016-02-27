package com.lordan.mark.PosseUp.UI.EventDetailGroup;

import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.os.Bundle;


import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.google.android.gms.maps.model.LatLng;


import com.google.gson.Gson;
import com.lordan.mark.PosseUp.AbstractActivity;
import com.lordan.mark.PosseUp.Model.Constants;
import com.lordan.mark.PosseUp.Model.Event;
import com.lordan.mark.PosseUp.R;
import com.lordan.mark.PosseUp.databinding.EventDetailsLayoutBinding;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Mark on 07/02/2016.
 */


public class EventDetailsActivity extends AbstractActivity {


    private static final String TAG = "EventDetailsActivity";
    private RequestQueue queue;
    private int eventID = -1;
    private Event event = new Event();
    private EventDetailsLayoutBinding binding;
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

        AppCompatImageView directions = (AppCompatImageView) findViewById(R.id.directions_button);
        directions.setImageAlpha(128);
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
                try {
                    event.setAttendees(response.getJSONArray("EventAttendees"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                binding.setEvent(event);
                displayAttendeeList();
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
    private void displayAttendeeList(){

        int numberOfGuests = event.getAttendees().size();
        TextView numGuestsHeading =(TextView) findViewById(R.id.event_guests_heading);
        numGuestsHeading.setText("guests (" + numberOfGuests +")");
        if(numberOfGuests > 4){
            displayAttendee(4);
            TextView extraGuests = new TextView(this);
            extraGuests.setText("+" + (numberOfGuests - 4) + " guests" );
        }
        else{
            displayAttendee(numberOfGuests);
        }


    }
    private void displayAttendee(int loopVar){
        LinearLayout attendeeHolder = (LinearLayout) findViewById(R.id.event_details_pictures);
        int pixelsDP = Math.round(convertPixelsToDp(55));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams( pixelsDP, pixelsDP );
        for (int i = 0; i < loopVar; i++) {
            CircularImageView attendee = new CircularImageView(this);

            attendee.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.profiler));
            attendee.setScaleType(ImageView.ScaleType.FIT_CENTER);
            attendee.setLayoutParams(layoutParams);
            attendeeHolder.addView(attendee);
        }
    }

    public void attendEvent(View v) {

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

    public int convertPixelsToDp(int dp) {
        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return Math.round(px);
    }
}
