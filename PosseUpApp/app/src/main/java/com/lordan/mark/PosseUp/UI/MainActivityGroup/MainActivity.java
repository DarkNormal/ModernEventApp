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
import android.widget.Toast;

import com.lordan.mark.PosseUp.UI.SettingsActivity;
import com.lordan.mark.PosseUp.UI.SigninGroup.SigninActivity;
import com.microsoft.windowsazure.messaging.NotificationHub;
import com.microsoft.windowsazure.notifications.NotificationsManager;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;


public class MainActivity extends AbstractActivity implements ProfileFragment.OnFragmentInteractionListener, UserFragment.OnListFragmentInteractionListener, ChatFragment.OnChatFragmentInteractionListener{


    private final String TAG = "MainActivity";
    private final String SENDER_ID = "851010273767";
    private GoogleCloudMessaging gcm;
    private NotificationHub hub;
    private static Boolean isVisible = false;
    private MenuItem mPreviousMenuItem;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private ActionBar mActionBar;
    private AzureService az;

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
        try{
            mActionBar = getSupportActionBar();
        }
        catch(NullPointerException npe){
            Log.e(TAG, "ActionBar null");
        }

        az = new AzureService();
        NavigationView navView = (NavigationView) findViewById(R.id.drawer_nav_view);
        View header = navView.getHeaderView(0);
        TextView drawerUsername = (TextView) header.findViewById(R.id.drawer_username);
        drawerUsername.setText(getCurrentUsername());
        TextView drawerEmail = (TextView) header.findViewById(R.id.drawer_email);
        drawerEmail.setText(getCurrentEmail());
        CircularImageView drawerProfilePicture = (CircularImageView) header.findViewById(R.id.drawer_profile_picture);
        Picasso.with(this).load(az.getProfileImageURL(this)).into(drawerProfilePicture);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                selectDrawerItem(menuItem);
                return true;
            }
        });

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                mToolbar,  /* nav drawer image to replace 'Up' caret */
                R.string.opendrawer,  /* "open drawer" description for accessibility */
                R.string.closeddrawer  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                try {
                    mActionBar.setTitle(mTitle);
                }catch(NullPointerException npe){
                    Log.e(TAG, "MActionBar null onDrawerClosed");
                }
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                try {
                    mActionBar.setTitle(mDrawerTitle);
                }catch(NullPointerException npe){
                    Log.e(TAG, "MActionBar null onDrawerOpened");
                }
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        // Initialize the ViewPager and set an adapter
    }

    private void selectDrawerItem(MenuItem menuItem) {
        if(menuItem.getItemId() != R.id.drawer_settings) {
            menuItem.setCheckable(true);
            menuItem.setChecked(true);
            if (mPreviousMenuItem != null && mPreviousMenuItem != menuItem) {
                mPreviousMenuItem.setChecked(false);
            }
            mPreviousMenuItem = menuItem;
        }

        //Closing drawer on item click
        mDrawerLayout.closeDrawers();

        Fragment fragment;
        Bundle args;
        //Check to see which item was being clicked and perform appropriate action
        switch (menuItem.getItemId()) {
            //Replacing the main content with ContentFragment Which is our Inbox View;

            case R.id.drawer_home:
                Toast.makeText(getApplicationContext(), "Home Selected", Toast.LENGTH_SHORT).show();
                fragment = new Tab1();
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
                break;
            case R.id.drawer_profile:
                Toast.makeText(getApplicationContext(), "Profile Selected", Toast.LENGTH_SHORT).show();
                args = new Bundle();
                args.putBoolean("isCurrentUser", true);
                args.putString("username", getCurrentUsername());
                fragment = new ProfileFragment();
                fragment.setArguments(args);
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
                break;
            case R.id.drawer_settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;
            case R.id.drawer_events:
                args = new Bundle();
                args.putString("username", getCurrentUsername());
                fragment = new EventBreakdownFragment();
                fragment.setArguments(args);
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
                break;
            case R.id.drawer_chat:
                fragment = new ChatFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
                break;
            default:
                Toast.makeText(getApplicationContext(), "Somethings Wrong", Toast.LENGTH_SHORT).show();
                break;

        }
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
                    Log.i(TAG,"Registered Successfully - RegID: " + hub.register(regid, getCurrentEmail()).getRegistrationId());
                } catch (Exception e) {
                    Log.e(TAG, "GCM register exception");
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
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            return true;
        } else if (id == R.id.sign_out_menu) {
            signOut();
            startActivity(new Intent(MainActivity.this, SigninActivity.class));
            finish();
            return true;
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

    @Override
    public void onFragmentInteraction(User u, String viewType) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("User", u);
        bundle.putString("viewType", viewType);
        UserFragment userFragment = new UserFragment();
        userFragment.setArguments(bundle);
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
}
