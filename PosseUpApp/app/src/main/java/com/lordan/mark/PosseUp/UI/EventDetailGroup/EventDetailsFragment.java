package com.lordan.mark.PosseUp.UI.EventDetailGroup;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.lordan.mark.PosseUp.Model.User;
import com.lordan.mark.PosseUp.R;
import com.lordan.mark.PosseUp.UI.MainActivityGroup.ChatActivity;
import com.lordan.mark.PosseUp.UI.ProfileGroup.ProfileActivity;
import com.lordan.mark.PosseUp.VolleyCallback;
import com.lordan.mark.PosseUp.databinding.FragmentEventDetailsBinding;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class EventDetailsFragment extends Fragment implements
        View.OnClickListener{

    private final String TAG = "EventDetailsFragment";


    private Event event;
    private int eventID;
    private View v;
    private String currentUser;
    private boolean isUserHost;
    private RequestQueue queue;
    private FragmentEventDetailsBinding mBinding;
    private OnFragmentInteractionListener mListener;
    private final int DELETE_EVENT = 5;
    private ConfirmExitDialog confirm;
    @Bind(R.id.event_guests_heading)
    public TextView numGuestsHeading;
    @Bind(R.id.event_invited_guests_heading)
    public TextView numInvitedGuestsHeading;

    public EventDetailsFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_event_details, container, false);
        v = mBinding.getRoot();
        ButterKnife.bind(this,v);
        queue = Volley.newRequestQueue(getContext());
        Bundle bundle = this.getArguments();
        eventID = bundle.getInt("EventID");
        currentUser = bundle.getString("currentUsername");
        isUserHost = bundle.getBoolean("CurrentUserIsHost");
        if (savedInstanceState != null) {
            //probably orientation change
            event = savedInstanceState.getParcelable("event");
            mBinding.setEvent(event);
            mBinding.setVenue(event.getPlaceDetails());
            Picasso.with(getContext()).load(event.getEventImage()).into(mBinding.eventImageHeader);
            displayEventUsers();
        } else {
            if (event == null) {
                Log.i(TAG, "null savedInstanceState");
                if (eventID != -1) {
                    getEventDetails(eventID);
                }
            }
        }
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.menu_event_details, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Intent intent;
        switch(item.getItemId()){
            case R.id.delete_event:
                showDeleteDialog();
                return true;
            case R.id.edit_event:
                Toast.makeText(getContext(), "Edit event selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.event_details_chat:
                intent = ChatActivity.newIntent(getContext(), event.getEventName(), eventID);
                startActivity(intent);
                return true;
            case R.id.invite_to_event:
                mListener.onInviteFollowers(eventID);
                return true;
            case R.id.take_attendance:
                if(isUserHost) {
                    intent = AttendanceActivity.newIntent(getContext(), eventID,isUserHost, event.getAttendees());
                    startActivity(intent);
                }
                else{
                    intent = new Intent(getContext(), AttendActivity.class);
                    intent.putExtra("currentUsername", currentUser);
                    startActivity(intent);

                }
            default:
                return false;

        }
    }
    @Override
    public void onPrepareOptionsMenu (Menu menu){
        if(!isUserHost){
            menu.removeItem(R.id.edit_event);
            menu.removeItem(R.id.delete_event);
            menu.findItem(R.id.take_attendance).setTitle("Attend");
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case DELETE_EVENT:
                deleteEvent(new VolleyCallback() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        getActivity().finish();
                    }

                    @Override
                    public void onError(VolleyError error) {
                        Toast.makeText(getContext(), "Error deleting event", Toast.LENGTH_SHORT).show();
                    }
                });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.attend_button:
                String btnText = mBinding.attendButton.getText().toString().toUpperCase();
                if (btnText.equals("RSVP")) {
                    interactEvent(true);
                } else {
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
                String query = location.latitude + "," + location.longitude + "(" + label + ")";
                String encodedQuery = Uri.encode(query);
                String uriString = uriBegin + "?q=" + encodedQuery;
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uriString + "&z=12"));
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
                if (event.isAllDay()) {
                    intent.putExtra(CalendarContract.Events.ALL_DAY, true);// periodicity
                } else {
                    intent.putExtra(CalendarContract.Events.ALL_DAY, false);// periodicity
                }
                intent.putExtra(CalendarContract.Events.DESCRIPTION, event.getEventDesc());
                startActivity(intent);
                break;
            default:
                break;
        }
    }
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Event e);
        void onInviteFollowers(int eventID);
    }
    private void getEventDetails(int id){
        getEventDetailsCall(id, new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                FetchEventDetailsTask fetchEventDetailsTask = new FetchEventDetailsTask();
                fetchEventDetailsTask.execute(result);
            }

            @Override
            public void onError(VolleyError error) {
            }
        });
    }
    private void getEventDetailsCall(int id, final VolleyCallback callback) {
        String url = Constants.baseUrl + "api/Events/" + id;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error);
            }
        });
        queue.add(jsonObjectRequest);
    }

    private void displayEventUsers() {

        int numberOfGuests = event.getAttendees().size();
        int numberOfInvitedGuests = event.getInvitedGuests().size();
        numInvitedGuestsHeading.setText(getString(R.string.invited_guests_heading, numberOfInvitedGuests));
        numGuestsHeading.setText(getString(R.string.guests_heading, numberOfGuests));
        if (numberOfGuests > 4) {
            displayUsers(4, event.getAttendees(), true);
            TextView extraGuests = new TextView(getContext());
            extraGuests.setText(getString(R.string.extra_guests, numberOfGuests - 4));
        } else {
            displayUsers(numberOfGuests, event.getAttendees(), true);
        }
        if (numberOfInvitedGuests > 4) {
            displayUsers(4, event.getInvitedGuests(), false);
            TextView extraInvitedGuests = new TextView(getContext());
            extraInvitedGuests.setText(getString(R.string.extra_guests, numberOfGuests - 4));
        } else {
            displayUsers(numberOfInvitedGuests, event.getInvitedGuests(), false);
        }


    }

    private void displayUsers(final int loopVar, final ArrayList<User> mDataset, boolean isGuests) {
        LinearLayout attendeeHolder;
        if(isGuests) {
            attendeeHolder = (LinearLayout) v.findViewById(R.id.event_details_pictures);
        }
        else{
            attendeeHolder  = (LinearLayout) v.findViewById(R.id.event_invited_guests_pictures);
        }
        attendeeHolder.removeAllViews();
        int pixelsDP = Math.round(convertPixelsToDp(55));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(pixelsDP, pixelsDP);
        for (int i = 0; i < loopVar; i++) {
            CircularImageView attendee = new CircularImageView(getContext());
            final int position = i;
            Picasso.with(getContext()).load(mDataset.get(position).getProfileImage()).into(attendee);
            attendee.setScaleType(ImageView.ScaleType.FIT_CENTER);
            attendee.setLayoutParams(layoutParams);

            attendee.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), ProfileActivity.class);
                    intent.putExtra("username", mDataset.get(position).getUsername());
                    startActivity(intent);
                }
            });
            attendeeHolder.addView(attendee);
        }
    }

    private void showDeleteDialog(){
        confirm = new ConfirmExitDialog();
        confirm.setTargetFragment(this, DELETE_EVENT);
        confirm.show(getFragmentManager(), "confirm_dialog");
    }
    public static class ConfirmExitDialog extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomAlertDialogStyle);
            builder.setTitle("Delete Event?")
                    .setMessage("Do you want to delete this event?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, getActivity().getIntent());
                            getActivity().finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }

    private void interactEvent(final boolean attend) {
        String url = Constants.baseUrl;
        if (attend) {
            url += "api/Event/Attend/" + eventID + "?username=" + currentUser;
        } else {
            url += "api/Event/Leave/" + eventID + "?username=" + currentUser;
        }
        interactEvent(url, new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                if (attend) {
                    mBinding.attendButton.setText(getString(R.string.leave));
                    getEventDetails(eventID);
                } else {
                    mBinding.attendButton.setText(getString(R.string.rsvp));
                    getEventDetails(eventID);
                }
            }

            @Override
            public void onError(VolleyError error) {

            }
        });
    }
    private void interactEvent(String url, final VolleyCallback callback) {

        url = url.replace(" ", "%20");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getBoolean("success")) {
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

    private void deleteEvent(final VolleyCallback callback){
        String url = Constants.baseUrl + "api/Events/" + eventID;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error);
            }
        });
        queue.add(jsonObjectRequest);
    }
    private class FetchEventDetailsTask extends AsyncTask<JSONObject, Void, Boolean> {

        @Override
        protected Boolean doInBackground(JSONObject... jsonObjects) {
            boolean isUserAttending = false;
            final JSONObject response = jsonObjects[0];
            Gson gson = new Gson();
            event = new Event();
            event = gson.fromJson(response.toString(), Event.class);
            event.setEndingTime();
            event.setStartingTime();
            mBinding.setEvent(event);
            mBinding.setVenue(event.getPlaceDetails());
            for (User u : event.getAttendees()) {
                if (u.getUsername().equals(currentUser)) {
                    isUserAttending = true;
                    break;
                }
            }
            return isUserAttending;
        }

        @Override
        protected void onPostExecute(Boolean isUserAttending) {
            super.onPostExecute(isUserAttending);
            Picasso.with(getContext()).load(event.getEventImage()).into(mBinding.eventImageHeader);
            if(isUserAttending){
                mBinding.attendButton.setText(getString(R.string.leave));
            }
            displayEventUsers();

        }
    }
}
