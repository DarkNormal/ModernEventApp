package com.lordan.mark.PosseUp;

import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by Mark on 10/3/2015.
 */
public class ConfigureAccount extends AbstractActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        String name = getIntent().getStringExtra("name").substring(0,1) + getIntent().getStringExtra("name").substring(1);
        setContentView(R.layout.configure_account_layout);
        TextView user_fName = (TextView) findViewById(R.id.welcome_name);
        user_fName.setText(" " + name);

    }

}
