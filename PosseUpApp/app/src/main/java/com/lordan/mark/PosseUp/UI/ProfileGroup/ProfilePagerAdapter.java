package com.lordan.mark.PosseUp.UI.ProfileGroup;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;

/**
 * Created by Mark on 25/01/2016.
 */
public class ProfilePagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 3;
    CharSequence titles[]={"Friends","History" , "Info"};
    private Context context;

    public ProfilePagerAdapter(FragmentManager fm, Context context){
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        return null;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }
}
