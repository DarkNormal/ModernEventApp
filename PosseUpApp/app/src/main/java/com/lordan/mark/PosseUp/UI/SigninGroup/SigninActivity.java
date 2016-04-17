package com.lordan.mark.PosseUp.UI.SigninGroup;



import android.content.Intent;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lordan.mark.PosseUp.AbstractActivity;
import com.lordan.mark.PosseUp.R;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by Mark on 8/31/2015
 */
public class SigninActivity extends AbstractActivity {

    @Bind(R.id.fragmentHolder)
    public LinearLayout fragmentHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin_layout);
        ButterKnife.bind(this);
        if(savedInstanceState == null){
            Fragment myFrag = new SignInFragment();
            getSupportFragmentManager().beginTransaction().add(fragmentHolder.getId(), myFrag, "signin_fragment").commit();
        }
    }
    public void switchToRegister(){
        Fragment registerFrag = new RegisterFragment();
        getSupportFragmentManager().beginTransaction().replace(fragmentHolder.getId(), registerFrag).addToBackStack("register").commit();

    }
    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0 ){
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //myFrag.onActivityResult(requestCode,resultCode,data);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();

        ImageView background = (ImageView)findViewById(R.id.signin_background);
        if(background != null) {
            background.setImageDrawable(null);
            background = null;
        }
    }
}



