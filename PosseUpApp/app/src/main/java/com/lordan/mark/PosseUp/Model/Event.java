package com.lordan.mark.PosseUp.Model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.location.places.Place;
import com.google.gson.annotations.SerializedName;
import com.lordan.mark.PosseUp.BR;


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
    private final SimpleDateFormat allDayFancyFormatter = new SimpleDateFormat("E, MMM d, 'All Day'");


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

    @SerializedName("EventImageURL")
    private String eventImageURL;

    @SerializedName("EventImage")
    private String eventImage;


    @SerializedName("EventStartTime")
    private String startDateTime;
    @SerializedName("EventEndTime")
    private String endDateTime;



    @SerializedName("EventAllDay")
    private boolean mAllDay;

    private transient Calendar startingCal;
    private transient Calendar endingCal;



    private String lastChatMessage;





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
    public Event(int id, String eventTitle, String eventDescription, String eventVisibility, boolean allDay, Place venue) {
        this.eventID = id;
        if(venue != null) {
            setPlaceDetails(venue);
        }
        this.eventName = eventTitle;
        this.eventDesc = eventDescription;
        this.eventVisibility = eventVisibility;
        this.mAllDay = allDay;
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
    public void setPlaceDetails(PlaceVenue placeDetails) {
        this.placeDetails = placeDetails;
    }


    public int getEventID() {
        return eventID;
    }

    @Bindable
    public String getEventName() {
        return eventName;
    }

    public String getHostEmail() {
        return hostEmail;
    }

    public void setHostEmail(String hostEmail) {
        this.hostEmail = hostEmail;
    }

    public String getEventDesc() {
        return eventDesc;
    }

    public void setStartDateTime(Calendar startDateTime) {
        this.startDateTime = formatter.format(startDateTime.getTime());
    }
    public void setEndDateTime(Calendar endDateTime) {
        this.endDateTime = formatter.format(endDateTime.getTime());
    }

    public boolean isAllDay() {
        return mAllDay;
    }

    public void setAllDay(boolean mAllDay) {
        this.mAllDay = mAllDay;
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

    //Return the event's start time as a formatted string
    @Bindable
    public String getStartingCal() {
        return fancyFormatter.format(startingCal.getTime());
    }
    @Bindable
    public String getAllDayStartingCal() {
        return allDayFancyFormatter.format(startingCal.getTime());
    }
    //Return the event's start time as a Calendar object
    //Create it from the String recieved from the Server if it's null
    public Calendar getStartTimeCalendar(){
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
    //set the calendar but also set the String time to a pre-formatted version accepted by Server
    public void setStartingCal(Calendar cal) {
        this.startingCal = cal;
        setStartDateTime(this.startingCal);
    }

    //Return the event's end time as a Calendar object
    //Create it from the String recieved from the Server if it's null
    public Calendar getEndTimeCalendar(){
        if(endingCal == null){
            try {
                endingCal = Calendar.getInstance();
                endingCal.setTime(formatter.parse(endDateTime));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return endingCal;
    }

    @Bindable
    public String getEndingCal() {
        return fancyFormatter.format(endingCal.getTime());
    }
    public void setEndingCal(Calendar cal){
        this.endingCal = cal;
        setEndDateTime(this.endingCal);
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

    public String getEventImageURL() {
        return eventImageURL;
    }
    public void setEventImageURL(String eventImageURL) {
        this.eventImageURL = eventImageURL;
    }

    public String getEventImage() {
        return eventImage;
    }

    public void setEventImage(String eventImage) {
        this.eventImage = eventImage;
    }

    public String getLastChatMessage() {
        return lastChatMessage;
    }

    public void setLastChatMessage(String lastChatMessage) {
        this.lastChatMessage = lastChatMessage;
    }
}
