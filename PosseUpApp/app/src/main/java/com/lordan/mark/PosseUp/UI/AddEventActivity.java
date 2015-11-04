package com.lordan.mark.PosseUp.UI;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
        final Spinner spinner = (Spinner) findViewById(R.id.create_event_type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.event_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        Button createEventBtn = (Button) findViewById(R.id.create_event_button);
        createEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText eventTitle = (EditText) findViewById(R.id.create_event_title_input);
                if(!eventTitle.getText().toString().isEmpty()){
                    createEvent(eventTitle.getText().toString(), spinner.getSelectedItem().toString());
                }
            }
        });
    }
    private void createEvent(String eventTitle, String eventVisibility){
//        Event newEvent = new Event(eventTitle, eventVisibility, AbstractActivity.mEmail);
//        ListenableFuture<JsonElement> result = mobileServiceClient.invokeApi( "Events", newEvent, JsonElement.class );
//
//        Futures.addCallback(result, new FutureCallback<JsonElement>() {
//            @Override
//            public void onSuccess(JsonElement result) {
//                Toast.makeText(getApplicationContext(), "Event created", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//                System.out.println(t.getStackTrace().toString());
//            }
//        });
    }

    }
