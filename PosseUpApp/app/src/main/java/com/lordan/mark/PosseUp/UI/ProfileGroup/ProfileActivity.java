package com.lordan.mark.PosseUp.UI.ProfileGroup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.lordan.mark.PosseUp.AbstractActivity;
import com.lordan.mark.PosseUp.Model.User;
import com.lordan.mark.PosseUp.R;
import com.lordan.mark.PosseUp.UI.EventDetailGroup.UserFragment;

/**
 * Created by Mark on 04/03/2016
 */
public class ProfileActivity extends AbstractActivity implements ProfileFragment.OnFragmentInteractionListener, UserFragment.OnListFragmentInteractionListener {
    private String username;
    private final FragmentManager fragmentManager = getSupportFragmentManager();

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
            boolean isCurrentUser = getCurrentUsername().equals(username);
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


    @Override
    public void onFragmentInteraction(User u, String viewType) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("User", u);
        bundle.putString("viewType", viewType);
        UserFragment userFragment = new UserFragment();
        userFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_content_holder, userFragment).addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onListFragmentInteraction(User u) {

    }
}
