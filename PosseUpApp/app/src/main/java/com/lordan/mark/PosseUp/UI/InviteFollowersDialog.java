package com.lordan.mark.PosseUp.UI;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.lordan.mark.PosseUp.R;

import java.util.ArrayList;

/**
 * Created by Mark on 16/04/2016.
 */
public class InviteFollowersDialog extends DialogFragment {
    private String[] followers;
    ArrayList<Integer> mSelectedFollowers = new ArrayList();
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomAlertDialogStyle);

        builder.setTitle("Invite Followers")
                .setMultiChoiceItems(followers, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if(isChecked){
                            mSelectedFollowers.add(which);
                        }
                        else if(mSelectedFollowers.contains(which)){
                            mSelectedFollowers.remove(Integer.valueOf(which));
                        }
                    }
                })
                .setPositiveButton("Invite", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        InviteFollowersDialogListener activity = (InviteFollowersDialogListener) getActivity();
                        String[] selectedFollowers = new String[mSelectedFollowers.size()];
                        for (int i = 0; i < mSelectedFollowers.size(); i++) {
                            selectedFollowers[i] =followers[mSelectedFollowers.get(i)];
                        }
                        activity.onFinishInviteDialog(selectedFollowers);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
    public interface InviteFollowersDialogListener {
        void onFinishInviteDialog(String[] selectedFollowers);
    }
    public void setFollowers(String[] followers){
        this.followers = followers;
    }
}