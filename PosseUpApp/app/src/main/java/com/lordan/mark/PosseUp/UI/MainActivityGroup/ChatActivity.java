package com.lordan.mark.PosseUp.UI.MainActivityGroup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lordan.mark.PosseUp.BasicAdapter;
import com.lordan.mark.PosseUp.DataOperations.AzureService;
import com.lordan.mark.PosseUp.MessageAdapter;
import com.lordan.mark.PosseUp.Model.ChatMessage;
import com.lordan.mark.PosseUp.R;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

import java.util.ArrayList;
import java.util.Calendar;

public class ChatActivity extends AppCompatActivity {
    private static final String EVENT_NAME = "com.marklordan.posseup.event_name";
    private Pubnub pubnub;
    private ArrayList<ChatMessage> chatLog = new ArrayList<>();
    private MessageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (toolbar != null) {
            toolbar.setTitle(getIntent().getStringExtra(EVENT_NAME));
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        RecyclerView chat = (RecyclerView) findViewById(R.id.chat_message_recyclerview);
        chat.setLayoutManager(layoutManager);
        adapter = new MessageAdapter(getApplicationContext(),chatLog);
        chat.setAdapter(adapter);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final Callback callback = new Callback() {
            public void successCallback(String channel, Object response) {
                System.out.println(response.toString());
            }
            public void errorCallback(String channel, PubnubError error) {
                System.out.println(error.toString());
            }
        };
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatMessage message = new ChatMessage("Hello", Calendar.getInstance().getTime().toString(), new AzureService().getCurrentUsername(getApplicationContext()), new AzureService().getProfileImageURL(getApplicationContext()));
                pubnub.publish("test_channel", new Gson().toJson(message), callback);
                //pubnub.publish("test_channel", "Hello from the PubNub Java SDK!" , callback);
                Toast.makeText(ChatActivity.this, chatLog.size() + " ", Toast.LENGTH_SHORT).show();
            }
        });
        pubnub = new Pubnub("pub-c-80485b35-97d9-4403-8465-c5a6e2547d65", "sub-c-2b32666a-f73e-11e5-8cfb-0619f8945a4f");
        subscribeWithPubNub();




    }

    public static Intent newIntent(Context context, String eventName) {
        Intent i = new Intent(context, ChatActivity.class);
        i.putExtra(EVENT_NAME, eventName);
        return i;
    }

    private void subscribeWithPubNub() {
        try {
            pubnub.subscribe("test_channel", new Callback() {
                @Override
                public void connectCallback(String channel, Object message) {
                    System.out.println("SUBSCRIBE : CONNECT on channel:" + channel
                            + " : " + message.getClass() + " : "
                            + message.toString());
                }

                @Override
                public void disconnectCallback(String channel, Object message) {
                    System.out.println("SUBSCRIBE : DISCONNECT on channel:" + channel
                            + " : " + message.getClass() + " : "
                            + message.toString());
                }

                public void reconnectCallback(String channel, Object message) {
                    System.out.println("SUBSCRIBE : RECONNECT on channel:" + channel
                            + " : " + message.getClass() + " : "
                            + message.toString());
                }

                @Override
                public void successCallback(String channel, Object message) {
                    System.out.println("SUBSCRIBE : " + channel + " : "
                            + message.getClass() + " : " + message.toString());
                    addMessage(message);
                }

                @Override
                public void errorCallback(String channel, PubnubError error) {
                    System.out.println("SUBSCRIBE : ERROR on channel " + channel
                            + " : " + error.toString());
                }
            });
        } catch (PubnubException e) {
            e.printStackTrace();
        }
    }
    private void addMessage(Object message){
        chatLog.add(new Gson().fromJson(message.toString(), ChatMessage.class));
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyItemInserted(chatLog.size()-1);
            }
        });
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        pubnub.unsubscribe("test_channel");
    }

}
