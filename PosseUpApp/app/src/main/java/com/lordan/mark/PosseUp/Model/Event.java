package com.lordan.mark.PosseUp.Model;

import com.google.android.gms.location.places.Place;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.GregorianCalendar;

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

    @SerializedName("EventStartDateTime")
    private Calendar startDateTime;


    @SerializedName("EventEndDateTime")
    private Calendar endDateTime;
    @SerializedName("EventLocationLat")
    private double eventLocationLat;
    @SerializedName("EventLocationLng")
    private double eventLocationLng;

    private Place placeDetails;

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

    public Calendar getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(Calendar startDateTime) {
        this.startDateTime = startDateTime;
    }

    public Place getPlaceDetails() {
        return placeDetails;
    }

    public void setPlaceDetails(Place placeDetails) {
        this.placeDetails = placeDetails;
    }

    public Calendar getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(Calendar endDateTime) {
        this.endDateTime = endDateTime;
    }



}
