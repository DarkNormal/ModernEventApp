package com.lordan.mark.PosseUp.UI.EventDetailGroup;


import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.model.LatLng;
import com.lordan.mark.PosseUp.AbstractActivity;
import com.lordan.mark.PosseUp.Model.Event;
import com.lordan.mark.PosseUp.R;
import com.lordan.mark.PosseUp.UI.EventDetailGroup.dummy.DummyContent;

/**
 * Created by Mark on 07/02/2016.
 */


public class EventDetailsActivity extends AbstractActivity implements EventDetailsFragment.OnFragmentInteractionListener, UserFragment.OnListFragmentInteractionListener{


    private static final String TAG = "EventDetailsActivity";

    private int eventID = -1;

    private LatLng location;
    private FragmentManager fragmentManager;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_details_layout);
        Bundle bundle = getIntent().getExtras();
        eventID = bundle.getInt("EventID");
        location = new LatLng(bundle.getDouble("EventLat"), bundle.getDouble("EventLng"));
        if (eventID != -1) {
            Bundle fragmentBundle = new Bundle();
            fragmentBundle.putInt("EventID", eventID);
            fragmentBundle.putString("currentUsername", getCurrentUsername());
            EventDetailsFragment fragment = new EventDetailsFragment();
            fragment.setArguments(fragmentBundle);
            fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.event_details_content,fragment);
            fragmentTransaction.commit();
        }


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


    @Override
    public void onFragmentInteraction(Event e) {
        UserFragment fragment = new UserFragment();
        Bundle fragmentBundle = new Bundle();
        fragmentBundle.putParcelable("Event", e);
        fragment.setArguments(fragmentBundle);
        fragmentManager.beginTransaction().replace(R.id.event_details_content, fragment).addToBackStack("EventAttendeeList").commit();

    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {

    }
}
