package com.lordan.mark.PosseUp.UI;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.view.ViewPager;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.lordan.mark.PosseUp.AbstractActivity;
import com.lordan.mark.PosseUp.Item;
import com.lordan.mark.PosseUp.R;
import com.microsoft.windowsazure.mobileservices.*;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.lordan.mark.PosseUp.SlidingTabs.ViewPagerAdapter;
import com.microsoft.windowsazure.mobileservices.http.NextServiceFilterCallback;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilter;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterRequest;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AbstractActivity {
    private MobileServiceClient mClient;
    private MobileServiceTable<Item> itemTable;
    private ProgressBar mProgressBar;
    CharSequence Titles[]={"Home","New" , "Hot", "Top"};
    int Numboftabs =4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        try {
            mClient = new MobileServiceClient(
                    "AZURE URL",
                    "AZURE KEY",
                    this);
                    // .withFilter(new ProgressFilter());
            itemTable = mClient.getTable(Item.class);
        } catch (MalformedURLException e) {
            createAndShowDialog("there was an error creating the Mobile Service. verify the URL", "Error");
        }



        // Initialize the ViewPager and set an adapter
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(), Titles, Numboftabs));

        // Bind the tabs to the ViewPager
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setViewPager(pager);

//        final Item item = new Item();
//        item.Text = "yet another v awesome item!";
//        // Insert the new item
//        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
//            @Override
//            protected Void doInBackground(Void... params) {
//                try {
//                    final Item entity = addItemInTable(item);
//
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            if(!entity.isComplete()){
//                                Context context = getApplicationContext();
//                                CharSequence text = "Posted to DB!";
//                                int duration = Toast.LENGTH_SHORT;
//                                Toast toast = Toast.makeText(context, text, duration);
//                                toast.show();
//                            }
//                        }
//                    });
//                } catch (final Exception e) {
//                    createAndShowDialog(e, "Error");
//                }
//                return null;
//            }
//        };
//
//        runAsyncTask(task);



        getSupportActionBar().setElevation(0);

    }

    public Item addItemInTable(Item item) throws ExecutionException, InterruptedException {
        Item entity = itemTable.insert(item).get();
        return entity;
    }
    private AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            return task.execute();
        }
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

        return super.onOptionsItemSelected(item);
    }

    private void createAndShowDialog(Exception e, String title){
        createAndShowDialog(e.toString(), title);
    }
    private void createAndShowDialog(String message, String title){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(message);
        builder.setTitle(title);
        builder.create().show();
    }

    //	TODO Uncomment the the following code when using a mobile service
	private class ProgressFilter implements ServiceFilter {

        @Override
        public ListenableFuture<ServiceFilterResponse> handleRequest(
                ServiceFilterRequest request, NextServiceFilterCallback next) {

            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (mProgressBar != null) mProgressBar.setVisibility(ProgressBar.VISIBLE);
                }
            });

            SettableFuture<ServiceFilterResponse> result = SettableFuture.create();
            try {
                ServiceFilterResponse response = next.onNext(request).get();
                result.set(response);
            } catch (Exception exc) {
                result.setException(exc);
            }

          dismissProgressBar();
          return result;
        }

      private void dismissProgressBar() {
          runOnUiThread(new Runnable() {

              @Override
              public void run() {
                  if (mProgressBar != null) {
                      mProgressBar.setVisibility(ProgressBar.GONE);
                  }
              }
          });
        }
    }
}
