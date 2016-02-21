package com.lordan.mark.PosseUp.UI.MainActivityGroup;

/**
 * Created by Mark on 7/14/2015.
 */
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import com.github.fabtransitionactivity.SheetLayout;
import com.google.gson.Gson;
import com.lordan.mark.PosseUp.Model.Event;
import com.lordan.mark.PosseUp.Model.PlaceVenue;
import com.lordan.mark.PosseUp.UI.CreateEventGroup.AddEventActivity;

import com.lordan.mark.PosseUp.CustomAdapter;
import com.lordan.mark.PosseUp.Model.Constants;

import com.lordan.mark.PosseUp.R;
import com.lordan.mark.PosseUp.UI.EventDetailGroup.EventDetailsActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;



public class Tab1 extends Fragment implements SheetLayout.OnFabAnimationEndListener {

    private SheetLayout mSheetLayout;
    private FloatingActionButton mFab;
    private LinearLayout toolbar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private static final String TAG = "MainActivity - TAB1";

    private static final int REQUEST_CODE = 1;

    @Override
    public void onFabAnimationEnd() {
        Intent intent = new Intent(getContext(), AddEventActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    private static final int DATASET_COUNT = 10;

    private RecyclerView mRecyclerView;
    private CustomAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Event> mDataset;
    private RequestQueue queue;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.tab_1,container,false);
        v.setTag(TAG);
        mSheetLayout = (SheetLayout) v.findViewById(R.id.bottom_sheet);
        mFab = (FloatingActionButton) v.findViewById(R.id.addEvent_Button);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.cardList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mLayoutManager = new LinearLayoutManager(getActivity());



        mAdapter = new CustomAdapter(getContext(), mDataset, new CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent intent = new Intent(getContext(), EventDetailsActivity.class);
                intent.putExtra("EventID", mDataset.get(position).getEventID());
                try {
                    intent.putExtra("EventLat", mDataset.get(position).getPlaceDetails().getVenueLocation().latitude);
                    intent.putExtra("EventLng", mDataset.get(position).getPlaceDetails().getVenueLocation().longitude);
                }
                catch(NullPointerException npe){
                    Log.e(TAG, "Place Details null");
                }
                startActivity(intent);
                Toast.makeText(getContext(), "clicked position: " + position, Toast.LENGTH_SHORT).show();
            }
        });
        // Set CustomAdapter as the adapter for RecyclerView.
        mRecyclerView.setAdapter(mAdapter);


        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSheetLayout.expandFab();
                toolbar = (LinearLayout) getActivity().findViewById(R.id.main_toolbar_holder);
                toolbar.animate()
                        .alpha(0.0f)
                        .setDuration(250);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        toolbar.setVisibility(View.GONE);
                    }
                },250);
            }
        });

        mSwipeRefreshLayout =(SwipeRefreshLayout) v.findViewById(R.id.event_list_swipe);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                refreshEvents();
            }
        });

        return v;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        // Initialize dataset, this data would usually come from a local content provider or
        // remote server.
        initDataset();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE){
            if(resultCode == 0) {
                toolbar.setAlpha(1.0f);
                toolbar.setVisibility(View.VISIBLE);
                mSheetLayout.contractFab();
                Log.i("FABTRANSITION", "contracted fab");
            }
        }
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.menu_tab1_events, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.refresh_events:
                refreshEvents();
                return true;
            default:
                return false;

        }
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }
    @Override
    public void onStart(){
        super.onStart();
        mSheetLayout.setFab(mFab);
        mSheetLayout.setFabAnimationEndListener(this);
    }


    private void initDataset() {
        mDataset = new ArrayList<>();
        for (int i = 0; i < DATASET_COUNT; i++) {
            mDataset.add(new Event(1, "Event", "Event description", "host email"));
        }
    }
    private void refreshEvents() {


        queue = Volley.newRequestQueue(getActivity());
        String url = Constants.baseUrl + "api/Events";

        JsonArrayRequest jsonRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                ArrayList<Event> tempEvents = new ArrayList<>();
                try {
                    mSwipeRefreshLayout.setRefreshing(false);
                    for(int i = 0; i < response.length(); i++){
                        JSONObject event = response.getJSONObject(i);
                        Gson gson = new Gson();
                        PlaceVenue venue = gson.fromJson(event.getJSONObject("EventVenue").toString(), PlaceVenue.class);
                        Event c = new Event(event.getInt("EventID"), event.getString("EventTitle"),
                                event.getString("EventDescription"), event.getString("EventHost"), venue);
                        tempEvents.add(c);
                        Log.i("JSON RESPONSE", event.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                    mDataset.clear();
                    mDataset.addAll(tempEvents);
                    mAdapter.notifyDataSetChanged();
                    //updateList();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley error events", String.valueOf(error.getMessage()));
            }
        });
        queue.add(jsonRequest);


    }


}