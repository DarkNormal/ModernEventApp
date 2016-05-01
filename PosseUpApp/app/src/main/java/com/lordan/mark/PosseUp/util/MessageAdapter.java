package com.lordan.mark.PosseUp.util;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lordan.mark.PosseUp.Model.ChatMessage;
import com.lordan.mark.PosseUp.R;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/*
 * Created by Mark on 04/04/2016.
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>{

    private final ArrayList<ChatMessage> mDataset;
    private final Context mComtext;
    // Provide a reference to the views for each data item
// Complex data items may need more than one view per item, and
// you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public final TextView messageUsername;
        public final TextView messageTimestamp;
        public final TextView messageContent;
        public final CircularImageView chatImage;
        public ViewHolder(View v) {
            super(v);
            messageUsername = (TextView) v.findViewById(R.id.message_username);
            messageContent = (TextView) v.findViewById(R.id.message_content);
            messageTimestamp = (TextView) v.findViewById(R.id.message_timestamp);
            chatImage = (CircularImageView) v.findViewById(R.id.message_profile_picture);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MessageAdapter(Context context, ArrayList<ChatMessage> myDataset) {
        mComtext = context;
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_layout, parent, false);
        return new MessageAdapter.ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.messageUsername.setText(mDataset.get(position).getUsername());
        holder.messageTimestamp.setText(mDataset.get(position).getTimestamp());
        holder.messageContent.setText(mDataset.get(position).getContent());
        Picasso.with(mComtext).load(mDataset.get(position).getUserProfilePicture()).into(holder.chatImage);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
