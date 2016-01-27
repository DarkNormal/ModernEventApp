package com.lordan.mark.PosseUp.UI;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.view.ViewPager;

import com.google.android.gms.common.api.BooleanResult;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.common.collect.ObjectArrays;
import com.lordan.mark.PosseUp.AbstractActivity;
import com.lordan.mark.PosseUp.Model.MyHandler;
import com.lordan.mark.PosseUp.UI.ProfileGroup.ProfileActivity;
import com.lordan.mark.PosseUp.R;
import com.lordan.mark.PosseUp.UI.SigninGroup.SigninActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.lordan.mark.PosseUp.SlidingTabs.ViewPagerAdapter;
import com.microsoft.windowsazure.messaging.NotificationHub;
import com.microsoft.windowsazure.notifications.NotificationsManager;


public class MainActivity extends AbstractActivity{
    CharSequence Titles[]={"Home","Nearby" , "Chat", "Me"};
    int Numboftabs =4;

    private String SENDER_ID = "851010273767";
    private GoogleCloudMessaging gcm;
    private NotificationHub hub;
    private String HubName = "PosseUp-Notifications";
    private String HubListenConnectionString = "Endpoint=sb://posseup-notificationhub.servicebus.windows.net/;SharedAccessKeyName=DefaultListenSharedAccessSignature;SharedAccessKey=ULoYyvlyIcvWKY1XFmUYB8OGNTOfzIKHT11m1AukTuc=";
    private static Boolean isVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupGcm();

        // Initialize the ViewPager and set an adapter
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(), Titles, Numboftabs));

        // Bind the tabs to the ViewPager
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setViewPager(pager);



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
}
