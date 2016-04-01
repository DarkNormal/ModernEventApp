package com.lordan.mark.PosseUp.UI.EventDetailGroup;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.squareup.picasso.Picasso;

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
            Picasso.with(getContext()).load(event.getEventImage()).into(mBinding.eventImageHeader);
        }
        else {
            mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_event_details, container, false);
            queue = Volley.newRequestQueue(getContext());
        }
            v = mBinding.getRoot();
            AppCompatImageView directions = mBinding.directionsButton;
            directions.setImageAlpha(128);
            directions.setOnClickListener(this);
        mBinding.eventLocationHolder.setOnClickListener(this);
        mBinding.eventGuestsButton.setOnClickListener(this);
        mBinding.attendButton.setOnClickListener(this);
        mBinding.eventTimeGroup.setOnClickListener(this);
        return v;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
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
            Picasso.with(getContext()).load(event.getEventImage()).into(mBinding.eventImageHeader);
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
            case R.id.directions_button:
                LatLng location = event.getPlaceDetails().getVenueLocation();
                Intent directionIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?daddr=" + location.latitude + "," + location.longitude));
                startActivity(directionIntent);
                break;
            case R.id.event_location_holder:
                location = event.getPlaceDetails().getVenueLocation();
                String label = event.getPlaceDetails().getVenueName();
                String uriBegin = "geo:" + location.latitude + "," + location.longitude;
                String query = location.latitude+"," + location.longitude + "(" + label +")";
                String encodedQuery = Uri.encode(query);
                String uriString = uriBegin + "?q=" + encodedQuery;
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse( uriString + "&z=12"));
                startActivity(mapIntent);
                break;
            case R.id.event_guests_button:
                if (mListener != null) {
                    mListener.onFragmentInteraction(event);
                }
                break;
            case R.id.event_time_group:
                Intent intent = new Intent(Intent.ACTION_EDIT);
                intent.setType("vnd.android.cursor.item/event");
                intent.putExtra(CalendarContract.Events.TITLE, event.getEventName());
                intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                        event.getStartTimeCalendar().getTimeInMillis());
                intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                        event.getEndTimeCalendar().getTimeInMillis());
                if(event.isAllDay()){
                    intent.putExtra(CalendarContract.Events.ALL_DAY, true);// periodicity
                }
                else {
                    intent.putExtra(CalendarContract.Events.ALL_DAY, false);// periodicity
                }
                intent.putExtra(CalendarContract.Events.DESCRIPTION,event.getEventDesc());
                startActivity(intent);
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
                for (User u: event.getAttendees()) {
                    if(u.getUsername().equals(currentUser)){
                        mBinding.attendButton.setText(getString(R.string.leave));
                        break;
                    }
                }
                mBinding.setEvent(event);
                Picasso.with(getContext()).load(event.getEventImage()).into(mBinding.eventImageHeader);
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
            final int position = i;
            Picasso.with(getContext()).load(event.getAttendees().get(position).getProfileImage()).into(attendee);
            attendee.setScaleType(ImageView.ScaleType.FIT_CENTER);
            attendee.setLayoutParams(layoutParams);

            attendee.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), ProfileActivity.class);
                    intent.putExtra("username", event.getAttendees().get(position).getUsername());
                    startActivity(intent);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        //mBinding.eventImageHeader.setImageBitmap(null);
    }

}
