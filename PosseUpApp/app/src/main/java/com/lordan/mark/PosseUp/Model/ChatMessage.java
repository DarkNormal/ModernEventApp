package com.lordan.mark.PosseUp.Model;

import com.lordan.mark.PosseUp.ChatAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Calendar;

/**
 * Created by Mark on 06/04/2016.
 */
public class ChatMessage {
    private String content;
    private String timestamp;
    private String username;
    private String userProfilePicture;

    public ChatMessage(String content, Calendar timestamp, String username, String userProfilePicture){
        this.content = content;
        this.username = username;
        this.timestamp = Constants.chatFormatter.format(timestamp.getTime());
        this.userProfilePicture = userProfilePicture;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserProfilePicture() {
        return userProfilePicture;
    }

    public void setUserProfilePicture(String userProfilePicture) {
        this.userProfilePicture = userProfilePicture;
    }
}
