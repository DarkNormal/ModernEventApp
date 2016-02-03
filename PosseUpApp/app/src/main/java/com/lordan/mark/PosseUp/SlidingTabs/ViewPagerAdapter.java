package com.lordan.mark.PosseUp.SlidingTabs;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.lordan.mark.PosseUp.UI.MainActivityGroup.Tab1;
import com.lordan.mark.PosseUp.UI.MainActivityGroup.Tab2;
import com.lordan.mark.PosseUp.UI.MainActivityGroup.Tab3;
import com.lordan.mark.PosseUp.UI.MainActivityGroup.Tab4;

/**
 * Created by hp1 on 21-01-2015.
 */

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private CharSequence Titles[];      //Stores the titles of all tabs
    private int NumTabs;

    public ViewPagerAdapter(FragmentManager fm,CharSequence mTitles[], int mNumTabs) {
        super(fm);

        this.Titles = mTitles;
        this.NumTabs = mNumTabs;
    }

    @Override
    public Fragment getItem(int position) {

        if(position == 0) // if the position is 0 we are returning the First tab
        {
            Tab1 tab1 = new Tab1();
            return tab1;
        }
        else if(position == 1)
        {
            Tab2 tab2 = new Tab2();
            return tab2;
        }
        else if(position == 2)
        {
            Tab3 tab3 = new Tab3();
            return tab3;
        }
        else
        {
            Tab4 tab4 = new Tab4();
            return tab4;
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