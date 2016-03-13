package com.lordan.mark.PosseUp.UI.ProfileGroup;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.google.gson.Gson;
import com.lordan.mark.PosseUp.DataOperations.AzureService;

import com.lordan.mark.PosseUp.Model.Constants;
import com.lordan.mark.PosseUp.Model.Friendship;
import com.lordan.mark.PosseUp.Model.User;
import com.lordan.mark.PosseUp.R;

import com.lordan.mark.PosseUp.UI.EventDetailGroup.UserFragment;
import com.lordan.mark.PosseUp.databinding.AccountProfileLayoutBinding;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Mark on 31/01/2016.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener{

    private boolean whois;
    private User user;
    private String currentUsername;
    private RequestQueue queue;
    private OnFragmentInteractionListener mListener;
    private AzureService az;
    private AccountProfileLayoutBinding mBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.account_profile_layout, container, false);
        queue = Volley.newRequestQueue(getContext());
        az = new AzureService();
        currentUsername = az.getCurrentUsername(getContext());
        user = new User();
        mBinding.setUser(user);
        View rootView = mBinding.getRoot();
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            //whois is true when the the user is viewing their own profile, allow editing or something
            whois = bundle.getBoolean("isCurrentUser", false);
            user.setUsername(bundle.getString("username"));

        }
        if(currentUsername.equals(user.getUsername())){
            whois = true;
            mBinding.followButton.setVisibility(View.INVISIBLE);
        }
        getUserDetails(new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {

                try {
                    ArrayList<User> userlists = new ArrayList<>();
                    for (int i = 0; i < result.getJSONArray("Followers").length(); i++) {
                        JSONObject jsonObject = result.getJSONArray("Followers").getJSONObject(i);
                        userlists.add(new Gson().fromJson(jsonObject.toString(), User.class));
                    }
                    user.setFollowers(userlists);
                    userlists = new ArrayList<>();
                    for (int i = 0; i < result.getJSONArray("Following").length(); i++) {
                        JSONObject jsonObject = result.getJSONArray("Following").getJSONObject(i);
                        userlists.add(new Gson().fromJson(jsonObject.toString(), User.class));
                    }
                    user.setFollowing(userlists);
                    Log.i("profilefragment", "followers updated");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(!whois){
                    boolean friend = false;
                    for (User u: user.getFollowers()) {
                        if(u.getUsername().equals(currentUsername)){
                            mBinding.followButton.setText("unfollow");
                            friend = true;
                            break;
                        }
                    }
                    if(!friend) mBinding.followButton.setText("follow");
                }
            }
        });
        mBinding.followButton.setOnClickListener(this);
        mBinding.userFollowers.setOnClickListener(this);
        mBinding.userFollowing.setOnClickListener(this);
        mBinding.userEvents.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public void getUserDetails(final VolleyCallback callback) {
        String url = Constants.baseUrl + "api/Account/UserInfo/" + user.getUsername();
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
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.user_followers:
                mListener.onFragmentInteraction(user, "followers");
                break;
            case R.id.user_events:
                break;
            case R.id.user_following:
                mListener.onFragmentInteraction(user, "following");
                break;
            case R.id.follow_button:
                follow(mBinding.followButton.getText().toString());
                break;

        }
    }

    private void follow(String text) {
        final boolean currentlyFollowing = text.equals("follow");
        followFunction(currentlyFollowing,new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    if(result.getString("success").equals("true")){
                        if(currentlyFollowing) {
                            mBinding.followButton.setText("unfollow");
                            user.updateFollowers(false,new User(currentUsername));
                        }
                        else {
                            mBinding.followButton.setText("follow");
                            for (User u: user.getFollowers()) {
                                if(u.getUsername().equals(currentUsername)){
                                    user.updateFollowers(true,u);
                                    break;
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public interface VolleyCallback{
        void onSuccess(JSONObject result);
    }
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(User u, String viewType );
    }
    public void followFunction(boolean currentFollowing, final VolleyCallback callback) {
        String url = Constants.baseUrl + "api/Account/";
        if(currentFollowing) {
            url += "Follow";
        }
        else{
            url += "Unfollow";
        }
        Friendship following = new Friendship(currentUsername, user.getUsername(), false);
        Gson gson = new Gson();
        String event = gson.toJson(following);
        JSONObject obj = new JSONObject();
        try {
            obj = new JSONObject(event);

        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, obj , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.onSuccess(response);
                Log.i("profilefragment", "friend");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("profilefragment", "friend error");

            }
        });
        queue.add(jsonObjectRequest);
    }


}
