package com.lordan.mark.PosseUp.UI.EventDetailGroup;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
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
import android.widget.Toast;

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
import com.lordan.mark.PosseUp.R;
import com.lordan.mark.PosseUp.databinding.FragmentEventDetailsBinding;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EventDetailsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EventDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventDetailsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String TAG = "Event Details Fragment";
    private String mParam1;
    private String mParam2;

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

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EventDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EventDetailsFragment newInstance(String param1, String param2) {
        EventDetailsFragment fragment = new EventDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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
            if (event != null) {
                //returning from backstack, data is fine, do nothing
            } else {
                //newly created, compute data
                Log.i(TAG, "null savedinstancestate");
                if (eventID != -1) {
                    getEventDetails(eventID);
                }
            }
        }
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Event e);
    }

    public void getEventDetails(int id) {
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
        numGuestsHeading.setText("guests (" + numberOfGuests +")");
        if(numberOfGuests > 4){
            displayAttendee(4);
            TextView extraGuests = new TextView(getContext());
            extraGuests.setText("+" + (numberOfGuests - 4) + " guests" );
        }
        else{
            displayAttendee(numberOfGuests);
        }


    }
    private void displayAttendee(final int loopVar){
        LinearLayout attendeeHolder = (LinearLayout) v.findViewById(R.id.event_details_pictures);
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
                    Toast.makeText(getContext(), "View profile with ID " + event.getAttendees().get(position).getUserID(), Toast.LENGTH_SHORT).show();
                }
            });
            attendeeHolder.addView(attendee);
        }
    }
    public void attendEvent(View v) {

        String url = Constants.baseUrl + "/AttendEvent?id=" + eventID + "&username=" + currentUser;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //Log.i(TAG, response.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString());
            }
        });
        queue.add(jsonObjectRequest);
    }
    public int convertPixelsToDp(int dp) {
        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return Math.round(px);
    }

}