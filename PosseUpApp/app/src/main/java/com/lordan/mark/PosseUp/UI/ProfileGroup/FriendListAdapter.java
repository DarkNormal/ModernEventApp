package com.lordan.mark.PosseUp.UI.ProfileGroup;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.google.gson.Gson;
import com.lordan.mark.PosseUp.DataOperations.AzureService;
import com.lordan.mark.PosseUp.Model.Constants;
import com.lordan.mark.PosseUp.Model.Friendship;
import com.lordan.mark.PosseUp.Model.User;
import com.lordan.mark.PosseUp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Mark on 26/01/2016.
 */
public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.ViewHolder> {
    private ArrayList<User> mDataset;
    private CoordinatorLayout coordinatorLayout;
    private Context context;
    private RequestQueue queue;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mView;
        public LinearLayout addButton;


        public ViewHolder(View itemView) {
            super(itemView);
            mView = (TextView) itemView.findViewById(R.id.user_name);
            addButton = (LinearLayout) itemView.findViewById(R.id.add_user_button);
        }
    }

    public FriendListAdapter(ArrayList<User> mDataset, CoordinatorLayout coordinatorLayout, Context context
    ) {
        this.mDataset = mDataset;
        this.coordinatorLayout = coordinatorLayout;
        this.context = context;
        queue = Volley.newRequestQueue(context);
    }

    @Override
    public FriendListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_friend, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        //sets text to username
        holder.mView.setText(mDataset.get(position).getUsername());

        //icon within LinearLayout (button)
        final ImageView icon = (ImageView) holder.addButton.findViewById(R.id.add_user_button_icon);
        if (mDataset.get(position).isFriend()) {
            //if the user is a friend, set the button to added
            holder.addButton.setBackgroundResource(R.drawable.custom_button_background_selected);
            icon.setImageResource(R.drawable.ic_people);
        }
        holder.addButton.setOnClickListener(new View.OnClickListener() {
            //
            //TODO
            //Add calls to web service to complete functionality
            //
            @Override
            public void onClick(View v) {
                if (mDataset.get(position).isFriend()) {
                    //if the user is a friend, we are now unfriending them
                    updateButton(icon, R.drawable.tinted_ic_add, holder, false);
                    updateFriendship(true, mDataset.get(position).getUsername(), position);

                    mDataset.get(position).setFriend(false);
                } else {
                    //we are now adding the user as a friend
                    updateButton(icon, R.drawable.ic_people, holder, true);
                    updateFriendship(false, mDataset.get(position).getUsername(), position);

                    mDataset.get(position).setFriend(true);
                }


            }
        });


    }
    //switches the ImageView icon and the LinearLayout background based on selection
    private void updateButton(ImageView icon, int id, ViewHolder holder, boolean addedOrRemoved) {
        icon.setImageResource(id);
        if (addedOrRemoved) {
            //user added, switch the drawable to added
            holder.addButton.setBackgroundResource(R.drawable.custom_button_background_selected);

        } else {
            //user removed, switch drawable to not added
            holder.addButton.setBackgroundResource(R.drawable.custom_button_background);
        }

    }
    //updates the friendship/follow status via the web service, persisting the data
    private void updateFriendship(boolean remove, String usernameOfFriend, final int position){
        String url = Constants.baseUrl;
        AzureService az = new AzureService();
        if(remove){
            url += "api/FriendRelationships?username=" + az.getCurrentUsername(context) + "&usernameOfFriend=" +usernameOfFriend;
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.i("DELETE_FRIEND", response.toString());
                    showSnackBar(true, mDataset.get(position).getUsername());
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("DELETE_FRIEND", error.networkResponse.toString());

                }
            });
            queue.add(request);
        }
        else{
            url += "api/FriendRequests/AddFriend";
            Friendship newFriendship = new Friendship(az.getCurrentUsername(context), usernameOfFriend, false);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, convertToJSON(newFriendship), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.i("ADD_FRIEND", "successfully added friend");
                    showSnackBar(false, mDataset.get(position).getUsername());
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("ADD_FRIEND", error.toString());

                }
            });
            queue.add(request);
        }

    }
    private JSONObject convertToJSON(Friendship obj){
        Gson gson = new Gson();
        String jsonString = gson.toJson(obj);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    //displays a snackbar to the user detailing the action just made
    private void showSnackBar(boolean deleted, String username){
        String message;
        if(deleted){
            message = "You unfollowed ";
        }
        else{
            message = "You started following ";
        }
        Snackbar.make(coordinatorLayout, message + username, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
