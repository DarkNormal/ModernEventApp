package com.lordan.mark.PosseUp.UI.SigninGroup;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.JsonElement;
import com.lordan.mark.PosseUp.AbstractActivity;
import com.lordan.mark.PosseUp.Model.User;
import com.lordan.mark.PosseUp.R;
import com.lordan.mark.PosseUp.UI.RegisterActivity;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;

/**
 * Created by Mark on 10/10/2015.
 */
public class SigninFrag extends Fragment implements View.OnClickListener{
    private ProgressDialog mProgressDialog;
    private View detailsView;
    private TextView username;
    private MobileServiceClient mobileServiceClient = AbstractActivity.mobileServiceClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        detailsView = inflater.inflate(R.layout.signinfrag_layout, container, false);
        TextView forgotPassword = (TextView) detailsView.findViewById(R.id.forgot_password);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View v) {
                                                  mProgressDialog = ProgressDialog.show(getActivity(), "Resetting password",
                                                          "Please wait...", true);
                                                  sendEmail();

                                              }
                                          }

        );
        TextView signUp = (TextView) detailsView.findViewById(R.id.signup_text);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RegisterActivity.class);
                startActivity(intent);      //no need to finish this activity here, could cause a loop of activity stack
                //could also cause app to close completely from registerActivity
            }
        });
        Button signInButton = (Button) detailsView.findViewById(R.id.signin_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    username = (TextView) detailsView.findViewById(R.id.username_email);
                    TextView password = (TextView) detailsView.findViewById(R.id.password);
                    if(!username.getText().toString().isEmpty() && !password.getText().toString().isEmpty()) {
                        mProgressDialog = ProgressDialog.show(getActivity(), "Logging in",
                                "Please wait...", true);
                        ((SigninActivity)getActivity()).login(username.getText().toString(), password.getText().toString());
                    }
                    else{
                        if(username.getText().toString().isEmpty()){
                            username.setError("Cannot be blank");
                        }
                        if(password.getText().toString().isEmpty()){
                            password.setError("Cannot be blank");
                        }
                    }

                }

        });
        return detailsView;
    }

    @Override
    public void onClick(View v) {

    }

    private void sendEmail(){
        final TextView username = (TextView) detailsView.findViewById(R.id.username_email);
        if(!username.getText().toString().isEmpty()){

            User forgetfulUser = new User();
            forgetfulUser.setEmail(username.getText().toString());

            ListenableFuture<JsonElement> result = mobileServiceClient.invokeApi("forgot_password",forgetfulUser, JsonElement.class);
            Futures.addCallback(result, new FutureCallback<JsonElement>() {
                @Override
                public void onSuccess(JsonElement result) {
                    if (result.getAsJsonObject().get("status").getAsString().equals("email sent")) {
                        Toast.makeText(getActivity(), "Email sent to " + username.getText().toString(), Toast.LENGTH_SHORT).show();
                        mProgressDialog.dismiss();
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);

                        ForgotPasswordFrag newFragment = ForgotPasswordFrag.newInstance();
                        Bundle bundle = new Bundle();
                        bundle.putString("email", username.getText().toString());
                        newFragment.setArguments(bundle);

                        ft.replace(R.id.fragmentHolder, newFragment, "forgotpass_frag");
                        ft.commit();
                    } else {
                        username.setError("No matching email found");
                        mProgressDialog.dismiss();
                    }

                }

                @Override
                public void onFailure(Throwable t) {

                }
            });
        }
        else{
            username.setError("Enter an email address");
            mProgressDialog.dismiss();
        }

    }
    public static SigninFrag newInstance()
    {
        SigninFrag myFragment = new SigninFrag();
        return myFragment;
    }
}
