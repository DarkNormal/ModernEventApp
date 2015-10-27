package com.lordan.mark.PosseUp.UI;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.lordan.mark.PosseUp.AbstractActivity;
import com.lordan.mark.PosseUp.R;

/**
 * Created by Mark on 10/27/2015.
 */
public class AddEventActivity extends AbstractActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addevent_layout);
        Spinner spinner = (Spinner) findViewById(R.id.create_event_type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.event_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }
}
