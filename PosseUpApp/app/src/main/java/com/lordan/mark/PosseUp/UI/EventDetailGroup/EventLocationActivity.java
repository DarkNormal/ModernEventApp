package com.lordan.mark.PosseUp.UI.EventDetailGroup;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;
import com.lordan.mark.PosseUp.AbstractActivity;
import com.lordan.mark.PosseUp.Model.PlaceVenue;
import com.lordan.mark.PosseUp.R;
import com.lordan.mark.PosseUp.databinding.ActivityEventLocationBinding;

public class EventLocationActivity extends AbstractActivity implements OnMapReadyCallback {

    private PlaceVenue venue;
    private final String TAG = "EventLocationActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityEventLocationBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_event_location);
        Toolbar toolbar = (Toolbar) findViewById(R.id.event_detail_map_toolbar);
        setSupportActionBar(toolbar);
        try {
            ActionBar ab = getSupportActionBar();
            if (ab != null) {
                ab.setDisplayHomeAsUpEnabled(true);
            }
        }
        catch(NullPointerException npe){
            Log.e(TAG, "getSupportActionBar is null");
        }

        Intent i = getIntent();
        venue = i.getParcelableExtra("VenueDetails");
        binding.setVenue(venue);
        //venue.setVenueName(venueFromIntent.getVenueName());
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_map:
                String uriBegin = "geo:" + venue.getVenueLocation().latitude + "," + venue.getVenueLocation().longitude;
                String query = venue.getVenueLocation().latitude + "," + venue.getVenueLocation().longitude + "(" + venue.getVenueName() + ")";
                String encodedQuery = Uri.encode(query);
                String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
                Uri uri = Uri.parse(uriString);
                Uri location = Uri.parse("geo:37.422219,-122.08364?z=14");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Add a marker in Sydney and move the camera
        googleMap.addMarker(new MarkerOptions().position(venue.getVenueLocation()));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(venue.getVenueLocation(), 12));
    }
}
