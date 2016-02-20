package com.lordan.mark.PosseUp.Model;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlaceVenue{

   @SerializedName("LocationName")
   private String venueName;
   @SerializedName("LocationAddress")
   private String venueAddress;
   @SerializedName("LocationLat")
   private double venueLocationLat;
   @SerializedName("LocationLng")
   private double venueLocationLng;
   @SerializedName("LocationType")
   private List<Integer> venueType;
   @SerializedName("LocationRating")
   private double venueRating;

   public PlaceVenue(String venueName, String venueAddress, LatLng venueLocation, List<Integer> venueType, double venueRating) {
       this.venueName = venueName;
       this.venueAddress = venueAddress;
       this.venueLocationLat = venueLocation.latitude;
       this.venueLocationLng = venueLocation.longitude;
       this.venueType = venueType;
       this.venueRating = venueRating;
   }

   public double getVenueRating() {
       return venueRating;
   }

   public void setVenueRating(double venueRating) {
       this.venueRating = venueRating;
   }

   public String getVenueName() {
       return venueName;
   }

   public void setVenueName(String venueName) {
       this.venueName = venueName;
   }

   public String getVenueAddress() {
       return venueAddress;
   }

   public void setVenueAddress(String venueAddress) {
       this.venueAddress = venueAddress;
   }

   public LatLng getVenueLocation() {
       return new LatLng(venueLocationLat, venueLocationLng);
   }

   public void setVenueLocation(LatLng venueLocation) {
       this.venueLocationLat = venueLocation.latitude;
       this.venueLocationLng = venueLocation.longitude;
   }

   public List<Integer> getVenueType() {
       return venueType;
   }

   public void setVenueType(List<Integer> venueType) {
       this.venueType = venueType;
   }


}
