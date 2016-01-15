package com.lordan.mark.PosseUp.UI.CreateEventGroup;


import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;

import android.widget.LinearLayout;

import com.lordan.mark.PosseUp.AbstractActivity;

import com.lordan.mark.PosseUp.Model.Event;
import com.lordan.mark.PosseUp.R;



/**
 * Created by Mark on 10/27/2015.
 */
public class AddEventActivity extends AbstractActivity {
    private ViewPager pager;
    public static Event newEvent;
    public MyPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addevent_layout);

        setupToolbar();

        pager = (ViewPager) findViewById(R.id.add_event_pager);
        adapter = new MyPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);

    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.add_event_toolbar);
        setSupportActionBar(toolbar);
        // Show menu icon
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_cancel_light);

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
                nextInPager();
                break;
            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }
    private void nextInPager(){
        if(pager.getCurrentItem() == 0){
            pager.setCurrentItem(1);
        }
        else finish();
    }


    private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            switch (pos) {

                case 0:
                    return FirstEventFragment.newInstance("FirstFragment, Instance 1");
                case 1:
                    return SecondEventFragment.newInstance("SecondFragment, Instance 1");
                default:
                    return FirstEventFragment.newInstance("ThirdFragment, Default");
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
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

