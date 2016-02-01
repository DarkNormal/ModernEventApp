package com.lordan.mark.PosseUp.UI.ProfileGroup;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_profile, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.edit_profile:
                if(!item.isChecked()){
                    item.setIcon(R.drawable.ic_mode_edit);
                    item.setChecked(true);
                    //TODO save changes
                }
                else{
                    item.setIcon(R.drawable.ic_done_white);
                    item.setChecked(false);
                }
                Toast.makeText(getContext(), "Edit profile", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return false;

        }
    }

}
