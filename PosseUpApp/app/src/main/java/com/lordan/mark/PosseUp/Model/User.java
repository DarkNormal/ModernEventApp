package com.lordan.mark.PosseUp.Model;

import com.google.gson.annotations.SerializedName;
/**
 * Created by Mark on 10/1/2015.
 */
public class User {

    @SerializedName("UserID")
    private int userID;

    @SerializedName("Name")
    private String name;

    @SerializedName("Email")
    private String email;

    @SerializedName("Password")
    private String password;

    @SerializedName("Username")
    private String username;

//    @SerializedName("Verified")
//    private boolean verified;
//
//    @SerializedName("EmailOrUsername")
//    private String emailOrUsername;

    public User(){

    }
    public User(String email, String password, String username){
        userID = 99;
        name = "mark";
        this.email = email;
        this.password = password;
        this.username = username;
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

//    public boolean isVerified() {
//        return verified;
//    }
//
//    public void setIsRegistered(boolean isRegistered) {
//        this.verified = isRegistered;
//    }
//
//
//    public String getEmailOrUsername() {
//        return emailOrUsername;
//    }
//
//    public void setEmailOrUsername(String emailOrUsername) {
//        this.emailOrUsername = emailOrUsername;
//    }
}