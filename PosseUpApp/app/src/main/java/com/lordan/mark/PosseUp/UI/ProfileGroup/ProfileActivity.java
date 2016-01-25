package com.lordan.mark.PosseUp.UI.ProfileGroup;

import android.os.Bundle;
import android.widget.TextView;

import com.lordan.mark.PosseUp.AbstractActivity;
import com.lordan.mark.PosseUp.DataOperations.AzureService;
import com.lordan.mark.PosseUp.R;

/**
 * Created by Mark on 25/01/2016.
 */
public class ProfileActivity extends AbstractActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AzureService az = new AzureService();
        setContentView(R.layout.profile_layout);

        TextView username = (TextView) findViewById(R.id.profile_username);
        username.setText(az.getCurrentUsername(this));

    }

}
