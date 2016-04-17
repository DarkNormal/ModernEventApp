package com.lordan.mark.PosseUp.util;

/**
 * Created by Mark on 8/31/2015
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lordan.mark.PosseUp.Model.Event;
import com.lordan.mark.PosseUp.R;
import com.lordan.mark.PosseUp.UI.MainActivityGroup.CustomItemClickListener;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Provide views to RecyclerView with data from mDataSet.
 */
public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
    private static final String TAG = "CustomAdapter";
    private final CustomItemClickListener listener;
    private final List<Event> mDataSet;
    private final Context mContext;

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mTextView;
        public final TextView eventSnippet;
        public final ImageView image;
        final TextView eventTime;
        public ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            mTextView = (TextView) v.findViewById(R.id.recycler_event_title);
            eventSnippet = (TextView) v.findViewById(R.id.recycler_event_snippet);
            eventTime = (TextView) v.findViewById(R.id.recycler_event_time);
            image = (ImageView) v.findViewById(R.id.event_picture);
        }
    }

    /**
     * Initialize the data set of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
     */
    public CustomAdapter(Context mContext,List<Event> dataSet, CustomItemClickListener listener) {
        this.listener = listener;
        this.mContext = mContext;
        mDataSet = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.event_row_item, viewGroup, false);
        final ViewHolder mViewHolder = new ViewHolder(v);
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
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Log.d(TAG, "Element " + position + " set.");
        Picasso.with(mContext).load(mDataSet.get(position).getEventImageURL()).into(viewHolder.image);
        viewHolder.mTextView.setText(mDataSet.get(position).getEventName());
        viewHolder.eventSnippet.setText(mDataSet.get(position).getEventDesc());
        if(mDataSet.get(position).isAllDay()){
            viewHolder.eventTime.setText(mDataSet.get(position).getAllDayStartingCal());
        }
        else{
            viewHolder.eventTime.setText(mDataSet.get(position).getStartingCal());
        }

        // Get element from your data set at this position and replace the contents of the view
        // with that element
    }

    // Return the size of your data set (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}