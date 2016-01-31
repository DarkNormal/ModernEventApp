package com.lordan.mark.PosseUp.UI;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.google.android.gms.common.api.BooleanResult;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.common.collect.ObjectArrays;
import com.lordan.mark.PosseUp.AbstractActivity;
import com.lordan.mark.PosseUp.Model.MyHandler;
import com.lordan.mark.PosseUp.UI.MainActivityGroup.MainFragment;
import com.lordan.mark.PosseUp.UI.ProfileGroup.ProfileActivity;
import com.lordan.mark.PosseUp.R;
import com.lordan.mark.PosseUp.UI.ProfileGroup.ProfileFragment;
import com.lordan.mark.PosseUp.UI.SigninGroup.SigninActivity;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;


import com.astuetz.PagerSlidingTabStrip;
import com.lordan.mark.PosseUp.SlidingTabs.ViewPagerAdapter;
import com.microsoft.windowsazure.messaging.NotificationHub;
import com.microsoft.windowsazure.notifications.NotificationsManager;


public class MainActivity extends AbstractActivity{


    private String SENDER_ID = "851010273767";
    private GoogleCloudMessaging gcm;
    private NotificationHub hub;
    private String HubName = "PosseUp-Notifications";
    private String HubListenConnectionString = "Endpoint=sb://posseup-notificationhub.servicebus.windows.net/;SharedAccessKeyName=DefaultListenSharedAccessSignature;SharedAccessKey=ULoYyvlyIcvWKY1XFmUYB8OGNTOfzIKHT11m1AukTuc=";
    private static Boolean isVisible = false;
    private String[] navDrawerTitles;
    private DrawerLayout mDrawerLayout;
    private LinearLayout drawerLinear;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.main_activity_toolbar);
        setSupportActionBar(mToolbar);

        Fragment fragment = new MainFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();

        setupGcm();

        mTitle = mDrawerTitle = getTitle();

        navDrawerTitles = getResources().getStringArray(R.array.nav_drawer_items);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        drawerLinear = (LinearLayout) findViewById(R.id.drawer_linear);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new DrawerItemAdapter(this));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                mToolbar,  /* nav drawer image to replace 'Up' caret */
                R.string.opendrawer,  /* "open drawer" description for accessibility */
                R.string.closeddrawer  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        // Initialize the ViewPager and set an adapter




        getSupportActionBar().setElevation(0);

    }

    private void setupGcm() {
        MyHandler.mainActivity = this;
        NotificationsManager.handleNotifications(this, SENDER_ID, MyHandler.class);
        gcm = GoogleCloudMessaging.getInstance(this);
        hub = new NotificationHub(HubName, HubListenConnectionString, this);
        registerWithNotificationHubs();
    }
    @SuppressWarnings("unchecked")
    private void registerWithNotificationHubs(){
        new AsyncTask(){
            @Override
            protected Object doInBackground(Object... params){
                try{
                    String regid = gcm.register(SENDER_ID);
                    ToastNotify("Registered Successfully - RegID: " + hub.register(regid, getCurrentEmail()).getRegistrationId());
                }
                catch(Exception e){
                    ToastNotify("Registration Exception Message - " + e.getMessage());
                }
                return null;
            }
        }.execute(null, null, null);
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
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if(id == R.id.my_profile){
            startActivity(new Intent(MainActivity.this,ProfileActivity.class));
        }
        else if(id == R.id.sign_out_menu){
            signOut();
            startActivity(new Intent(MainActivity.this, SigninActivity.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onStart() {
        super.onStart();
        isVisible = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isVisible = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isVisible = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isVisible = false;
    }

    public void ToastNotify(final String notificationMessage)
    {
        if (isVisible == true)
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, notificationMessage, Toast.LENGTH_LONG).show();
                }
            });
    }
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    //TODO
    //
    //Make methods for repeating fragment transactions, also check if its already selected
    //Not sure if this already happens but otherwise it's a waste of resources
    //
    /** Swaps fragments in the main content view */
    private void selectItem(int position) {
        // Create a new fragment and specify the planet to show based on position
        Log.i("NAVDRAWER", "item " + position + " selected");
        switch(position){
            case 0:
                Fragment fragment = new MainFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
                break;
            case 1:
                Fragment profileFragment = new ProfileFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, profileFragment).commit();
                break;
            default:
                break;
        }
        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(navDrawerTitles[position]);
        mDrawerLayout.closeDrawer(drawerLinear);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);

    }
}
