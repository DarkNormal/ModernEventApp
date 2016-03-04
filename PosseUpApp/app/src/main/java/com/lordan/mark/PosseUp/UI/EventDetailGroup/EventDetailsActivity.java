package com.lordan.mark.PosseUp.UI.EventDetailGroup;


import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.lordan.mark.PosseUp.AbstractActivity;
import com.lordan.mark.PosseUp.Model.Constants;
import com.lordan.mark.PosseUp.Model.Event;
import com.lordan.mark.PosseUp.Model.User;
import com.lordan.mark.PosseUp.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Mark on 07/02/2016.
 */


public class EventDetailsActivity extends AbstractActivity implements EventDetailsFragment.OnFragmentInteractionListener, UserFragment.OnListFragmentInteractionListener{


    private static final String TAG = "EventDetailsActivity";
    private RequestQueue queue;

    private FragmentManager fragmentManager = getSupportFragmentManager();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        queue = Volley.newRequestQueue(this);
        setContentView(R.layout.scrollview_layout);
        Bundle bundle = getIntent().getExtras();
        int eventID = bundle.getInt("EventID");
        LatLng location = new LatLng(bundle.getDouble("EventLat"), bundle.getDouble("EventLng"));
        if (eventID != -1) {
            Bundle fragmentBundle = new Bundle();
            fragmentBundle.putInt("EventID", eventID);
            fragmentBundle.putString("currentUsername", getCurrentUsername());
            if(savedInstanceState == null) {
                EventDetailsFragment fragment = new EventDetailsFragment();
                fragment.setArguments(fragmentBundle);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.fragment_content_holder, fragment);
                fragmentTransaction.commit();
            }
        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.scrolling_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                //TODO fragment check
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onFragmentInteraction(Event e) {

        UserFragment fragment = new UserFragment();
        Bundle fragmentBundle = new Bundle();
        fragmentBundle.putParcelable("Event", e);
        fragment.setArguments(fragmentBundle);
        fragmentManager.beginTransaction().replace(R.id.fragment_content_holder, fragment).addToBackStack("EventAttendeeList").commit();

    }
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }

    @Override
    public void onListFragmentInteraction(User u) {
        checkFriendStatus(u);
        Toast.makeText(this, u.getUsername() + " selected", Toast.LENGTH_SHORT).show();
    }
    private void checkFriendStatus(final User u){
        String url = Constants.baseUrl + "api/FriendRequests/Check";
        JSONObject mJsonObject = new JSONObject();
        try {
            mJsonObject.put("FromUsername",getCurrentUsername());
            mJsonObject.put("ToUsername",u.getUsername());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, mJsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(EventDetailsActivity.this, u.getUsername() + " selected, is friends with you", Toast.LENGTH_SHORT).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(EventDetailsActivity.this, u.getUsername() + " selected, is not friends with you", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(jsonObjectRequest);
    }
}
