package com.lordan.mark.PosseUp.Model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.lordan.mark.PosseUp.BR;

import java.util.ArrayList;

/**
 * Created by Mark on 10/1/2015
 */
public class User  extends BaseObservable implements Parcelable{



    @SerializedName("UserID")
    private int userID;

    @SerializedName("Email")
    private String email;

    @SerializedName("Password")
    private String password;

    @SerializedName("Username")
    private String username;




    @SerializedName("Location")
    private String location;



    @SerializedName("Followers")
    private ArrayList<User> followers;

    @SerializedName("Following")
    private ArrayList<User> following;

    private boolean isFriend;

    public User(){
        followers = new ArrayList<>();

    }
    public User(Parcel in){
        this.userID = in.readInt();
        this.username = in.readString();
        followers = new ArrayList<>();
        in.readTypedList(followers, User.CREATOR);

    }
    public User(String email, String password, String username){
        userID = 99;
        this.email = email;
        this.password = password;
        this.username = username;
    }
    public User(String username){
        this.username = username;
    }
    public User(String username, int id){
        this.username = username;
        this.userID = id;
    }



    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Bindable
    public String getUsername() {
        return username;
    }


    public void setUsername(String username) {
        this.username = username;
        notifyPropertyChanged(BR.username);
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
    @Bindable
    public ArrayList<User> getFollowers() {
        return followers;
    }

    public void setFollowers(ArrayList<User> followers) {
        this.followers = followers;
        notifyPropertyChanged(BR.followers);

    }
    public void updateFollowers(boolean remove ,User u) {
        if(remove){
            this.followers.remove(u);
        }
        else{
            this.followers.add(u);
        }

        notifyPropertyChanged(BR.followers);

    }

    @Bindable
    public ArrayList<User> getFollowing() {
        return following;
    }

    public void setFollowing(ArrayList<User> following) {
        this.following = following;
        notifyPropertyChanged(BR.following);
    }

    public boolean isFriend() {
        return isFriend;
    }

    public void setFriend(boolean friend) {
        isFriend = friend;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(userID);
        dest.writeString(username);
        dest.writeTypedList(followers);

    }
    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>(){

        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
