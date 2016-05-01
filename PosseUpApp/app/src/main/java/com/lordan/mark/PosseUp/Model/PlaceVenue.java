package com.lordan.mark.PosseUp.Model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;
import com.lordan.mark.PosseUp.BR;


import java.util.List;


public class PlaceVenue extends BaseObservable implements Parcelable{

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

    public PlaceVenue(Parcel in) {

        this.venueName = in.readString();
        this.venueAddress = in.readString();
        this.venueLocationLat = in.readDouble();
        this.venueLocationLng = in.readDouble();
        this.venueRating = in.readDouble();
    }

   public PlaceVenue(String venueName, String venueAddress, LatLng venueLocation, List<Integer> venueType, double venueRating) {
       this.venueName = venueName;
       this.venueAddress = venueAddress;
       this.venueLocationLat = venueLocation.latitude;
       this.venueLocationLng = venueLocation.longitude;
       this.venueType = venueType;
       this.venueRating = venueRating;
   }

    @Bindable
   public String getVenueName() {
       return venueName;
   }


    @Bindable
   public String getVenueAddress() {
       return venueAddress;
   }

    public LatLng getVenueLocation() {
       return new LatLng(venueLocationLat, venueLocationLng);
   }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(venueName);
        dest.writeString(venueAddress);
        dest.writeDouble(venueLocationLat);
        dest.writeDouble(venueLocationLng);
        dest.writeDouble(venueRating);

    }
    public static final Parcelable.Creator<PlaceVenue> CREATOR = new Parcelable.Creator<PlaceVenue>(){

        @Override
        public PlaceVenue createFromParcel(Parcel source) {
            return new PlaceVenue(source);
        }

        @Override
        public PlaceVenue[] newArray(int size) {
            return new PlaceVenue[size];
        }
    };



}
