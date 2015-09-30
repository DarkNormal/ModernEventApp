package com.lordan.mark.PosseUp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Mark on 9/30/2015.
 */
public class RegisterActivity extends AbstractActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);
        Button signup = (Button) findViewById(R.id.signup_button);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //finish();
                registerUser();
            }
        });
        TextView backToSignIn = (TextView) findViewById(R.id.signin_text);
        backToSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();       //finish the current activity, goes to last activity in stack (sign in activity)
            }
        });
    }

    public void registerUser(){
        final EditText fullName = (EditText) findViewById(R.id.fullname);
        final EditText email = (EditText) findViewById(R.id.email_signup);
        final EditText password = (EditText) findViewById(R.id.password_signup);
        if(fullName.getText().toString().isEmpty()){
            fullName.setError("Please enter your name");
        }
        if(!isValidEmail(email.getText().toString())){
            email.setError("Invalid Email");
        }
        if(!isValidPassword(password.getText().toString())){
            password.setError("Invalid Password");

        }
    }

}
