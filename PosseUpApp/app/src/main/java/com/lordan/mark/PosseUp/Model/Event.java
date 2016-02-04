package com.lordan.mark.PosseUp.Model;

import com.google.android.gms.location.places.Place;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
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

    @SerializedName("EventStartTime")
    private String startDateTime;

    private Calendar startingTime;

    private Calendar endingTime;
    @SerializedName("EventEndTime")
    private String endDateTime;
    @SerializedName("EventLocationLat")
    private double eventLocationLat;
    @SerializedName("EventLocationLng")
    private double eventLocationLng;

    private Place placeDetails;

    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd kk:mm");

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

    public String getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(String startDateTime) {
        this.startDateTime = startDateTime;
    }

    public Calendar getStartingTime() {
        return startingTime;
    }

    public void setStartingTime(Calendar startingTime) {
        this.startingTime = startingTime;
        String date = df.format(startingTime.getTime());
        setStartDateTime(date);
    }

    public Calendar getEndingTime() {
        return endingTime;
    }

    public void setEndingTime(Calendar endingTime) {
        this.endingTime = endingTime;
        String date = df.format(endingTime.getTime());
        setEndDateTime(date);
    }

    public String getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(String endDateTime) {
        this.endDateTime = endDateTime;
    }

    public Place getPlaceDetails() {
        return placeDetails;
    }

    public void setPlaceDetails(Place placeDetails) {
        this.placeDetails = placeDetails;
    }




}
