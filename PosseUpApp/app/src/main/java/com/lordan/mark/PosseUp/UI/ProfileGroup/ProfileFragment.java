package com.lordan.mark.PosseUp.UI.ProfileGroup;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;

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


import com.android.volley.RequestQueue;

import com.android.volley.toolbox.Volley;

import com.lordan.mark.PosseUp.DataOperations.AzureService;

import com.lordan.mark.PosseUp.R;

import com.rengwuxian.materialedittext.MaterialEditText;

/**
 * Created by Mark on 31/01/2016.
 */
public class ProfileFragment extends Fragment {
    private ViewSwitcher viewSwitcher;
    private MaterialEditText materialEditText;
    private TextView username;
    private AlertDialog dialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.account_profile_layout, container, false);

        AzureService az = new AzureService();
//        AppCompatButton btn =(AppCompatButton) rootView.findViewById(R.id.edit_profile_btn);
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), ProfileActivity.class);
//                startActivity(intent);
//            }
//        });
//        viewSwitcher = (ViewSwitcher) rootView.findViewById(R.id.profile_username_swticher);
//        materialEditText = (MaterialEditText) rootView.findViewById(R.id.profile_username_edit);
//        username = (TextView) rootView.findViewById(R.id.profile_username);
//        username.setText(az.getCurrentUsername(getContext()));
//
//        ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.profile_pager);
//        viewPager.setAdapter(new ProfilePagerAdapter(getChildFragmentManager(), getContext()));
//        TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.profile_tabs);
//        tabLayout.setupWithViewPager(viewPager);
        RequestQueue queue = Volley.newRequestQueue(getContext());

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
                    item.setIcon(R.drawable.ic_action_tick);
                    item.setChecked(false);
                }
                Toast.makeText(getContext(), "Edit profile", Toast.LENGTH_SHORT).show();
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
        return builder.create();
    }


}
