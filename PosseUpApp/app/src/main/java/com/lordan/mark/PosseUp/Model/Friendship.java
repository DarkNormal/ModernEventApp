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

    public Friendship(String from, String to, boolean accepted){
        FromUsername = from;
        ToUsername = to;
        HasAccepted = accepted;
    }
}
