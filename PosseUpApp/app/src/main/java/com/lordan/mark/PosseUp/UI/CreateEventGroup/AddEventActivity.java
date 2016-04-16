package com.lordan.mark.PosseUp.UI.CreateEventGroup;



import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;

import android.support.v4.app.FragmentManager;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;

import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.google.gson.Gson;
import com.lordan.mark.PosseUp.AbstractActivity;

import com.lordan.mark.PosseUp.DataOperations.AzureService;
import com.lordan.mark.PosseUp.Model.Constants;
import com.lordan.mark.PosseUp.Model.Event;
import com.lordan.mark.PosseUp.Model.User;
import com.lordan.mark.PosseUp.R;
import com.lordan.mark.PosseUp.UI.InviteFollowersDialog;
import com.lordan.mark.PosseUp.VolleyCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by Mark on 10/27/2015
 */
public class AddEventActivity extends AbstractActivity implements InviteFollowersDialog.InviteFollowersDialogListener{
    private FirstEventFragment myFrag;
    private RequestQueue queue;
    private String hostEmail;
    private String eventID;
    private Event newEvent;
    private String[] mSelectedFollowers;
    @Bind(R.id.add_event_progress_bar) public ProgressBar progressBar;
    @Bind(R.id.coordinatorLayout) public CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addevent_layout);
        ButterKnife.bind(this);
        setupToolbar();

        hostEmail = new AzureService().getCurrentEmail(this);
        FrameLayout fragmentHolder = (FrameLayout) findViewById(R.id.add_event_fragment_holder);
        FragmentManager fragMan = getSupportFragmentManager();
        FragmentTransaction fragTransaction = fragMan.beginTransaction();
        myFrag = new FirstEventFragment();
        if(fragmentHolder != null) {
            fragTransaction.add(fragmentHolder.getId(), myFrag, "add_event_basic");
            fragTransaction.commit();
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        queue = Volley.newRequestQueue(this);

    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.add_event_toolbar);
        setSupportActionBar(toolbar);
        // Show menu icon
        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setHomeAsUpIndicator(R.drawable.ic_action_cancel);
            ab.setDisplayHomeAsUpEnabled(true);
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_event_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (android.R.id.home):
                android.app.FragmentManager fm = getFragmentManager();
                View view = this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                ConfirmExitDialog confirm = new ConfirmExitDialog();
                confirm.show(fm, "confirm_dialog");
                break;
            case (R.id.create_event_next):
                newEvent = myFrag.getEvent();
                if(newEvent != null){
                    newEvent.setHostEmail(hostEmail);
                    newEvent.setInvitedGuests(mSelectedFollowers);
                    sendEvent(newEvent);
                }
                break;
            case R.id.create_event_invite:
                inviteFriends();
            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFinishInviteDialog(String[] selectedUsers) {
        mSelectedFollowers = selectedUsers;
    }

    public static class ConfirmExitDialog extends DialogFragment{
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomAlertDialogStyle);
            builder.setTitle("Discard Event?")
                    .setMessage("All progress will be lost")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
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
    private void inviteFriends(){
        getUserDetails(new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                String[] followers = null;
                try {
                    followers = new String[result.getJSONArray("Followers").length()];
                    for (int i = 0; i < result.getJSONArray("Followers").length(); i++) {
                        JSONObject jsonObject = result.getJSONArray("Followers").getJSONObject(i);
                        followers[i] = jsonObject.getString("Username");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                InviteFollowersDialog inviteFriendsDialog = new InviteFollowersDialog();
                inviteFriendsDialog.setFollowers(followers);
                inviteFriendsDialog.show(getFragmentManager(), "invite_dialog");
            }

            @Override
            public void onError(VolleyError error) {

            }
        });
    }

    private void sendEvent(Event e){
        progressBar.setVisibility(View.VISIBLE);
        sendEvent(e, new VolleyCallback() {
            @Override
            public void onSuccess(final JSONObject result) {
                String eventID = null;
                try {
                    eventID = result.getString("EventID");
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                progressBar.setVisibility(View.INVISIBLE);
                String url = Constants.baseUrl + "api/Event/Attend/" + eventID + "?username=" + new AzureService().getCurrentUsername(getApplicationContext());
                addHostToEventList(url, new VolleyCallback() {
                    @Override
                    public void onSuccess(JSONObject mResult) {
                        Intent data = new Intent();
                        try {
                            data.putExtra("EventID", result.getInt("EventID"));
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                        setResult(RESULT_OK,data);
                        finish();
                    }

                    @Override
                    public void onError(VolleyError error) {
                        progressBar.setVisibility(View.INVISIBLE);

                    }
                });
            }

            @Override
            public void onError(VolleyError error) {
                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Error creating event", Snackbar.LENGTH_SHORT).setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendEvent(newEvent);
                    }
                }).setActionTextColor(Color.YELLOW);
                View v = snackbar.getView();
                TextView textView = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.WHITE);
                progressBar.setVisibility(View.INVISIBLE);
                snackbar.show();
            }
        });
    }
    private void sendEvent(Event e, final VolleyCallback callback) {
        String url = Constants.baseUrl + "api/Events";
        String event = new Gson().toJson(e);
        JSONObject eventObj = new JSONObject();
        try {
            eventObj = new JSONObject(event);


        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, eventObj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.onSuccess(response);
                Log.i("Create Event", response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Create Event Error", "");
                callback.onError(error);
            }
        });
        queue.add(jsonObjectRequest);
    }
    private void addHostToEventList(String url, final VolleyCallback callback){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getBoolean("success")) {
                        callback.onSuccess(response);
                        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
                        if (coordinatorLayout != null) {
                            Snackbar.make(coordinatorLayout, "Event created", Snackbar.LENGTH_LONG)
                                    .setAction("View", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            finish();
                                        }
                                    })
                                    .setActionTextColor(Color.MAGENTA)
                                    .show();
                        }
                    }
                } catch (JSONException e) {

                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error);
            }
        });
        queue.add(jsonObjectRequest);
    }

    private void getUserDetails(final VolleyCallback callback) {
        String url = Constants.baseUrl + "api/Account/UserInfo/" + new AzureService().getCurrentUsername(this).replace(" ", "%20");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.onSuccess(response);
                Log.i("profilefragment", "got response");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("profilefragment", "got error");

            }
        });
        queue.add(jsonObjectRequest);
    }
}

