package com.lordan.mark.PosseUp.UI.CreateEventGroup;


import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentManager;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;

import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.lordan.mark.PosseUp.AbstractActivity;

import com.lordan.mark.PosseUp.Model.Event;
import com.lordan.mark.PosseUp.R;



/**
 * Created by Mark on 10/27/2015.
 */
public class AddEventActivity extends AbstractActivity {
    public static Event newEvent;
    private LinearLayout fragmentHolder;
    private FragmentManager fragMan;
    private FirstEventFragment myFrag;
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
                    int PLACE_PICKER_REQUEST = 1;
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                    try {
                        startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
                    } catch (GooglePlayServicesRepairableException e) {
                        e.printStackTrace();
                    } catch (GooglePlayServicesNotAvailableException e) {
                        e.printStackTrace();
                    }
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
}

