package com.lordan.mark.PosseUp.UI.MainActivityGroup;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.lordan.mark.PosseUp.ChatAdapter;
import com.lordan.mark.PosseUp.CustomAdapter;
import com.lordan.mark.PosseUp.DataOperations.AzureService;
import com.lordan.mark.PosseUp.Model.Constants;
import com.lordan.mark.PosseUp.Model.Event;
import com.lordan.mark.PosseUp.Model.PlaceVenue;
import com.lordan.mark.PosseUp.R;
import com.lordan.mark.PosseUp.UI.EventDetailGroup.AttendActivity;
import com.lordan.mark.PosseUp.UI.EventDetailGroup.AttendanceActivity;
import com.lordan.mark.PosseUp.UI.EventDetailGroup.EventDetailsActivity;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChatFragment.OnChatFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = "ChatFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnChatFragmentInteractionListener mListener;
    private Pubnub pubnub;
    private ChatAdapter mAdapter;
    private ArrayList<Event> mDataset = new ArrayList<>();

    public ChatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatFragment newInstance(String param1, String param2) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        pubnub = new Pubnub("pub-c-80485b35-97d9-4403-8465-c5a6e2547d65", "sub-c-2b32666a-f73e-11e5-8cfb-0619f8945a4f");


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chat, container, false);
        // Inflate the layout for this fragment
        subscribeWithPubNub();
        Callback callback = new Callback() {
            public void successCallback(String channel, Object response) {
                System.out.println(response.toString());
            }
            public void errorCallback(String channel, PubnubError error) {
                System.out.println(error.toString());
            }
        };
        pubnub.publish("test_channel", "Hello from the PubNub Java SDK!" , callback);

        RecyclerView mRecyclerView = (RecyclerView) v.findViewById(R.id.chat_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new ChatAdapter(getContext(), mDataset, new CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                startActivity(ChatActivity.newIntent(getContext(), mDataset.get(position).getEventName()));
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        getChats();
        return v;
    }

    private void subscribeWithPubNub() {
        try {
            pubnub.subscribe("test_channel", new Callback() {
                @Override
                public void connectCallback(String channel, Object message) {
                    System.out.println("SUBSCRIBE : CONNECT on channel:" + channel
                            + " : " + message.getClass() + " : "
                            + message.toString());
                }

                @Override
                public void disconnectCallback(String channel, Object message) {
                    System.out.println("SUBSCRIBE : DISCONNECT on channel:" + channel
                            + " : " + message.getClass() + " : "
                            + message.toString());
                }

                public void reconnectCallback(String channel, Object message) {
                    System.out.println("SUBSCRIBE : RECONNECT on channel:" + channel
                            + " : " + message.getClass() + " : "
                            + message.toString());
                }

                @Override
                public void successCallback(String channel, Object message) {
                    System.out.println("SUBSCRIBE : " + channel + " : "
                            + message.getClass() + " : " + message.toString());
                }

                @Override
                public void errorCallback(String channel, PubnubError error) {
                    System.out.println("SUBSCRIBE : ERROR on channel " + channel
                            + " : " + error.toString());
                }
            });
        } catch (PubnubException e) {
            e.printStackTrace();
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onChatFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnChatFragmentInteractionListener) {
            mListener = (OnChatFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    private void getChats() {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = Constants.baseUrl + "api/Account/UserInfo/" + new AzureService().getCurrentUsername(getContext()).replace(" ", "%20");
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mDataset.clear();
                try {
                    JSONArray events = response.getJSONArray("Events");
                    for(int i = 0; i < events.length(); i++){
                        JSONObject jsonEvent = events.getJSONObject(i);
                        Event tempEvent = new Gson().fromJson(jsonEvent.toString(), Event.class);
                        mDataset.add(tempEvent);
                        mAdapter.notifyItemInserted(mDataset.size()-1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //updateList();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse response = error.networkResponse;
                if(response != null){
                    Log.e(TAG, response.statusCode + " " + response.toString());
                }
                else{
                    Log.e(TAG, "Volley refresh events error");
                }

            }
        });
        queue.add(jsonRequest);


    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnChatFragmentInteractionListener {
        // TODO: Update argument type and name
        void onChatFragmentInteraction(Uri uri);
    }

}
