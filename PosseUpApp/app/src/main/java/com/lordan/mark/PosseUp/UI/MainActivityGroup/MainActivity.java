package com.lordan.mark.PosseUp.UI.MainActivityGroup;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.lordan.mark.PosseUp.AbstractActivity;
import com.lordan.mark.PosseUp.Model.MyHandler;
import com.lordan.mark.PosseUp.R;
import com.lordan.mark.PosseUp.UI.ProfileGroup.ProfileFragment;
import com.lordan.mark.PosseUp.UI.SigninGroup.SigninActivity;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.windowsazure.messaging.NotificationHub;
import com.microsoft.windowsazure.notifications.NotificationsManager;


public class MainActivity extends AbstractActivity {


    private String SENDER_ID = "851010273767";
    private GoogleCloudMessaging gcm;
    private NotificationHub hub;
    private static Boolean isVisible = false;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.main_toolbar);


        setSupportActionBar(mToolbar);

        Fragment fragment = new Tab1();
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();

        setupGcm();

        mTitle = mDrawerTitle = getTitle();

        NavigationView navView = (NavigationView) findViewById(R.id.drawer_nav_view);
        View header = navView.getHeaderView(0);
        TextView drawerUsername = (TextView) header.findViewById(R.id.drawer_username);
        drawerUsername.setText(getCurrentUsername());
        TextView drawerEmail = (TextView) header.findViewById(R.id.drawer_email);
        drawerEmail.setText(getCurrentEmail());
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);

                //Closing drawer on item click
                mDrawerLayout.closeDrawers();

                Fragment fragment;
                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.drawer_home:

                        Toast.makeText(getApplicationContext(), "Home Selected", Toast.LENGTH_SHORT).show();
                        fragment = new Tab1();
                        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
                        return true;
                    case R.id.drawer_profile:
                        Toast.makeText(getApplicationContext(), "Profile Selected", Toast.LENGTH_SHORT).show();
                        fragment = new ProfileFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
                        return true;
                    default:
                        Toast.makeText(getApplicationContext(), "Somethings Wrong", Toast.LENGTH_SHORT).show();
                        return true;

                }
            }
        });
        String[] navDrawerTitles = getResources().getStringArray(R.array.nav_drawer_items);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        LinearLayout drawerLinear = (LinearLayout) findViewById(R.id.drawer_linear);
        //mDrawerList = (ListView) findViewById(R.id.left_drawer);
        //mDrawerList.setAdapter(new DrawerItemAdapter(this));
//        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
//
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeButtonEnabled(true);
//
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
        String hubListenConnectionString = "Endpoint=sb://posseup-notificationhub.servicebus.windows.net/;SharedAccessKeyName=DefaultListenSharedAccessSignature;SharedAccessKey=ULoYyvlyIcvWKY1XFmUYB8OGNTOfzIKHT11m1AukTuc=";
        String hubName = "PosseUp-Notifications";
        hub = new NotificationHub(hubName, hubListenConnectionString, this);
        registerWithNotificationHubs();
    }

    @SuppressWarnings("unchecked")
    private void registerWithNotificationHubs() {
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object... params) {
                try {
                    String regid = gcm.register(SENDER_ID);
                    ToastNotify("Registered Successfully - RegID: " + hub.register(regid, getCurrentEmail()).getRegistrationId());
                } catch (Exception e) {
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
        } else if (id == R.id.sign_out_menu) {
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

    public void ToastNotify(final String notificationMessage) {
        if (isVisible == true)
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, notificationMessage, Toast.LENGTH_LONG).show();
                }
            });
    }
}
