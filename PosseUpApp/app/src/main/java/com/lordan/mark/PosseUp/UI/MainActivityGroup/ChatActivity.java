package com.lordan.mark.PosseUp.UI.MainActivityGroup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.lordan.mark.PosseUp.R;

public class ChatActivity extends AppCompatActivity {
    private static final String EVENT_NAME = "com.marklordan.posseup.event_name";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getIntent().getStringExtra(EVENT_NAME));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


    }

    public static Intent newIntent(Context context, String eventName) {
        Intent i = new Intent(context, ChatActivity.class);
        i.putExtra(EVENT_NAME, eventName);
        return i;
    }

}
