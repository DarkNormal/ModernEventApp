package com.lordan.mark.PosseUp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lordan.mark.PosseUp.Model.Event;
import com.lordan.mark.PosseUp.UI.MainActivityGroup.CustomItemClickListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Mark on 04/04/2016.
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder>{
    private ArrayList<Event> mDataset;
    private Context mComtext;
    private CustomItemClickListener listener;
    // Provide a reference to the views for each data item
// Complex data items may need more than one view per item, and
// you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;
        public CircularImageView chatImage;
        public TextView lastMessage;
        public TextView lastMessageTimeStamp;
        public ViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.recycler_chat_title);
            chatImage = (CircularImageView) v.findViewById(R.id.chat_picture);
            lastMessage = (TextView) v.findViewById(R.id.recycler_event_snippet);
            lastMessageTimeStamp = (TextView) v.findViewById(R.id.chat_timestamp);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ChatAdapter(Context context, ArrayList<Event> myDataset, CustomItemClickListener customItemClickListener) {
        this.listener = customItemClickListener;
        mComtext = context;
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_group_layout, parent, false);
        final ChatAdapter.ViewHolder mViewHolder = new ChatAdapter.ViewHolder(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, mViewHolder.getAdapterPosition());
            }
        });
        return mViewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mTextView.setText(mDataset.get(position).getEventName());
        Picasso.with(mComtext).load(mDataset.get(position).getEventImage()).into(holder.chatImage);
        if(mDataset.get(position).getLastChatMessage() != null){
            holder.lastMessage.setText(mDataset.get(position).getLastChatMessage());
            holder.lastMessageTimeStamp.setText(mDataset.get(position).getLastMessageTimeStamp());
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
