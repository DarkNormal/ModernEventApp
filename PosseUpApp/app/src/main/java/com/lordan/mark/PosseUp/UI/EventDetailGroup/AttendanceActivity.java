package com.lordan.mark.PosseUp.UI.EventDetailGroup;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.lordan.mark.PosseUp.Model.Constants;
import com.lordan.mark.PosseUp.Model.User;
import com.lordan.mark.PosseUp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AttendanceActivity extends AppCompatActivity implements
        NearbySubscribeFragment.OnNearbyFragmentInteractionListener{

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */

    private static final String TAG = "AttendanceActivity";
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private static final String EXTRA_EVENT_ID = "EventID";
    private static final String EXTRA_CURRENT_USER_IS_HOST = "EventIsHost";
    private static final String EXTRA_EVENT_ATTENDEES= "EventAttendees";
    private int eventID;
    private RequestQueue queue;


    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private ArrayList<User> attendeeList;

    public static Intent newIntent(Context context, int eventID, boolean isCurrentUserHost, ArrayList<User> attendees){
        Intent i = new Intent(context, AttendanceActivity.class);
        i.putExtra(EXTRA_EVENT_ID, eventID);
        i.putExtra(EXTRA_CURRENT_USER_IS_HOST, isCurrentUserHost);
        i.putExtra(EXTRA_EVENT_ATTENDEES, attendees);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        queue = Volley.newRequestQueue(this);
        Intent intent = getIntent();
        eventID = intent.getIntExtra(EXTRA_EVENT_ID, 0);
        attendeeList = intent.getParcelableArrayListExtra(EXTRA_EVENT_ATTENDEES);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_attendance, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(ArrayList<User> users, boolean[] isHere) {
        confirmUsers(users, isHere);
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position){
                case 0:
                    return NearbySubscribeFragment.newInstance(position + 1, attendeeList);
                case 1:
                    //TODO get current list of attendees for this event
                    //return Tab4.newInstance(position +1);
                default:
                    return NearbySubscribeFragment.newInstance(position + 1, attendeeList);
            }
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "NEARBY";
                case 1:
                    return "PRESENT";
            }
            return null;
        }
    }
    private void confirmUsers(ArrayList<User> confirmedAttendees, boolean[] isHere){
        JSONArray arr = new JSONArray();
        for(int i =0; i < confirmedAttendees.size(); i++){
            if(isHere[i]){
                arr.put(confirmedAttendees.get(i).getUsername());
            }
        }
        String url = Constants.baseUrl + "api/Event/" + eventID + "/ConfirmGuests";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Usernames", arr);
            Log.i(TAG, jsonObject.toString(2));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getBoolean("success")){
                        Toast.makeText(AttendanceActivity.this, "Attendees confirmed", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "got error");
            }
        });
        queue.add(jsonObjectRequest);
    }
}
