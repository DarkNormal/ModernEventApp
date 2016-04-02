package com.lordan.mark.PosseUp.SlidingTabs;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.lordan.mark.PosseUp.UI.MainActivityGroup.Tab1;
import com.lordan.mark.PosseUp.UI.MainActivityGroup.Tab2;
import com.lordan.mark.PosseUp.UI.MainActivityGroup.EventBreakdownFragment;
import com.lordan.mark.PosseUp.UI.MainActivityGroup.Tab4;

/**
 * Created by hp1 on 21-01-2015
 */

class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private final CharSequence[] Titles;      //Stores the titles of all tabs
    private final int NumTabs;

    public ViewPagerAdapter(FragmentManager fm,CharSequence mTitles[], int mNumTabs) {
        super(fm);

        this.Titles = mTitles;
        this.NumTabs = mNumTabs;
    }

    @Override
    public Fragment getItem(int position) {

        if(position == 0) // if the position is 0 we are returning the First tab
        {
            return new Tab1();
        }
        else if(position == 1)
        {
            return new Tab2();
        }
        else if(position == 2)
        {
            return new EventBreakdownFragment();
        }
        else
        {
            return new Tab4();
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {        // This method return the titles for the Tabs in the Tab Strip
        return Titles[position];
    }

    @Override
    public int getCount() {  // This method return the Number of tabs for the tabs Strip
        return NumTabs;
    }
}