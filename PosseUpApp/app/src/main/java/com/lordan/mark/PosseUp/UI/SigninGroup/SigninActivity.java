package com.lordan.mark.PosseUp.UI.SigninGroup;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

import android.os.Bundle;

import android.widget.LinearLayout;

import com.lordan.mark.PosseUp.AbstractActivity;

import com.lordan.mark.PosseUp.LanguageHelper;

import com.lordan.mark.PosseUp.R;


/**
 * Created by Mark on 8/31/2015.
 */
public class SigninActivity extends AbstractActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LanguageHelper.changeLocale(this.getResources(), "en");
        setContentView(R.layout.signin_layout);
        LinearLayout fragmentHolder = (LinearLayout) findViewById(R.id.fragmentHolder);
        FragmentManager fragMan = getFragmentManager();
        FragmentTransaction fragTransaction = fragMan.beginTransaction();
        Fragment myFrag = new SigninFrag();
        fragTransaction.add(fragmentHolder.getId(), myFrag, "signin_fragment");
        fragTransaction.commit();


    }
}



