package com.lordan.mark.PosseUp.Model;

import com.google.gson.annotations.SerializedName;
/**
 * Created by Mark on 10/1/2015.
 */
public class User {
    @SerializedName("fullname")
    private String fullname;

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    private String username;

    @SerializedName("isRegistered")
    private boolean isRegistered;

    public User(){

    }
    public User(String email, String password, String fullname){
        this.fullname = fullname;
        this.email = email;
        this.password = password;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
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

    public boolean isRegistered() {
        return isRegistered;
    }

    public void setIsRegistered(boolean isRegistered) {
        this.isRegistered = isRegistered;
    }
}
