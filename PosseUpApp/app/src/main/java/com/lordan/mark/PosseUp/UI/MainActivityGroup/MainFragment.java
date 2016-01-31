package com.lordan.mark.PosseUp.UI.MainActivityGroup;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.astuetz.PagerSlidingTabStrip;
import com.lordan.mark.PosseUp.R;
import com.lordan.mark.PosseUp.SlidingTabs.ViewPagerAdapter;

import java.util.Locale;

/**
 * Created by Mark on 31/01/2016.
 */
public class MainFragment extends Fragment {
    CharSequence Titles[]={"Home","Nearby" , "Chat", "Me"};
    int Numboftabs =4;

    public MainFragment(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_activity_fragment, container, false);

        ViewPager pager = (ViewPager) rootView.findViewById(R.id.pager);
        pager.setAdapter(new ViewPagerAdapter(getChildFragmentManager(), Titles, Numboftabs));

        // Bind the tabs to the ViewPager
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) rootView.findViewById(R.id.tabs);
        tabs.setViewPager(pager);

        return rootView;
    }
}
