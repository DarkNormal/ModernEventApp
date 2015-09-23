package com.lordan.mark.PosseUp;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by Mark on 7/18/2015.
 * Abstract activity to set orientation of all Activities to Portrait
 * Every activity is a child of this class, or should be anyway
 */

public abstract class AbstractActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
}
