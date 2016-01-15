package com.lordan.mark.PosseUp.UI.CreateEventGroup;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;

import android.widget.LinearLayout;

import com.lordan.mark.PosseUp.AbstractActivity;

import com.lordan.mark.PosseUp.R;



/**
 * Created by Mark on 10/27/2015.
 */
public class AddEventActivity extends AbstractActivity {
    private ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addevent_layout);

        setupToolbar();

        pager = (ViewPager) findViewById(R.id.add_event_pager);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));

    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.add_event_toolbar);
        setSupportActionBar(toolbar);
        // Show menu icon
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_cancel_light);
        ab.setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_event_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (android.R.id.home):
                finish();
                break;
            case (R.id.create_event_next):
                nextInPager();
                break;
            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }
    private void nextInPager(){
        if(pager.getCurrentItem() == 0){
            pager.setCurrentItem(1);
        }
        else finish();
    }


    private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            switch (pos) {

                case 0:
                    return FirstEventFragment.newInstance("FirstFragment, Instance 1");
                case 1:
                    return SecondEventFragment.newInstance("SecondFragment, Instance 1");
                default:
                    return FirstEventFragment.newInstance("ThirdFragment, Default");
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}

