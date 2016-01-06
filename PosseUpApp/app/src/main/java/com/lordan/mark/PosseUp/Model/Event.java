package com.lordan.mark.PosseUp.Model;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

/**
 * Created by Mark on 10/28/2015.
 */
public class Event extends JSONObject{
    @SerializedName("EventTitle")
    private String eventName;
    @SerializedName("EventVisibility")
    private String eventVisibility;
    @SerializedName("EventDescription")
    private String eventDesc;
    @SerializedName("EventHost")
    private String hostEmail;

    @SerializedName("EventLocationLat")
    private double eventLocationLat;



    @SerializedName("EventLocationLng")
    private double eventLocationLng;

    public Event(String name, String visibility, String email, String eventDesc, double eventLocationLat, double eventLocationLng){
        this.eventName = name;
        this.eventVisibility = visibility;
        this.hostEmail = email;
        this.eventDesc = eventDesc;
        this.eventLocationLat = eventLocationLat;
        this.eventLocationLng = eventLocationLng;
    }

    public Event() {

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
    public String getEventDesc() {
        return eventDesc;
    }

    public void setEventDesc(String eventDesc) {
        this.eventDesc = eventDesc;
    }
    public double getEventLocationLng() {
        return eventLocationLng;
    }

    public void setEventLocationLng(double eventLocationLng) {
        this.eventLocationLng = eventLocationLng;
    }

    public double getEventLocationLat() {
        return eventLocationLat;
    }

    public void setEventLocationLat(double eventLocationLat) {
        this.eventLocationLat = eventLocationLat;
    }


}
