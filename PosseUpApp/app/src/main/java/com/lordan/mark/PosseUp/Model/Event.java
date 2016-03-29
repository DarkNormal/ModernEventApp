package com.lordan.mark.PosseUp.Model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.location.places.Place;
import com.google.gson.annotations.SerializedName;


import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Mark on 10/28/2015
 */
public class Event extends BaseObservable implements Parcelable{

    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private final SimpleDateFormat fancyFormatter = new SimpleDateFormat("E, MMM d, h:mm aa");


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
    @SerializedName("EventEndTime")
    private String endDateTime;

    private Calendar startingCal;

    private Calendar endingCal;





    @SerializedName("EventAttendees")
    private ArrayList<User> attendees;



    @SerializedName("EventVenue")
    private PlaceVenue placeDetails;

    public Event(Parcel in) {
        this.eventName = in.readString();
        this.eventDesc = in.readString();
        this.eventID = in.readInt();
        this.hostEmail = in.readString();
        this.placeDetails = in.readParcelable(PlaceVenue.class.getClassLoader());
    }
    public Event(String name, String visibility, String email, String eventDesc){
        this.eventName = name;
        this.eventVisibility = visibility;
        this.hostEmail = email;
        this.eventDesc = eventDesc;

    }
    public Event(int id, String eventTitle, String eventDescription, String eventHost) {
        this.eventID = id;

        this.eventName = eventTitle;
        this.eventDesc = eventDescription;
        this.hostEmail = eventHost;
    }
    public Event(int id, String eventTitle, String eventDescription, String eventHost, PlaceVenue venue) {
        this.eventID = id;
        this.placeDetails = venue;
        this.eventName = eventTitle;
        this.eventDesc = eventDescription;
        this.hostEmail = eventHost;
    }
    public Event(int id, String eventTitle, String eventDescription, String eventHost, String eventStartTime, String eventEndTime, PlaceVenue venue) {
        this.eventID = id;
        this.placeDetails = venue;
        this.eventName = eventTitle;
        this.eventDesc = eventDescription;
        this.startDateTime = eventStartTime;
        this.endDateTime = eventEndTime;
        this.hostEmail = eventHost;
    }

    public Event() {

    }

    public PlaceVenue getPlaceDetails() {
        return placeDetails;
    }

    public void setPlaceDetails(Place placeDetails) {
        this.placeDetails = new PlaceVenue(placeDetails.getName().toString(), placeDetails.getAddress().toString(), placeDetails.getLatLng(),
                placeDetails.getPlaceTypes(), placeDetails.getRating());
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

    private String getHostEmail() {
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


    public String getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(Calendar startDateTime) {
        this.startDateTime = formatter.format(startDateTime.getTime());
    }
    public String getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(Calendar endDateTime) {
        this.endDateTime = formatter.format(endDateTime.getTime());
    }

    @Bindable
    public String getStartingCal() {
        return fancyFormatter.format(startingCal.getTime());
    }

    public void setStartTime(String time){
        this.startDateTime = time;
    }

    public void setStartingTime() {
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(formatter.parse(startDateTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        notifyPropertyChanged(BR.startingCal);
        this.startingCal = cal;
    }
    public void setStartingCal(Calendar cal) {
        this.startingCal = cal;
    }
    public Calendar getTime(){
        if(startingCal == null){
            try {
                startingCal = Calendar.getInstance();
                startingCal.setTime(formatter.parse(startDateTime));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return startingCal;
    }

    @Bindable
    public String getEndingCal() {
        return fancyFormatter.format(endingCal.getTime());
    }

    public void setEndingTime() {
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(formatter.parse(endDateTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        notifyPropertyChanged(BR.endingCal);
        this.endingCal = cal;
    }
    public void setEndingCal(Calendar cal){
        this.endingCal = cal;
    }

    public ArrayList<User> getAttendees() {
        return this.attendees;
    }

    public void setAttendees(JSONArray attendees) {
        this.attendees.clear();
        for (int i = 0; i < attendees.length(); i++) {
            try {
                this.attendees.add(new User(attendees.getJSONObject(i).getString("Username"),attendees.getJSONObject(i).getInt("Id")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    public void setPlaceDetails(PlaceVenue placeDetails) {
        this.placeDetails = placeDetails;
    }

    @Override
    public String toString(){
        return getEventName() + " " + getEventDesc() + " " + getHostEmail();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(eventName);
        dest.writeString(eventDesc);
        dest.writeInt(eventID);
        dest.writeString(hostEmail);
        dest.writeParcelable(placeDetails, flags );

    }
    public static final Parcelable.Creator<Event> CREATOR = new Parcelable.Creator<Event>(){

        @Override
        public Event createFromParcel(Parcel source) {
            return new Event(source);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };
}
