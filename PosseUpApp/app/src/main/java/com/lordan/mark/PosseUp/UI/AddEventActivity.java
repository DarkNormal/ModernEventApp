package com.lordan.mark.PosseUp.UI;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.lordan.mark.PosseUp.AbstractActivity;
import com.lordan.mark.PosseUp.Model.Constants;
import com.lordan.mark.PosseUp.Model.Event;
import com.lordan.mark.PosseUp.R;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Mark on 10/27/2015.
 */
public class AddEventActivity extends AbstractActivity{
    private RequestQueue queue;

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
                Event newEvent = new Event(
                        getInputValues(findViewById(R.id.create_event_title_input)),
                        spinner.getSelectedItem().toString(),
                        getCurrentEmail(),
                        getInputValues(findViewById(R.id.create_event_desc_field)),
                        Double.valueOf(getInputValues(findViewById(R.id.create_event_latitude))),
                        Double.valueOf(getInputValues(findViewById(R.id.create_event_longitude))));

                    createEvent(newEvent);
                }

        });
     getSupportActionBar().setElevation(0);
    }

    private String getInputValues(View holder){
        EditText editView = (EditText) holder;
        return editView.getText().toString();

    }
    private void createEvent(Event newEvent){
        queue = Volley.newRequestQueue(this);
        if(newEvent.getHostEmail() != null){
            JSONObject jsonObject = convertToJSON(newEvent);

            String url = Constants.baseUrl + "api/Events";
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.i("Add-Event", response.toString());
                    Toast.makeText(getApplicationContext(), "Event added to database", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Add-Event", error.networkResponse.toString());
                    Toast.makeText(getApplicationContext(), "Error adding event to database", Toast.LENGTH_SHORT).show();
                }
            });
            queue.add(request);
        }
    }
    private JSONObject convertToJSON(Event obj){
        Gson gson = new Gson();
        String jsonString = gson.toJson(obj);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;

    }

}
