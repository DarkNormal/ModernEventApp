package com.lordan.mark.PosseUp.UI;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.lordan.mark.PosseUp.AbstractActivity;
import com.lordan.mark.PosseUp.Model.Constants;
import com.lordan.mark.PosseUp.Model.Event;
import com.lordan.mark.PosseUp.R;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Mark on 10/27/2015.
 */
public class AddEventActivity extends AbstractActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    private RequestQueue queue;
    private GoogleMap map;
    private FragmentManager fm;
    private SupportMapFragment fragment;
    private SwitchCompat mSwitch;
    private Marker marker;
    private Spinner spinner;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addevent_layout);

        spinner = (Spinner) findViewById(R.id.create_event_type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.event_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        mSwitch = (SwitchCompat) findViewById(R.id.switch_event);
        if(mSwitch != null){
            mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if(isChecked == true){
                        fm.beginTransaction().hide(fragment).commit();
                    }
                    else{
                        fm.beginTransaction().show(fragment).commit();
                    }
                }
            });
        }
        fm = getSupportFragmentManager();
        buildFragment();
        buildGoogleApiClient();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.add_event_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case(R.id.create_event_cancel):
                finish();
                break;
            case(R.id.create_event_next):
                finish();
            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    private String getInputValues(View holder){
        EditText editView = (EditText) holder;
        return editView.getText().toString();

    }
    private void createEvent(){
        Event newEvent = new Event(
                getInputValues(findViewById(R.id.create_event_title_input)),
                spinner.getSelectedItem().toString(),
                getCurrentEmail(),
                getInputValues(findViewById(R.id.create_event_desc_field)),
                marker.getPosition().latitude,
                marker.getPosition().longitude);

        createEvent(newEvent);
    }
    private void createEvent(Event newEvent){
        queue = Volley.newRequestQueue(this);
        if(newEvent.getHostEmail() != null){
            JSONObject jsonObject = convertToJSON(newEvent);

            String url = Constants.baseUrl + "api/Events";
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.i("Add-Event", response.toString());
                    Toast.makeText(getApplicationContext(), "Event added to database", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Add-Event", error.networkResponse.toString());
                    Toast.makeText(getApplicationContext(), "Error adding event to database", Toast.LENGTH_SHORT).show();
                }
            });
            queue.add(request);
        }
    }
    private JSONObject convertToJSON(Event obj){
        Gson gson = new Gson();
        String jsonString = gson.toJson(obj);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;

    }
    @Override
    public void onResume() {
        super.onResume();
        if (map == null) {
            fragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {   //returns the map asynchronously when called - auto
                    map = googleMap;
                    map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                        @Override
                        public void onMapLongClick(LatLng latLng) {
                            if(marker != null) {
                                marker.remove();
                            }
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(latLng);
                                markerOptions.draggable(true);
                                markerOptions.title("Location");
                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                                marker = map.addMarker(markerOptions);


                        }
                    });
                }
            });
        }
    }
    private void buildFragment(){
        FragmentManager fm = getSupportFragmentManager();
        fragment = (SupportMapFragment) fm.findFragmentById(R.id.map_event);
        if (fragment == null) {
            fragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map_event, fragment).commit();
        }
    }
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
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

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
