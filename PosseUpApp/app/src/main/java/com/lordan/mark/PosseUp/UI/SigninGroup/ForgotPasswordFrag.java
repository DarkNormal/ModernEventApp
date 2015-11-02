package com.lordan.mark.PosseUp.UI.SigninGroup;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lordan.mark.PosseUp.AbstractActivity;
import com.lordan.mark.PosseUp.DataOperations.AzureService;
import com.lordan.mark.PosseUp.Model.User;
import com.lordan.mark.PosseUp.R;
import com.lordan.mark.PosseUp.UI.MainActivity;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;

/**
 * Created by Mark on 10/10/2015.
 */
public class ForgotPasswordFrag extends Fragment {
    private View forgotPasswordView;
    private ProgressDialog mProgressDialog;
    private MobileServiceClient mobileServiceClient = AbstractActivity.mobileServiceClient;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Bundle bundle = this.getArguments();
        String email = "";
        if(bundle != null){
             email = bundle.getString("email");
        }
        forgotPasswordView = inflater.inflate(R.layout.forgotpasswordfrag_layout, container, false);
        final EditText emailInput = (EditText) forgotPasswordView.findViewById(R.id.forgotpassword_email);
        emailInput.setText(email);
        final EditText temppassword = (EditText) forgotPasswordView.findViewById(R.id.uid);
        Button confirm = (Button) forgotPasswordView.findViewById(R.id.reset_begin);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!emailInput.getText().toString().isEmpty() && !temppassword.getText().toString().isEmpty()){
                    mProgressDialog = ProgressDialog.show(getActivity(), "Resetting password",
                            "Please wait...", true);

                    tempLogin(emailInput.getText().toString(), temppassword.getText().toString() );
                }
                else{
                    if(emailInput.getText().toString().isEmpty()){
                        emailInput.setError("Enter a username or email");
                    }
                    else{
                        temppassword.setError("Enter a password");
                    }
                }

            }
        });
        Button cancel = (Button) forgotPasswordView.findViewById(R.id.reset_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);

                SigninFrag newFragment = SigninFrag.newInstance();
                ft.replace(R.id.fragmentHolder, newFragment, "forgotpass_frag");
                ft.commit();
            }
        });
        return forgotPasswordView;
    }
    public static ForgotPasswordFrag newInstance()
    {
        ForgotPasswordFrag myFragment = new ForgotPasswordFrag();
        return myFragment;
    }
    public void tempLogin(String usernameOrEmail, String password) {

//        final User user = new User();
//        user.setEmailOrUsername(usernameOrEmail);
//        user.setPassword(password);
//        ListenableFuture<JsonElement> result = mobileServiceClient.invokeApi("forgot_password_confirmed", user, JsonElement.class);
//
//        Futures.addCallback(result, new FutureCallback<JsonElement>() {
//            @Override
//            public void onSuccess(JsonElement result) {
//                System.out.println("hooray!");
//                if (result.isJsonObject()) {
//                    JsonObject resultObj = result.getAsJsonObject();
//                    if (resultObj.get("status").getAsString().equals("SUCCESS")) {
//                        MobileServiceUser mUser = new MobileServiceUser(resultObj.get("userId").getAsString());
//                        mUser.setAuthenticationToken(resultObj.get("token").toString());
//                        mobileServiceClient.setCurrentUser(mUser);
//                        AzureService az = new AzureService();
//                        az.saveUserData(getActivity(), mobileServiceClient, user.getUsername(), user.getEmail());
//                        Intent intent = new Intent(getActivity(), MainActivity.class);
//                        startActivity(intent);
//                        getActivity().finish();
//                    } else {
//                        //incorrect username/password
//
//                        mProgressDialog.dismiss();
//                    }
//
//                } else {
//                    System.out.println("dang");
//                    mProgressDialog.dismiss();
//                }
//
//            }
//
//            @Override
//            public void onFailure(Throwable exc) {
//                System.out.println("boo-urns!");
//                mProgressDialog.dismiss();
//            }
//
//
//        });
    }
}
