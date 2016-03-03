package com.lordan.mark.PosseUp.UI.ProfileGroup;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Mark on 25/01/2016.
 */
public class ProfilePagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 3;
    CharSequence titles[]={"History","Friends" , "Info"};
    private Context context;

    public ProfilePagerAdapter(FragmentManager fm, Context context){
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment mFragment = null;
        switch(position){
            case 0:
                mFragment = new ProfileInfoFragment();
                break;
            case 1:
                mFragment = new ProfileFriendList();
                break;
            case 2:
                mFragment = new ProfileInfoFragment();
                break;
            default:
                break;
        }
        return mFragment;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }
    @Override
    public CharSequence getPageTitle(int position){
        return titles[position];
    }
}
