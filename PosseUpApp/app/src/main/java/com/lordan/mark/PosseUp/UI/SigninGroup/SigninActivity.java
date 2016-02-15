package com.lordan.mark.PosseUp.UI.SigninGroup;



import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.LinearLayout;

import com.lordan.mark.PosseUp.AbstractActivity;

import com.lordan.mark.PosseUp.LanguageHelper;

import com.lordan.mark.PosseUp.R;


/**
 * Created by Mark on 8/31/2015.
 */
public class SigninActivity extends AbstractActivity {

    private FragmentManager fragMan;
    private LinearLayout fragmentHolder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LanguageHelper.changeLocale(this.getResources(), "en");
        setContentView(R.layout.signin_layout);
        fragmentHolder = (LinearLayout) findViewById(R.id.fragmentHolder);
        fragMan = getSupportFragmentManager();
        FragmentTransaction fragTransaction = fragMan.beginTransaction();
        Fragment myFrag = new SigninFrag();
        fragTransaction.add(fragmentHolder.getId(), myFrag, "signin_fragment");
        fragTransaction.commit();


    }
    public void switchToRegister(){
        Fragment registerFrag = new RegisterFragment();
        fragMan.beginTransaction().replace(fragmentHolder.getId(), registerFrag).addToBackStack("Register").commit();

    }
    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0 ){
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}



