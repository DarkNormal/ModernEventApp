package com.lordan.mark.PosseUp.UI;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


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

    public static final String TAG = "AddEventActivity";

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
        addPlacesListener();
        configDateTimeChooser();

        setupToolbar();

    }

    private void setupToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.add_event_toolbar);
        setSupportActionBar(toolbar);
        // Show menu icon
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_cancel_light);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void configDateTimeChooser() {
        final EditText dateInput = (EditText) findViewById(R.id.create_event_date);
        Calendar today = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("d/M/y");
        dateInput.setHint(format.format(today.getTime()));
        dateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //To show current date in the datepicker
                Calendar mcurrentDate = Calendar.getInstance();
                int mYear = mcurrentDate.get(Calendar.YEAR);
                int mMonth = mcurrentDate.get(Calendar.MONTH);
                int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker;
                mDatePicker = new DatePickerDialog( AddEventActivity.this,  new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        // TODO Auto-generated method stub
                    /*      Your code   to get date and time    */
                        selectedmonth = selectedmonth + 1;
                        dateInput.setText("" + selectedday + "/" + selectedmonth + "/" + selectedyear);
                    }
                }, mYear, mMonth, mDay);
                mDatePicker.show();
            }
        });
        final EditText timeInput = (EditText) findViewById(R.id.create_event_time);
        final SimpleDateFormat timeFormat = new SimpleDateFormat("kk:mm");
        timeInput.setHint(timeFormat.format(today.getTime()));
        timeInput.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Calendar mcurrentTime = Calendar.getInstance();
                    int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                    int minute = mcurrentTime.get(Calendar.MINUTE);
                    TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog(AddEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            boolean addZeroHour = false;
                            boolean addZeroMinute = false;
                            if(selectedHour < 10){
                                addZeroHour = true;
                            }
                            if(selectedMinute < 10){
                                addZeroMinute = true;
                            }
                            if(addZeroHour && !addZeroMinute){
                                timeInput.setText( "0" +selectedHour + ":" + selectedMinute);
                            }
                            else if(!addZeroHour && addZeroMinute){
                                timeInput.setText(selectedHour + ":0" + selectedMinute);
                            }
                            else if(addZeroHour && addZeroMinute){
                                timeInput.setText( "0" +selectedHour + ":0" + selectedMinute);
                            }
                            else{
                                timeInput.setText(selectedHour + ":" + selectedMinute);
                            }

                        }
                    }, hour, minute, true);//Yes 24 hour time

                    mTimePicker.show();

                }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.add_event_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case(android.R.id.home):
                finish();
                break;
            case(R.id.create_event_next):
                finish();
                break;
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
                "TODO",
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
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
    }
    private void addPlacesListener(){
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);


        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });
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
