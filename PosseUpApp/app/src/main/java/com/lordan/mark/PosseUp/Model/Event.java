package com.lordan.mark.PosseUp.Model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Mark on 10/28/2015.
 */
public class Event {
    @SerializedName("event_title")
    private String eventName;
    @SerializedName("event_visibility")
    private String eventVisibility;

    @SerializedName("host")
    private String hostEmail;

    public Event(String name, String visibility, String email){
        this.eventName = name;
        this.eventVisibility = visibility;
        this.hostEmail = email;
    }



    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getHostEmail() {
        return hostEmail;
    }

    public void setHostEmail(String hostEmail) {
        this.hostEmail = hostEmail;
    }

    public String getEventVisibility() {
        return eventVisibility;
    }

    public void setEventVisibility(String eventVisibility) {
        this.eventVisibility = eventVisibility;
    }


}
