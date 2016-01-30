package com.lordan.mark.PosseUp.UI.ProfileGroup;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lordan.mark.PosseUp.Model.User;
import com.lordan.mark.PosseUp.R;

import org.json.JSONArray;
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


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mView;
        public LinearLayout addButton;


        public ViewHolder(View itemView) {
            super(itemView);
            mView = (TextView) itemView.findViewById(R.id.friend_list_id);
            addButton = (LinearLayout) itemView.findViewById(R.id.add_user_button);
        }
    }

    public FriendListAdapter(ArrayList<User> mDataset, CoordinatorLayout coordinatorLayout, Context context
    ) {
        this.mDataset = mDataset;
        this.coordinatorLayout = coordinatorLayout;
        this.context = context;
    }

    @Override
    public FriendListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_friend, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
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
                    Snackbar.make(coordinatorLayout, "You unfollowed " + mDataset.get(position).getUsername()
                            , Snackbar.LENGTH_SHORT).show();
                    mDataset.get(position).setFriend(false);
                } else {
                    //we are now adding the user as a friend
                    updateButton(icon, R.drawable.ic_people, holder, true);
                    Snackbar.make(coordinatorLayout, "You are now following " + mDataset.get(position).getUsername()
                            , Snackbar.LENGTH_SHORT).setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            updateButton(icon, R.drawable.tinted_ic_add, holder, false);
                            mDataset.get(position).setFriend(false);
                        }
                    }).setActionTextColor(Color.MAGENTA).show();
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

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
