package com.lordan.mark.PosseUp.UI.SigninGroup;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


import com.lordan.mark.PosseUp.R;


/**
 * Created by Mark on 10/10/2015.
 */
public class ForgotPasswordFrag extends Fragment {
    private ProgressDialog mProgressDialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Bundle bundle = this.getArguments();
        String email = "";
        if(bundle != null){
             email = bundle.getString("email");
        }
        View forgotPasswordView = inflater.inflate(R.layout.forgotpasswordfrag_layout, container, false);
        final EditText emailInput = (EditText) forgotPasswordView.findViewById(R.id.forgotpassword_email);
        emailInput.setText(email);
        final EditText mTemporaryPassword = (EditText) forgotPasswordView.findViewById(R.id.uid);
        Button confirm = (Button) forgotPasswordView.findViewById(R.id.reset_begin);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!emailInput.getText().toString().isEmpty() && !mTemporaryPassword.getText().toString().isEmpty()){
                    mProgressDialog = ProgressDialog.show(getActivity(), "Resetting password",
                            "Please wait...", true);

                    tempLogin(emailInput.getText().toString(), mTemporaryPassword.getText().toString() );
                }
                else{
                    if(emailInput.getText().toString().isEmpty()){
                        emailInput.setError("Enter a username or email");
                    }
                    else{
                        mTemporaryPassword.setError("Enter a password");
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

                SignInFrag newFragment = new SignInFrag();
                ft.replace(R.id.fragmentHolder, newFragment);
                ft.commit();
            }
        });
        return forgotPasswordView;
    }
    public static ForgotPasswordFrag newInstance()
    {
        return new ForgotPasswordFrag();
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
