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
 * Created by Mark on 10/10/2015
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

                SignInFragment newFragment = new SignInFragment();
                ft.replace(R.id.fragmentHolder, newFragment);
                ft.commit();
            }
        });
        return forgotPasswordView;

    }
}
