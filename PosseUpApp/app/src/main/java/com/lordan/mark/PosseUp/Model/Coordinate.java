package com.lordan.mark.PosseUp.Model;

/**
 * Created by Mark on 15/12/2015.
 */
public class Coordinate {
    private double latitude, longitude;
    private String eventTitle;

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getEventHost() {
        return eventHost;
    }

    public void setEventHost(String eventHost) {
        this.eventHost = eventHost;
    }

    private String eventDescription;
    private String eventHost;

    public double getLatitude(){
        return latitude;
    }
    public void setLatitude(double coord){
        latitude = coord;
    }
    public double getLongitude(){
        return longitude;
    }
    public void setLongitude(double coord){
        longitude = coord;
    }
    public Coordinate(){}

    public Coordinate(double latitude, double longitude, String eventTitle, String eventDescription, String eventHost) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.eventTitle = eventTitle;
        this.eventDescription = eventDescription;
        this.eventHost = eventHost;
    }
}
