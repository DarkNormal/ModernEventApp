package com.lordan.mark.PosseUp.UI;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lordan.mark.PosseUp.R;

/**
 * Created by Mark on 31/01/2016.
 */
public class DrawerItemAdapter extends BaseAdapter {
    private Context context;
    String[] listTitles;
    private TypedArray imgs;
    public DrawerItemAdapter(Context context){
        this.context = context;
        listTitles = context.getResources().getStringArray(R.array.nav_drawer_items);
        imgs = context.getResources().obtainTypedArray(R.array.nav_drawer_icons);
    }
    @Override
    public int getCount() {
        return listTitles.length;
    }

    @Override
    public Object getItem(int position) {
        return listTitles[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.drawer_list_item, parent, false);
        }
        else{
            row = convertView;
        }
        TextView textView = (TextView) row.findViewById(R.id.drawer_item_text);
        ImageView imageView = (ImageView) row.findViewById(R.id.drawer_item_icon);
        textView.setText(listTitles[position]);
        imageView.setImageResource(imgs.getResourceId(position, R.drawable.ic_cancel_light));
        return row;
    }
}
