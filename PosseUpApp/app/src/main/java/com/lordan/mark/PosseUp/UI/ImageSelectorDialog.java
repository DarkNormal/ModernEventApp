package com.lordan.mark.PosseUp.UI;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.lordan.mark.PosseUp.R;

/**
 * Created by Mark on 31/03/2016.
 */
public class ImageSelectorDialog extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomAlertDialogStyle);
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View v =inflater.inflate(R.layout.image_selector_layout, null);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(v);
        TextView camera = (TextView) v.findViewById(R.id.camera_button);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTargetFragment().onActivityResult(getTargetRequestCode(), 11 , getActivity().getIntent());

            }
        });
        TextView gallery = (TextView) v.findViewById(R.id.gallery_button);
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTargetFragment().onActivityResult(getTargetRequestCode(), 12 , getActivity().getIntent());

            }
        });
        builder.setTitle("Select image");
        // Create the AlertDialog object and return it
        return builder.create();
    }

}
