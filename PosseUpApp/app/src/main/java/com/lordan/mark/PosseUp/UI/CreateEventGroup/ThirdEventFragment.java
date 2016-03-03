package com.lordan.mark.PosseUp.UI.CreateEventGroup;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lordan.mark.PosseUp.R;

/**
 * Created by Mark on 15/01/2016.
 */
public class ThirdEventFragment extends Fragment {
    View v;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        v = inflater.inflate(R.layout.add_event_confirm, container, false);
        getActivity().setTitle("Confirm Event");
        displayCard();
        return v;
    }
    private void displayCard(){
        TextView title = (TextView) v.findViewById(R.id.confirm_event_title);
    }
}
