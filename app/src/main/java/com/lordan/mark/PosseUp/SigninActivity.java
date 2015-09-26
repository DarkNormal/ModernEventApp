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
        forgotPassword.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View v) {
                                                  int duration = Toast.LENGTH_SHORT;
                                                  Context context = getApplicationContext();
                                                  Toast toast = Toast.makeText(context, "Clicked!", duration);
                                                  toast.show();
                                              }
                                          }
        );

    }

}
