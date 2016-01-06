package com.lordan.mark.PosseUp.UI;

import android.app.AlertDialog;

import android.content.Intent;
import android.support.v4.view.ViewPager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.lordan.mark.PosseUp.AbstractActivity;
import com.lordan.mark.PosseUp.R;
import com.lordan.mark.PosseUp.UI.SigninGroup.SigninActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;
import com.lordan.mark.PosseUp.SlidingTabs.ViewPagerAdapter;



public class MainActivity extends AbstractActivity{
    CharSequence Titles[]={"Home","Nearby" , "Chat", "Me"};
    int Numboftabs =4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        // Initialize the ViewPager and set an adapter
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(), Titles, Numboftabs));

        // Bind the tabs to the ViewPager
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setViewPager(pager);



        getSupportActionBar().setElevation(0);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if(id == R.id.my_profile){

        }
        else if(id == R.id.sign_out_menu){
            signOut();
            startActivity(new Intent(MainActivity.this, SigninActivity.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
