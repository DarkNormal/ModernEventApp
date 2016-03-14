package com.lordan.mark.PosseUp.Model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Mark on 30/01/2016
 */
public class Friendship {

    @SerializedName("FromUsername")
    private String FromUsername;
    @SerializedName("ToUsername")
    private String ToUsername;
    @SerializedName("HasAccepted")
    private Boolean HasAccepted;

    public String getFromUsername() {
        return FromUsername;
    }

    public void setFromUsername(String fromUsername) {
        FromUsername = fromUsername;
    }

    public String getToUsername() {
        return ToUsername;
    }

    public void setToUsername(String toUsername) {
        ToUsername = toUsername;
    }

    public Boolean getHasAccepted() {
        return HasAccepted;
    }

    public void setHasAccepted(Boolean hasAccepted) {
        HasAccepted = hasAccepted;
    }

    public Friendship(String from, String to, boolean accepted){
        FromUsername = from;
        ToUsername = to;
        HasAccepted = accepted;
    }
}
