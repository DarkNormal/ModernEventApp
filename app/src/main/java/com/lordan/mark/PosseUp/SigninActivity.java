package com.lordan.mark.PosseUp;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Mark on 8/31/2015.
 */
public class SigninActivity extends AbstractActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LanguageHelper.changeLocale(this.getResources(), "fr");
        setContentView(R.layout.signin_layout);

        TextView forgotPassword = (TextView) findViewById(R.id.forgot_password);
        TextView noAccount = (TextView) findViewById(R.id.no_account);                  //TODO merge these two strings together as there is
        TextView signUp = (TextView) findViewById(R.id.signup_text);                    //TODO a non clickable gap between them of 2-4dp
        makeTextClickable(forgotPassword);
        makeTextClickable(noAccount);
        makeTextClickable(signUp);

    }

    @Override
    public void onBackPressed() {
        /*Override the back button action on sign in activity
        * This prevents the return to StartActivity (logo screen)
        */
            moveTaskToBack(true);
    }
    public void makeTextClickable(TextView toClick){    //adds click listener to any TextViews within this activity
        toClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int duration = Toast.LENGTH_SHORT;
                Context context = getApplicationContext();
                Toast toast = Toast.makeText(context, "Clicked!", duration);
                toast.show();
            }
        });

    }

}
