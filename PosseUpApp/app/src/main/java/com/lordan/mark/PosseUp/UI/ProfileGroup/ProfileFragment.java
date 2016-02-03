package com.lordan.mark.PosseUp.UI.ProfileGroup;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.astuetz.PagerSlidingTabStrip;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.lordan.mark.PosseUp.DataOperations.AzureService;
import com.lordan.mark.PosseUp.Model.ChangePasswordModel;
import com.lordan.mark.PosseUp.Model.Constants;
import com.lordan.mark.PosseUp.R;
import com.lordan.mark.PosseUp.SlidingTabs.ViewPagerAdapter;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Mark on 31/01/2016.
 */
public class ProfileFragment extends Fragment {
    private ViewSwitcher viewSwitcher;
    private MaterialEditText materialEditText;
    private TextView username;
    private RequestQueue queue;
    private AlertDialog dialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.profile_layout, container, false);

        AzureService az = new AzureService();

        viewSwitcher = (ViewSwitcher) rootView.findViewById(R.id.profile_username_swticher);
        materialEditText = (MaterialEditText) rootView.findViewById(R.id.profile_username_edit);
        username = (TextView) rootView.findViewById(R.id.profile_username);
        username.setText(az.getCurrentUsername(getContext()));

        ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.profile_pager);
        viewPager.setAdapter(new ProfilePagerAdapter(getChildFragmentManager(), getContext()));
        TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.profile_tabs);
        tabLayout.setupWithViewPager(viewPager);
        queue = Volley.newRequestQueue(getContext());

        return rootView;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_profile, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public void onPrepareOptionsMenu(Menu menu){
        super.onPrepareOptionsMenu(menu);
        MenuItem checkItem = menu.findItem(R.id.edit_profile);
        MenuItem menuItem = menu.findItem(R.id.edit_profile_password);
        if(checkItem.isChecked()){
            menuItem.setVisible(false);
        }
        else menuItem.setVisible(true);


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.edit_profile:
                if (!item.isChecked()) {
                    username.setText(materialEditText.getText().toString());
                    viewSwitcher.showPrevious();
                    item.setIcon(R.drawable.ic_mode_edit);
                    item.setChecked(true);
                    //TODO save changes to web service
                    //TODO also discard changes if cancelled
                }
                else{
                    materialEditText.setText(username.getText().toString());
                    viewSwitcher.showNext();
                    item.setIcon(R.drawable.ic_done_white);
                    item.setChecked(false);
                }
                Toast.makeText(getContext(), "Edit profile", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.edit_profile_password:

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                LayoutInflater inflater = getActivity().getLayoutInflater();



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
                return true;
            default:
                return false;

        }
    }
    private AlertDialog getPasswordDialog(LinearLayout linearLayout){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle("Change Password");
        builder.setView(linearLayout);
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                MaterialEditText oldPassword = (MaterialEditText) linearLayout.findViewById(R.id.edit_profile_oldPassword);
//                MaterialEditText newPassword = (MaterialEditText) linearLayout.findViewById(R.id.edit_profile_newPassword);
//                MaterialEditText confirmPassword = (MaterialEditText) linearLayout.findViewById(R.id.edit_profile_confirmPassword);
//                changePassword(oldPassword, newPassword, confirmPassword);


            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        return dialog;
    }

    private void changePassword(MaterialEditText old, MaterialEditText newPassword, MaterialEditText confirmNewPassword){

        if(newPassword.getText().toString().equals(confirmNewPassword.getText().toString())){
            ChangePasswordModel changePasswordModel = new ChangePasswordModel(old.getText().toString(), newPassword.getText().toString(), confirmNewPassword.getText().toString());
            Gson gson = new Gson();
            String PasswordModelString = gson.toJson(changePasswordModel);
            JSONObject passwordModel = new JSONObject();
            try {
                passwordModel = new JSONObject(PasswordModelString);
                passwordModel.put("Email", new AzureService().getCurrentEmail(getContext()));
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
                            passwordChange(true, null);
                        }
                        else{
                            passwordChange(false, response);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
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
            Toast.makeText(getContext(), "Passwords dont match", Toast.LENGTH_SHORT).show();
        }
    }
    private void passwordChange(boolean changed, JSONObject response){
        if(response != null){
            try {
                Toast.makeText(getContext(), response.getString("cause"), Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        else{
            Toast.makeText(getContext(), "Password changed", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        }
    }

}
