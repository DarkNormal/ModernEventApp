package com.lordan.mark.PosseUp.UI.ProfileGroup;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.lordan.mark.PosseUp.DataOperations.AzureService;
import com.lordan.mark.PosseUp.R;
import com.lordan.mark.PosseUp.SlidingTabs.ViewPagerAdapter;

/**
 * Created by Mark on 31/01/2016.
 */
public class ProfileFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.profile_layout, container, false);

        AzureService az = new AzureService();

        TextView username = (TextView) rootView.findViewById(R.id.profile_username);
        username.setText(az.getCurrentUsername(getContext()));

        ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.profile_pager);
        viewPager.setAdapter(new ProfilePagerAdapter(getChildFragmentManager(), getContext()));
        TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.profile_tabs);
        tabLayout.setupWithViewPager(viewPager);

        return rootView;
    }
}
