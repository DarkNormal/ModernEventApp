package com.lordan.mark.PosseUp.Model;

import com.google.gson.annotations.SerializedName;
/**
 * Created by Mark on 10/1/2015.
 */
public class User {



    @SerializedName("UserID")
    private int userID;

    @SerializedName("Email")
    private String email;

    @SerializedName("Password")
    private String password;

    @SerializedName("Username")
    private String username;

    @SerializedName("Verified")
    private boolean verified;

    @SerializedName("EmailOrUsername")
    private String emailOrUsername;

    private boolean isFriend;

    public User(){

    }
    public User(String email, String password, String username){
        userID = 99;
        String name = "mark";
        this.email = email;
        this.password = password;
        this.username = username;
    }
    public User(String username, boolean isFriend){
        this.username = username;
        this.isFriend = isFriend;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isFriend() {
        return isFriend;
    }

    public void setFriend(boolean friend) {
        isFriend = friend;
    }

}
