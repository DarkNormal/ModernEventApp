package com.lordan.mark.PosseUp.UI.CreateEventGroup;



import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentManager;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;

import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.google.gson.Gson;
import com.lordan.mark.PosseUp.AbstractActivity;

import com.lordan.mark.PosseUp.DataOperations.AzureService;
import com.lordan.mark.PosseUp.Model.Constants;
import com.lordan.mark.PosseUp.Model.Event;
import com.lordan.mark.PosseUp.R;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Mark on 10/27/2015.
 */
public class AddEventActivity extends AbstractActivity {
    public static Event newEvent;
    private LinearLayout fragmentHolder;
    private FragmentManager fragMan;
    private FirstEventFragment myFrag;
    private RequestQueue queue;
    Fragment secondFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addevent_layout);

        setupToolbar();


        fragmentHolder = (LinearLayout) findViewById(R.id.add_event_fragment_holder);
        fragMan = getSupportFragmentManager();
        FragmentTransaction fragTransaction = fragMan.beginTransaction();
        myFrag = new FirstEventFragment();
        fragTransaction.add(fragmentHolder.getId(), myFrag, "add_event_basic");
        fragTransaction.commit();
        queue = Volley.newRequestQueue(this);

    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.add_event_toolbar);
        setSupportActionBar(toolbar);
        // Show menu icon
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_action_cancel);

        ab.setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_event_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (android.R.id.home):
                android.app.FragmentManager fm = getFragmentManager();
                ConfirmExitDialog confirm = new ConfirmExitDialog();
                confirm.show(fm, "confirm_dialog");
                break;
            case (R.id.create_event_next):
                newEvent = myFrag.getEvent();
                if(newEvent != null){
                    AzureService az = new AzureService();
                    newEvent.setHostEmail(az.getCurrentEmail(this));
                    sendEvent(newEvent);
                }
                break;
            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }
    private void switchFragment(){
        FragmentTransaction fragTransaction = fragMan.beginTransaction();
        secondFrag = new SecondEventFragment();
        fragTransaction.replace(fragmentHolder.getId(), secondFrag, "add_event_location");
        fragTransaction.commit();
    }

    public static class ConfirmExitDialog extends DialogFragment{
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Discard Event?")
                    .setMessage("All progress will be lost")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            getActivity().finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dismiss();
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }
    private void sendEvent(Event e){
        String url = Constants.baseUrl + "api/Events";
        Gson gson = new Gson();
        String event = gson.toJson(e);
        JSONObject eventObj = new JSONObject();
        try {
            eventObj = new JSONObject(event);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, eventObj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("Create Event", response.toString());
                Snackbar.make(findViewById(R.id.coordinatorLayout), "Event created", Snackbar.LENGTH_LONG)
                        .setAction("View", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                finish();
                            }
                        })
                        .setActionTextColor(Color.MAGENTA)
                        .show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Create Event Error", error.getMessage());
            }
        });
        queue.add(jsonObjectRequest);
    }
}

