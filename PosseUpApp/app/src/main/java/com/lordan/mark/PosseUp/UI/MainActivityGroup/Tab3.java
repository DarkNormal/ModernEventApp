package com.lordan.mark.PosseUp.UI.MainActivityGroup;

/**
 * Created by Mark on 7/15/2015
 */
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Tab3 extends Fragment {

    private List<Event> pastDataset, upcomingDataset;
    private String username;
    private final String TAG = "TAB3";
    private RequestQueue queue;
    private CustomAdapter pastAdapter, upcomingAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.tab_3,container,false);
        RecyclerView pastRecyclerView = (RecyclerView) v.findViewById(R.id.pastEventsRecycler);
        RecyclerView upcomingRecyclerView = (RecyclerView) v.findViewById(R.id.upcomingEventsRecycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        LinearLayoutManager nextLayoutManager = new LinearLayoutManager(getContext());

        nextLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        pastRecyclerView.setLayoutManager(layoutManager);
        upcomingRecyclerView.setLayoutManager(nextLayoutManager);

        getUserDetails(new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    Gson gson = new Gson();
                    for (int i = 0; i < result.getJSONArray("Events").length(); i++) {

                        Event e = gson.fromJson(result.getJSONArray("Events").getJSONObject(i).toString(), Event.class);
                        Calendar now = Calendar.getInstance();
                        if(e.getTime().after(now)){
                            Log.i(TAG, "event in the future");
                            upcomingDataset.add(gson.fromJson(result.getJSONArray("Events").getJSONObject(i).toString(), Event.class));
                        }
                        else{
                            Log.i(TAG, "event in the past");
                            pastDataset.add(gson.fromJson(result.getJSONArray("Events").getJSONObject(i).toString(), Event.class));
                        }

                    }
                    pastAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        pastAdapter = new CustomAdapter(getContext(), pastDataset, new CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Toast.makeText(getContext(), "clicked position: " + position, Toast.LENGTH_SHORT).show();
            }
        });
        pastRecyclerView.setAdapter(pastAdapter);
        upcomingAdapter = new CustomAdapter(getContext(), upcomingDataset, new CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Toast.makeText(getContext(), "clicked position: " + position, Toast.LENGTH_SHORT).show();
            }
        });
        upcomingRecyclerView.setAdapter(upcomingAdapter);

        return v;
    }
    private void getEvents(){

    }

    private void getUserDetails(final VolleyCallback callback) {
        String url = Constants.baseUrl + "api/Account/UserInfo/" + username.replace(" ", "%20");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.onSuccess(response);
                Log.i(TAG, "got response");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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
        pastDataset = new ArrayList<>();
        upcomingDataset = new ArrayList<>();
        getEvents();
    }

}