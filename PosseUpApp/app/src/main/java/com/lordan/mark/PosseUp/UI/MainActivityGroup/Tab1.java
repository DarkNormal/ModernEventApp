package com.lordan.mark.PosseUp.UI.MainActivityGroup;

/**
 * Created by Mark on 7/14/2015
 */
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;


import com.google.gson.Gson;
import com.lordan.mark.PosseUp.DataOperations.AzureService;
import com.lordan.mark.PosseUp.Model.Event;
import com.lordan.mark.PosseUp.Model.PlaceVenue;
import com.lordan.mark.PosseUp.UI.CreateEventGroup.AddEventActivity;

import com.lordan.mark.PosseUp.util.CustomAdapter;
import com.lordan.mark.PosseUp.Model.Constants;

import com.lordan.mark.PosseUp.R;
import com.lordan.mark.PosseUp.UI.EventDetailGroup.EventDetailsActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class Tab1 extends Fragment{

    @Bind(R.id.addEvent_Button) public FloatingActionButton mFab;
    @Bind(R.id.event_list_swipe) public SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.cardList) public RecyclerView mRecyclerView;
    @Bind(R.id.coordinatorLayout) public CoordinatorLayout mCoordinatorLayout;
    @Bind(R.id.loading_event_message_holder) public RelativeLayout mLoadingContentHolder;
    @Bind(R.id.failed_loading_event_message_holder) public RelativeLayout mFailedLoadingContentHolder;
    @Bind(R.id.try_refresh_button) public AppCompatButton mTryRefreshButton;
    private static final String TAG = "MainActivity - TAB1";

    private static final int REQUEST_CODE = 1;
    private CustomAdapter mAdapter;
    private List<Event> mDataset;
    private RequestQueue queue;
    private String url = Constants.baseUrl + "api/Events";


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_1, container, false);
        ButterKnife.bind(this, v);
        v.setTag(TAG);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new CustomAdapter(getContext(), mDataset, new CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent intent = EventDetailsActivity.newIntent(getContext(),mDataset.get(position).getEventID(),
                        TextUtils.equals(new AzureService().getCurrentEmail(getContext()), mDataset.get(position).getHostEmail()));
                startActivity(intent);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        getActivity().setTitle(getString(R.string.tab1));

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshEvents();
            }
        });

        return v;
    }
    @OnClick(R.id.addEvent_Button)
    public void addEvent(){
        Intent intent = new Intent(getContext(), AddEventActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }
    @OnClick(R.id.try_refresh_button)
    public void tryRefreshEvents(){
        mFailedLoadingContentHolder.setVisibility(View.GONE);
        mLoadingContentHolder.setVisibility(View.VISIBLE);
        refreshEvents();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        // Initialize dataset, this data would usually come from a local content provider or
        // remote server.
        queue = Volley.newRequestQueue(getContext());
        initDataset();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){
                //event created successfully
                Snackbar alert = Snackbar.make(mCoordinatorLayout, "Event created", Snackbar.LENGTH_LONG)
                        .setAction("View", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //last param in intent is always true as the host will be the one creating and seeing this popup
                                Intent intent = EventDetailsActivity.newIntent(getContext(),data.getIntExtra("EventID", 0), true);
                                startActivity(intent);
                            }
                        })
                        .setActionTextColor(Color.MAGENTA);
                View v = alert.getView();
                TextView textView = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.WHITE);
                alert.show();
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

    private void initDataset() {
        mDataset = new ArrayList<>();
        refreshEvents();
    }
    private void refreshEvents() {
        JsonArrayRequest jsonRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                mSwipeRefreshLayout.setRefreshing(false);
                FetchEventsTask task = new FetchEventsTask();
                task.execute(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mSwipeRefreshLayout.setRefreshing(false);
                mLoadingContentHolder.setVisibility(View.GONE);
                if (mSwipeRefreshLayout.getVisibility() != View.VISIBLE){
                    mFailedLoadingContentHolder.setVisibility(View.VISIBLE);
                }
                NetworkResponse response = error.networkResponse;
                if(response != null){
                    displaySnack(response.statusCode);
                    Log.e(TAG, response.statusCode + " " + response.toString());
                }
                else{
                    displaySnack(0);
                    Log.e(TAG, "Volley refresh events error");
                }
            }
        });
        queue.add(jsonRequest);


    }
    private void displaySnack(int errorCode){
        //displays a custom snackbar based on the error code
        Snackbar alert;
        switch (errorCode){
            case 401:
                //Unauthorized - token probably gone
                alert = Snackbar.make(mFab, "Authentication Failed", Snackbar.LENGTH_LONG).setAction("REFRESH", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        refreshEvents();
                    }
                });


                alert.setActionTextColor(Color.RED);
                break;
            case 403:
            case 503:
                //Forbidden - service unavailable or stopped
                alert = Snackbar.make(mFab, "Service Unavailable", Snackbar.LENGTH_LONG).setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO refresh token
                    }
                });
                alert.setActionTextColor(Color.YELLOW);
                break;
            default:
                alert = Snackbar.make(mFab, getString(R.string.fail_loading_event), Snackbar.LENGTH_LONG).setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mFailedLoadingContentHolder.getVisibility() == View.VISIBLE) {
                            mFailedLoadingContentHolder.setVisibility(View.GONE);
                            mLoadingContentHolder.setVisibility(View.VISIBLE);
                        }
                        refreshEvents();
                    }
                });
                alert.setActionTextColor(Color.YELLOW);

                break;
        }
        View v = alert.getView();
        TextView textView = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        alert.show();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
    public class FetchEventsTask extends AsyncTask<JSONArray, Void, ArrayList<Event>>{

        @Override
        protected ArrayList<Event> doInBackground(JSONArray... jsonArrays) {
            final JSONArray response = jsonArrays[0];
            ArrayList<Event> tempEvents = new ArrayList<>();
            try {
                for(int i = 0; i < response.length(); i++){
                    JSONObject event = response.getJSONObject(i);
                    PlaceVenue venue = new Gson().fromJson(event.getJSONObject("EventVenue").toString(), PlaceVenue.class);
                    Event c = new Gson().fromJson(response.getJSONObject(i).toString(), Event.class);
                    c.setEndingTime();
                    c.setStartingTime();
                    c.setEventImageURL(c.getEventImage());
                    c.setPlaceDetails(venue);
                    if(c.getStartTimeCalendar().after(Calendar.getInstance())) {
                        tempEvents.add(c);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return tempEvents;
        }

        @Override
        protected void onPostExecute(ArrayList<Event> arrayList) {
            super.onPostExecute(arrayList);
            mSwipeRefreshLayout.setVisibility(View.VISIBLE);
            mLoadingContentHolder.setVisibility(View.GONE);
            mDataset.clear();
            mDataset.addAll(arrayList);
            mAdapter.notifyDataSetChanged();
        }
    }
}