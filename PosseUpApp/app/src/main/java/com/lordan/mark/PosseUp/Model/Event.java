package com.lordan.mark.PosseUp.Model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.google.android.gms.location.places.Place;
import com.google.gson.annotations.SerializedName;
import com.lordan.mark.PosseUp.BR;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Mark on 10/28/2015.
 */
public class Event extends BaseObservable{

    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private SimpleDateFormat fancyFormatter = new SimpleDateFormat("E, MMM d, h:mm aa");


    @SerializedName("EventID")
    private int eventID;
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

    private Place placeDetails;;

    public Event(String name, String visibility, String email, String eventDesc, double eventLocationLat, double eventLocationLng){
        this.eventName = name;
        this.eventVisibility = visibility;
        this.hostEmail = email;
        this.eventDesc = eventDesc;
        this.eventLocationLat = eventLocationLat;
        this.eventLocationLng = eventLocationLng;
    }
    public Event(int id, double latitude, double longitude, String eventTitle, String eventDescription, String eventHost) {
        this.eventID = id;
        this.eventLocationLat = latitude;
        this.eventLocationLng = longitude;
        this.eventName = eventTitle;
        this.eventDesc = eventDescription;
        this.hostEmail = eventHost;
    }

    public Event() {

    }

    public int getEventID() {
        return eventID;
    }

    public void setEventID(int eventID) {
        this.eventID = eventID;
    }

    @Bindable
    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        notifyPropertyChanged(BR.eventName);
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

    @Bindable
    public String getStartingTime() {
        return fancyFormatter.format(startingTime.getTime());
    }


    public void setStartingTime() {
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(formatter.parse(startDateTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        notifyPropertyChanged(BR.startingTime);
        this.startingTime = cal;
    }
    public void setStartingTime(Calendar cal) {
        this.startingTime = cal;
    }

    @Bindable
    public String getEndingTime() {
        return fancyFormatter.format(endingTime.getTime());
    }

    public void setEndingTime() {
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(formatter.parse(endDateTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        notifyPropertyChanged(BR.endingTime);
        this.endingTime = cal;
    }
    public void setEndingTime(Calendar cal){
        this.endingTime = cal;
    }

    @Override
    public String toString(){
        return getEventName() + " " + getEventDesc() + " " + getHostEmail();
    }


}
