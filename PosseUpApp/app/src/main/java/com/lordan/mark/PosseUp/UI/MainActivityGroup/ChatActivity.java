package com.lordan.mark.PosseUp.UI.MainActivityGroup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lordan.mark.PosseUp.DataOperations.AzureService;
import com.lordan.mark.PosseUp.util.MessageAdapter;
import com.lordan.mark.PosseUp.Model.ChatMessage;
import com.lordan.mark.PosseUp.R;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;

public class ChatActivity extends AppCompatActivity {
    private static final String EVENT_NAME = "com.marklordan.posseup.event_name";
    private static final String EVENT_ID = "com.marklordan.posseup.event_id";
    private Pubnub pubnub;
    private ArrayList<ChatMessage> chatLog = new ArrayList<>();
    private MessageAdapter adapter;
    private RecyclerView chat;
    private String eventID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        eventID = String.valueOf(getIntent().getIntExtra(EVENT_ID, 0));
        if (toolbar != null) {
            toolbar.setTitle(getIntent().getStringExtra(EVENT_NAME));
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);


        chat = (RecyclerView) findViewById(R.id.chat_message_recyclerview);
        if(chat != null) {
            chat.setLayoutManager(layoutManager);
        }
        adapter = new MessageAdapter(getApplicationContext(),chatLog);
        chat.setAdapter(adapter);
        AppCompatButton btn = (AppCompatButton) findViewById(R.id.message_send_btn);
        final Callback callback = new Callback() {
            public void successCallback(String channel, Object response) {
                System.out.println(response.toString());
            }
            public void errorCallback(String channel, PubnubError error) {
                System.out.println(error.toString());
            }
        };
        final EditText messageToSend = (EditText) findViewById(R.id.message_to_send);
        if(btn != null) {
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ChatMessage message = new ChatMessage(messageToSend.getText().toString(),
                            Calendar.getInstance(),
                            new AzureService().getCurrentUsername(getApplicationContext()),
                            new AzureService().getProfileImageURL(getApplicationContext()));
                    if(messageToSend != null) {
                        messageToSend.getText().clear();
                    }
                    pubnub.publish(eventID, new Gson().toJson(message), callback);
                }
            });
        }
        pubnub = new Pubnub("pub-c-80485b35-97d9-4403-8465-c5a6e2547d65", "sub-c-2b32666a-f73e-11e5-8cfb-0619f8945a4f");

        pubnub.history(eventID, 20, true, new Callback() {
            @Override
            public void successCallback(String channel, Object message) {
                super.successCallback(channel, message);
                JSONArray jsonArray = (JSONArray) message;
                JSONArray messages = null;
                try {
                    messages = jsonArray.getJSONArray(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < messages.length(); i++) {
                    try {
                        addMessage(messages.getString(i));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
            @Override
            public void errorCallback(String channel, PubnubError error) {
                System.out.println("History Error : ERROR on channel " + channel
                        + " : " + error.toString());
            }

        });
        subscribeWithPubNub();




    }

    public static Intent newIntent(Context context, String eventName, int eventID) {
        Intent i = new Intent(context, ChatActivity.class);
        i.putExtra(EVENT_ID, eventID);
        i.putExtra(EVENT_NAME, eventName);
        return i;
    }

    private void subscribeWithPubNub() {
        try {
            pubnub.subscribe(eventID, new Callback() {
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
    private void addMessage(final Object message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                chatLog.add(new Gson().fromJson(message.toString(), ChatMessage.class));
                adapter.notifyItemInserted(chatLog.size()-1);
                chat.scrollToPosition(chatLog.size() -1);
            }
        });
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        pubnub.unsubscribe(eventID);
    }

}
