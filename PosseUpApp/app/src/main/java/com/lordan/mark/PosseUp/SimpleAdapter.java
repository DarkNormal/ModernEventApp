package com.lordan.mark.PosseUp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.lordan.mark.PosseUp.Model.Event;
import com.lordan.mark.PosseUp.UI.MainActivityGroup.CustomItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 ** Created by Mark on 23/03/2016.
 */
public class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.SimpleViewHolder> {

    private final Context mContext;
    private List<Event> mData;
    private CustomItemClickListener listener;

    public void add(Event e,int position) {
        position = position == -1 ? getItemCount()  : position;
        mData.add(position,e);
        notifyItemInserted(position);
    }

    public void remove(int position){
        if (position < getItemCount()  ) {
            mData.remove(position);
            notifyItemRemoved(position);
        }
    }

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        public final TextView title;

        public SimpleViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.recycler_event_title);
        }
    }

    public SimpleAdapter(Context context, List<Event> data, CustomItemClickListener listener) {
        mContext = context;
        this.listener = listener;
        if (data != null)
            mData = new ArrayList<>(data);
        else mData = new ArrayList<>();
    }

    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.text_row_item, parent, false);
        final SimpleViewHolder mViewHolder = new SimpleViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, mViewHolder.getAdapterPosition());
            }
        });
        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, final int position) {
        holder.title.setText(mData.get(holder.getAdapterPosition()).getEventName());
        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext,"Position ="+holder.getAdapterPosition(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}