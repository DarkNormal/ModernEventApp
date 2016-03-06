package com.lordan.mark.PosseUp.UI.ProfileGroup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Toast;

import com.lordan.mark.PosseUp.AbstractActivity;
import com.lordan.mark.PosseUp.R;
import com.lordan.mark.PosseUp.UI.EventDetailGroup.EventDetailsFragment;

/**
 * Created by Mark on 04/03/2016.
 */
public class ProfileActivity extends AbstractActivity {
    private String username;
    private FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scrollview_layout);
        Intent extras = getIntent();
        try{
            username = extras.getStringExtra("username");
        }
        catch(Exception e){
            e.printStackTrace();
        }
        if (username != null) {
            boolean isCurrentUser = getCurrentUsername() == username;
            Bundle fragmentBundle = new Bundle();
            fragmentBundle.putString("username", username);
            fragmentBundle.putBoolean("isCurrentUser", isCurrentUser);
            fragmentBundle.putString("currentUsername", getCurrentUsername());
            if(savedInstanceState == null) {
                ProfileFragment fragment = new ProfileFragment();
                fragment.setArguments(fragmentBundle);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.fragment_content_holder, fragment);
                fragmentTransaction.commit();
            }
        }


    }

    public void viewFollowers(View view) {
        Toast.makeText(getApplicationContext(), "TODO display recyclerview of followers", Toast.LENGTH_SHORT).show();
    }
    public void viewFollowing(View view) {
        Toast.makeText(getApplicationContext(), "TODO display recyclerview of following", Toast.LENGTH_SHORT).show();
    }

    public void viewEvents(View view) {
        Toast.makeText(getApplicationContext(), "TODO display recyclerview of events attended and hosted", Toast.LENGTH_SHORT).show();
    }
}
