package com.lordan.mark.PosseUp.UI.MainActivityGroup;

/**
 * Created by Mark on 7/15/2015
 */
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.lordan.mark.PosseUp.CustomAdapter;
import com.lordan.mark.PosseUp.Model.Constants;
import com.lordan.mark.PosseUp.Model.Event;
import com.lordan.mark.PosseUp.R;
import com.lordan.mark.PosseUp.SimpleAdapter;
import com.lordan.mark.PosseUp.SimpleSectionedRecyclerViewAdapter;
import com.lordan.mark.PosseUp.UI.DividerItemDecoration;
import com.lordan.mark.PosseUp.UI.EventDetailGroup.EventDetailsActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Tab3 extends Fragment {

    private List<Event> eventDataSet, pastDataSet, upcomingDataSet;
    private String username;
    private final String TAG = "TAB3";
    private RequestQueue queue;
    private RecyclerView eventRecycler;
    private SimpleAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private List<SimpleSectionedRecyclerViewAdapter.Section> sections;
    private View v;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.tab_3,container,false);

        eventRecycler = (RecyclerView) v.findViewById(R.id.eventsRecycler);
        eventRecycler.setHasFixedSize(true);
        eventRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        eventRecycler.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        sections = new ArrayList<>();
        mSwipeRefreshLayout =(SwipeRefreshLayout) v.findViewById(R.id.event_list_swipe);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                getEvents();
            }
        });
        return v;
    }
    private void getEvents(){
        getUserDetails(new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    Gson gson = new Gson();
                    pastDataSet.clear();
                    upcomingDataSet.clear();
                    for (int i = 0; i < result.getJSONArray("Events").length(); i++) {

                        Event e = gson.fromJson(result.getJSONArray("Events").getJSONObject(i).toString(), Event.class);
                        Calendar now = Calendar.getInstance();
                        if(e.getTime().after(now)){
                            Log.i(TAG, "event in the future");
                            upcomingDataSet.add(gson.fromJson(result.getJSONArray("Events").getJSONObject(i).toString(), Event.class));
                        }
                        else{
                            Log.i(TAG, "event in the past");
                            pastDataSet.add(gson.fromJson(result.getJSONArray("Events").getJSONObject(i).toString(), Event.class));
                        }

                    }
                    eventDataSet.clear();

                    eventDataSet.addAll(upcomingDataSet);
                    sections.clear();
                    //Sections
                    sections.add(new SimpleSectionedRecyclerViewAdapter.Section(0,"UPCOMING"));
                    sections.add(new SimpleSectionedRecyclerViewAdapter.Section(eventDataSet.size(),"PAST"));
                    eventDataSet.addAll(pastDataSet);

                    mAdapter = new SimpleAdapter(getContext(), eventDataSet, new CustomItemClickListener() {
                        @Override
                        public void onItemClick(View v, int position) {
                            if(position > upcomingDataSet.size()){
                                position -= 2;
                            }
                            else position -=1;
                            Intent intent = new Intent(getContext(), EventDetailsActivity.class);
                            intent.putExtra("EventID", eventDataSet.get(position).getEventID());
                            try {
                                intent.putExtra("EventLat", eventDataSet.get(position).getPlaceDetails().getVenueLocation().latitude);
                                intent.putExtra("EventLng", eventDataSet.get(position).getPlaceDetails().getVenueLocation().longitude);
                            }
                            catch(NullPointerException npe){
                                Log.e(TAG, "Place Details null");
                            }
                            startActivity(intent);
                            Toast.makeText(getContext(), "clicked position: " + position, Toast.LENGTH_SHORT).show();
                        }
                    });
                    SimpleSectionedRecyclerViewAdapter.Section[] dummy = new SimpleSectionedRecyclerViewAdapter.Section[sections.size()];
                    SimpleSectionedRecyclerViewAdapter mSectionedAdapter = new
                            SimpleSectionedRecyclerViewAdapter(getContext(),R.layout.section,R.id.section_text,mAdapter);
                    mSectionedAdapter.setSections(sections.toArray(dummy));
                    eventRecycler.setAdapter(mSectionedAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getUserDetails(final VolleyCallback callback) {
        String url = Constants.baseUrl + "api/Account/UserInfo/" + username.replace(" ", "%20");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mSwipeRefreshLayout.setRefreshing(false);
                callback.onSuccess(response);
                Log.i(TAG, "got response");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mSwipeRefreshLayout.setRefreshing(false);
                Log.e(TAG, "got error");

            }
        });
        queue.add(jsonObjectRequest);
    }
    public interface VolleyCallback{
        void onSuccess(JSONObject result);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        username = args.getString("username");
        queue = Volley.newRequestQueue(getContext());
        //setHasOptionsMenu(true);
        initDataset();

    }

    private void initDataset() {
        eventDataSet = new ArrayList<>();
        upcomingDataSet = new ArrayList<>();
        pastDataSet = new ArrayList<>();
        getEvents();
    }

    private void onEventClick(List<Event> mDataset, int position){
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

}