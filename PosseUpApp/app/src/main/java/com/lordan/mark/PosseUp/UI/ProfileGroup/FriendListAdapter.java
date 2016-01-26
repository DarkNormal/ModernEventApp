package com.lordan.mark.PosseUp.UI.ProfileGroup;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lordan.mark.PosseUp.R;

/**
 * Created by Mark on 26/01/2016.
 */
public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.ViewHolder>{
    private String[] mDataset;


    public  static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView mView;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = (TextView) itemView.findViewById(R.id.friend_list_id);
        }
    }

    public FriendListAdapter(String[] mDataset){
        this.mDataset = mDataset;
    }

    @Override
    public FriendListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_friend, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mView.setText("Friend");
    }

    @Override
    public int getItemCount() {
        return mDataset.length;
    }
}
