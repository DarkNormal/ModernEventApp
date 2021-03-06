package com.lordan.mark.PosseUp.UI.MainActivityGroup;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.lordan.mark.PosseUp.AbstractActivity;
import com.lordan.mark.PosseUp.DataOperations.AzureService;
import com.lordan.mark.PosseUp.Model.MyHandler;
import com.lordan.mark.PosseUp.Model.User;
import com.lordan.mark.PosseUp.UI.EventDetailGroup.UserFragment;
import com.lordan.mark.PosseUp.R;
import com.lordan.mark.PosseUp.UI.ProfileGroup.ProfileFragment;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.lordan.mark.PosseUp.UI.SigninGroup.SigninActivity;
import com.microsoft.windowsazure.messaging.NotificationHub;
import com.microsoft.windowsazure.notifications.NotificationsManager;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;


public class MainActivity extends AbstractActivity implements
        ProfileFragment.OnFragmentInteractionListener,
        UserFragment.OnListFragmentInteractionListener,
        ChatFragment.OnChatFragmentInteractionListener{


    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mTitle;
    private GoogleCloudMessaging gcm;
    private String regID = null;
    private NotificationHub hub;
    @Bind(R.id.drawer_nav_view)
    public NavigationView navView;
    @Bind(R.id.drawer_layout)
    public DrawerLayout mDrawerLayout;
    private Fragment content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);

        CharSequence mDrawerTitle;
        mTitle = mDrawerTitle = getTitle();
        try{
            ActionBar mActionBar = getSupportActionBar();
        }
        catch(NullPointerException npe){
            String TAG = "MainActivity";
            Log.e(TAG, "ActionBar null");
        }

        View header = navView.getHeaderView(0);
        ((TextView) header.findViewById(R.id.drawer_username)).setText(getCurrentUsername());
        ((TextView) header.findViewById(R.id.drawer_email)).setText(getCurrentEmail());
        Picasso.with(this).load(new AzureService().getProfileImageURL(this)).into((CircularImageView) header.findViewById(R.id.drawer_profile_picture));
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                selectDrawerItem(menuItem);
                return true;
            }
        });
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                mToolbar,  /* nav drawer image to replace 'Up' caret */
                R.string.opendrawer,  /* "open drawer" description for accessibility */
                R.string.closeddrawer  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        if (savedInstanceState == null) {
            navView.getMenu().performIdentifierAction(R.id.drawer_home, 0);
        }
        setupGcm();
    }

    private void selectDrawerItem(MenuItem menuItem) {
        //Closing drawer on item click
            if(menuItem.isChecked()){
                menuItem.setChecked(false);
            }
            else {
                menuItem.setChecked(true);
            }
        mDrawerLayout.closeDrawers();
        //Check to see which item was being clicked and perform appropriate action
        switch (menuItem.getItemId()) {
            case R.id.drawer_home:
                mTitle = getString(R.string.tab1);
                if(!(content instanceof Tab1)){
                content = new Tab1();
                changeFragments(content, "TAB1");
                }
                break;
            case R.id.drawer_profile:
                mTitle = getString(R.string.drawer_profile);
                if(!(content instanceof ProfileFragment)) {
                    content = ProfileFragment.newInstance(true, getCurrentUsername());
                    changeFragments(content, "PROFILE_TAB");
                }
                break;
            case R.id.drawer_events:
                mTitle = "Events Overview";
                if(!(content instanceof EventBreakdownFragment)) {
                    content = EventBreakdownFragment.newInstance(getCurrentUsername());
                    changeFragments(content, "BREAKDOWN_TAB");
                }
                break;
            case R.id.drawer_chat:
                mTitle = "Chat";
                if(!(content instanceof  ChatFragment)) {
                    content = new ChatFragment();
                    changeFragments(content, "CHAT_TAB");
                }
                break;
        }

    }
    private void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    private void changeFragments(Fragment fragment,String tag){
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment, tag).commit();
        setActionBarTitle(mTitle.toString());
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

        if (id == R.id.sign_out_menu) {
            signOutOfAccount();
            startActivity(new Intent(MainActivity.this, SigninActivity.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onFragmentInteraction(User u, String viewType) {
        UserFragment userFragment = UserFragment.newInstance(u, viewType);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, userFragment).addToBackStack(null);
        fragmentTransaction.commit();

    }

    @Override
    public void onListFragmentInteraction(User u) {

    }

    @Override
    public void onChatFragmentInteraction(Uri uri) {

    }
    private void setupGcm() {

        NotificationsManager.handleNotifications(this, getString(R.string.SENDER_ID), MyHandler.class);
        gcm = GoogleCloudMessaging.getInstance(this);
        hub = new NotificationHub(getString(R.string.hubName), getString(R.string.hubListenConnectionString), this);
        registerWithNotificationHubs();
    }
    @SuppressWarnings("unchecked")
    private void registerWithNotificationHubs() {
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object... params) {
                try {
                    String regid = gcm.register(getString(R.string.SENDER_ID));
                    regID = hub.register(regid, getCurrentEmail()).getRegistrationId();
                    Log.i("MainActivity","Registered Successfully - RegID: " + regID);
                } catch (Exception e) {
                    Log.e("MainActivity", "GCM register exception");
                }
                return null;
            }
        }.execute(null, null, null);
    }

    @SuppressWarnings("unchecked")
    public void signOut(){
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object... params) {
                try {
                    hub.unregister();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute(null, null, null);

        signOutOfAccount();
    }
}
