package com.lordan.mark.PosseUp.UI.ProfileGroup;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.lordan.mark.PosseUp.AbstractActivity;
import com.lordan.mark.PosseUp.DataOperations.AzureService;
import com.lordan.mark.PosseUp.Model.ChangePasswordModel;
import com.lordan.mark.PosseUp.Model.Constants;
import com.lordan.mark.PosseUp.R;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Mark on 03/02/2016.
 */
public class EditProfileActivity extends AbstractActivity {
    private AlertDialog dialog;
    private RequestQueue queue;
    private MaterialEditText username;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile_layout);

        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.edit_profile_activity_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        username = (MaterialEditText) findViewById(R.id.profile_username_edit);
        username.setText(getCurrentUsername());
        queue = Volley.newRequestQueue(this);
        AppCompatButton changePassword = (AppCompatButton) findViewById(R.id.change_password_button);
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPasswordDialog();
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_save, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.menu_save:
                //TODO save changes
                showConfirmDialog();
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
    private void showConfirmDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialogStyle);
        builder.setTitle("Confirm changes");
        LayoutInflater inflater = getLayoutInflater();
        final View layoutView = inflater.inflate(R.layout.profile_edit_dialog, null);
        builder.setView(layoutView);
        builder.setPositiveButton("Yes", null);
        builder.setNegativeButton("No", null);
        dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialEditText password = (MaterialEditText) layoutView.findViewById(R.id.edit_profile_confirmPasswordDialog);
                boolean valid = true;
                if (TextUtils.isEmpty(password.getText())) {
                    password.setError("Cannot be empty");
                    valid = false;
                }
                if (valid) {
                    changeProfileInfo(password);
                }

            }
        });
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }
    private void changeProfileInfo(MaterialEditText password){
        JSONObject newDetails = new JSONObject();

        try {
            newDetails.put("Email", getCurrentEmail());
            newDetails.put("Password", password.getText().toString());
            newDetails.put("Username", username.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = Constants.baseUrl + "api/Account/ChangeUsername";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, newDetails, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("CHANGEUSERNAME", response.toString());
                try {
                    if(response.getBoolean("success")){
                        detailsChange(true, null);
                    }
                    else{
                        detailsChange(false, response);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) throws NullPointerException{
                NetworkResponse networkResponse = error.networkResponse;
                Log.e("CHANGEUSERNAME", "error");
                switch (networkResponse.statusCode){
                    case 400:
                        break;
                    case 401:
                        Log.i("CHANGEUSERNAME", "UNAUTHORIZED");
                        break;
                    default:
                        break;
                }
            }
        });
        queue.add(jsonObjectRequest);

    }

    private void showPasswordDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialogStyle);
        LayoutInflater inflater = getLayoutInflater();
        builder.setTitle("Change Password");
        final View layoutView = inflater.inflate(R.layout.profile_edit_password_dialog, null);
        builder.setView(layoutView);
        builder.setPositiveButton("Save", null);
        builder.setNegativeButton("Cancel", null);
        dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialEditText oldPassword = (MaterialEditText) layoutView.findViewById(R.id.edit_profile_oldPassword);
                MaterialEditText newPassword = (MaterialEditText) layoutView.findViewById(R.id.edit_profile_newPassword);
                MaterialEditText confirmPassword = (MaterialEditText) layoutView.findViewById(R.id.edit_profile_confirmPassword);
                boolean valid = true;
                if(TextUtils.isEmpty(oldPassword.getText())){
                    oldPassword.setError("Cannot be empty");
                    valid = false;
                }
                if(TextUtils.isEmpty(newPassword.getText())){
                    newPassword.setError("Cannot be empty");
                    valid = false;
                }
                if(TextUtils.isEmpty(confirmPassword.getText())){
                    confirmPassword.setError("Cannot be empty");
                    valid = false;
                }
                if(valid){
                    changePassword(oldPassword, newPassword, confirmPassword);
                }

            }
        });
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
    private void changePassword(MaterialEditText old, MaterialEditText newPassword, MaterialEditText confirmNewPassword){

        if(newPassword.getText().toString().equals(confirmNewPassword.getText().toString())){
            ChangePasswordModel changePasswordModel = new ChangePasswordModel(old.getText().toString(), newPassword.getText().toString(), confirmNewPassword.getText().toString());
            Gson gson = new Gson();
            String PasswordModelString = gson.toJson(changePasswordModel);
            JSONObject passwordModel = new JSONObject();
            try {
                passwordModel = new JSONObject(PasswordModelString);
                passwordModel.put("Email", new AzureService().getCurrentEmail(this));
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            String url = Constants.baseUrl + "api/Account/ChangePassword";
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, passwordModel, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.i("CHANGEPASSWORD", response.toString());
                    try {
                        if(response.getBoolean("success")){
                            detailsChange(true, null);
                        }
                        else{
                            detailsChange(false, response);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) throws NullPointerException{
                    NetworkResponse networkResponse = error.networkResponse;
                    Log.e("CHANGEPASSWORD", "error");
                    switch (networkResponse.statusCode){
                        case 400:
                            break;
                        case 401:
                            Log.i("CHANGEPASSWORD", "UNAUTHORIZED");
                            break;
                        default:
                            break;
                    }
                }
            });
            queue.add(jsonObjectRequest);
        }
        else{
            Toast.makeText(this, "Passwords dont match", Toast.LENGTH_SHORT).show();
        }
    }
    private void detailsChange(boolean changed, JSONObject response){
        if(response != null){
            try {
                Toast.makeText(this, response.getString("cause"), Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        else{
            Toast.makeText(this, "Details changed", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        }
    }
}
