package com.lordan.mark.PosseUp.UI.ProfileGroup;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import com.android.volley.toolbox.Volley;

import com.lordan.mark.PosseUp.Model.Constants;
import com.lordan.mark.PosseUp.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class ProfileFriendList extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private RequestQueue queue;
    private ArrayList<String> list = new ArrayList<>();
    private SwipeRefreshLayout mSwipeRefreshLayout;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        queue = Volley.newRequestQueue(getContext());
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend_list, container, false);


        mRecyclerView = (RecyclerView) view.findViewById(R.id.friend_list);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new FriendListAdapter(list);
        mRecyclerView.setAdapter(mAdapter);
        getFriends();
        mSwipeRefreshLayout =(SwipeRefreshLayout) view.findViewById(R.id.friend_list_swipe);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                updateUserList();
            }
        });
        return view;
    }
    private void updateUserList(){
        getFriends();
    }

    private void getFriends(){
        String url = Constants.baseUrl + "api/Account/GetUsers";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                ArrayList<String> tempUsers = new ArrayList<>();
                try {
                    for (int i = 0; i < response.length(); i++) {
                        String user = response.getString(i);
                        tempUsers.add(user);
                    }
                    list.clear();
                    list.addAll(tempUsers);
                    mAdapter.notifyDataSetChanged();
                    mSwipeRefreshLayout.setRefreshing(false);
                }
                catch(JSONException e){
                    Log.e("GETFRIENDS", e.getMessage());
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("GETFRIENDS", error.getMessage());
            }
        });
        queue.add(jsonArrayRequest);
    }
}
