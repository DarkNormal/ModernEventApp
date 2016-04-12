package com.lordan.mark.PosseUp.util;

import android.content.Context;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lordan.mark.PosseUp.R;
import com.lordan.mark.PosseUp.UI.MainActivityGroup.CustomItemClickListener;

import java.util.ArrayList;

/**
 * Created by Mark on 03/04/2016.
 */
public class BasicAdapter extends RecyclerView.Adapter<BasicAdapter.ViewHolder> {
private ArrayList<String> mDataset;
    private CustomItemClickListener listener;
    private Context context;

// Provide a reference to the views for each data item
// Complex data items may need more than one view per item, and
// you provide access to all the views for a data item in a view holder
public static class ViewHolder extends RecyclerView.ViewHolder {
    // each data item is just a string in this case
    public TextView mTextView;
    public AppCompatCheckBox checkBox;

    public ViewHolder(View v) {
        super(v);
        mTextView = (TextView) v.findViewById(R.id.simpleTextViewItem);
        checkBox = (AppCompatCheckBox) v.findViewById(R.id.attendee_present_checkbox);
    }
}

    // Provide a suitable constructor (depends on the kind of dataset)
    public BasicAdapter(Context context, ArrayList<String> myDataset, CustomItemClickListener listener) {
        mDataset = myDataset;
        this.listener = listener;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public BasicAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.simple_text_item, parent, false);
        final BasicAdapter.ViewHolder mViewHolder = new BasicAdapter.ViewHolder(v);
        mViewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
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
        holder.mTextView.setText(mDataset.get(position));
        holder.checkBox.setChecked(true);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}