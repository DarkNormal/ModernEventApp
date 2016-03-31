package com.lordan.mark.PosseUp.UI.EventDetailGroup;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.lordan.mark.PosseUp.Model.Constants;
import com.lordan.mark.PosseUp.Model.Event;
import com.lordan.mark.PosseUp.Model.User;
import com.lordan.mark.PosseUp.R;
import com.lordan.mark.PosseUp.UI.ProfileGroup.ProfileActivity;
import com.lordan.mark.PosseUp.databinding.FragmentEventDetailsBinding;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

import org.json.JSONException;
import org.json.JSONObject;

public class EventDetailsFragment extends Fragment implements View.OnClickListener{

    private final String TAG = "EventDetailsFragment";



    private Event event;
    private int eventID;
    private View v;
    private String currentUser;

    private RequestQueue queue;
    private FragmentEventDetailsBinding mBinding;
    private OnFragmentInteractionListener mListener;

    public EventDetailsFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Bundle bundle = this.getArguments();
        eventID = bundle.getInt("EventID");

        currentUser = bundle.getString("currentUsername");

        if(event != null) {
            mBinding.setEvent(event);
            mBinding.setVenue(event.getPlaceDetails());
        }
        else {
            mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_event_details, container, false);
            queue = Volley.newRequestQueue(getContext());
        }



            v = mBinding.getRoot();
            AppCompatImageView directions = (AppCompatImageView) v.findViewById(R.id.directions_button);
            directions.setImageAlpha(128);
            directions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    LatLng location = event.getPlaceDetails().getVenueLocation();
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?daddr=" + location.latitude + "," + location.longitude));
                    startActivity(intent);
                }
            });
        RelativeLayout holder = (RelativeLayout) v.findViewById(R.id.event_location_holder);
        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng location = event.getPlaceDetails().getVenueLocation();
                String label = event.getPlaceDetails().getVenueName();
                String uriBegin = "geo:" + location.latitude + "," + location.longitude;
                String query = location.latitude+"," + location.longitude + "(" + label +")";
                String encodedQuery = Uri.encode(query);
                String uriString = uriBegin + "?q=" + encodedQuery;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse( uriString + "&z=12"));
                startActivity(intent);
            }
        });
            Button viewAll = (Button) v.findViewById(R.id.event_guests_button);
            viewAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onFragmentInteraction(event);
                    }
                }
            });

        mBinding.attendButton.setOnClickListener(this);

        final Pubnub pubnub = new Pubnub("pub-c-80485b35-97d9-4403-8465-c5a6e2547d65", "sub-c-2b32666a-f73e-11e5-8cfb-0619f8945a4f");

        try {
            pubnub.subscribe("my_channel", new Callback() {
                        @Override
                        public void connectCallback(String channel, Object message) {
                            pubnub.publish("my_channel", "Hello from the PubNub Java SDK", new Callback() {});
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
                    }
            );
        } catch (PubnubException e) {
            System.out.println(e.toString());
        }
        return v;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("event", event);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            //probably orientation change
            event = savedInstanceState.getParcelable("event");
            mBinding.setEvent(event);
            mBinding.setVenue(event.getPlaceDetails());
            displayAttendeeList();
        } else {
            if (event == null) {
                Log.i(TAG, "null savedInstanceState");
                if (eventID != -1) {
                    getEventDetails(eventID);
            }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.attend_button:
                String btnText = mBinding.attendButton.getText().toString().toUpperCase();
                if(btnText.equals("RSVP")) {
                    interactEvent(true);
                }
                else{
                    interactEvent(false);
                }
                break;
            default:
                break;
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Event e);
    }

    private void getEventDetails(int id) {
        String url = Constants.baseUrl + "api/Events/" + id;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Gson gson = new Gson();
                event = new Event();
                event = gson.fromJson(response.toString(), Event.class);
                event.setEndingTime();
                event.setStartingTime();
                try {
                    event.setAttendees(response.getJSONArray("EventAttendees"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                boolean isUserAttending = false;
                for (User u: event.getAttendees()) {
                    if(u.getUsername().equals(currentUser)){
                        mBinding.attendButton.setText(getString(R.string.leave));
                        isUserAttending = true;
                        break;
                    }
                }
                mBinding.setEvent(event);
                displayAttendeeList();
                mBinding.setVenue(event.getPlaceDetails());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("GETEVENTDETAILS", error.toString());

            }
        });
        queue.add(jsonObjectRequest);
    }
    private void displayAttendeeList(){

        int numberOfGuests = event.getAttendees().size();
        TextView numGuestsHeading =(TextView) v.findViewById(R.id.event_guests_heading);
        numGuestsHeading.setText(getString(R.string.guests_heading, numberOfGuests));
        if(numberOfGuests > 4){
            displayAttendee(4);
            TextView extraGuests = new TextView(getContext());
            extraGuests.setText(getString(R.string.extra_guests, numberOfGuests - 4));
        }
        else{
            displayAttendee(numberOfGuests);
        }


    }
    private void displayAttendee(final int loopVar){
        LinearLayout attendeeHolder = (LinearLayout) v.findViewById(R.id.event_details_pictures);
        attendeeHolder.removeAllViews();
        int pixelsDP = Math.round(convertPixelsToDp(55));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams( pixelsDP, pixelsDP );
        for (int i = 0; i < loopVar; i++) {
            CircularImageView attendee = new CircularImageView(getContext());

            attendee.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.profiler));
            attendee.setScaleType(ImageView.ScaleType.FIT_CENTER);
            attendee.setLayoutParams(layoutParams);
            final int position = i;
            attendee.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), ProfileActivity.class);
                    intent.putExtra("username", event.getAttendees().get(position).getUsername());
                    startActivity(intent);
                    //Toast.makeText(getContext(), "View profile with ID " + event.getAttendees().get(position).getUserID(), Toast.LENGTH_SHORT).show();
                }
            });
            attendeeHolder.addView(attendee);
        }
    }

    public interface VolleyCallback{
        void onSuccess(JSONObject result);
    }
    public void interactEvent(final boolean attend){
        String url = Constants.baseUrl;
        if(attend){
            url += "api/Event/Attend/" + eventID + "?username=" + currentUser;
        }
        else{
            url += "api/Event/Leave/" + eventID + "?username=" + currentUser;
        }
        interactEvent(url, new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                if(attend){
                    mBinding.attendButton.setText(getString(R.string.leave));
                    getEventDetails(eventID);
                }
                else{
                    mBinding.attendButton.setText(getString(R.string.rsvp));
                    getEventDetails(eventID);
                }
            }
        });
    }
    public void interactEvent(String url, final VolleyCallback callback) {

        url = url.replace(" ", "%20");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getBoolean("success")){
                        callback.onSuccess(response);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString());
            }
        });
        queue.add(jsonObjectRequest);
    }
    private int convertPixelsToDp(int dp) {
        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return Math.round(px);
    }

}
