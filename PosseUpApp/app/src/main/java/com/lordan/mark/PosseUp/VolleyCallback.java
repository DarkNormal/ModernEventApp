package com.lordan.mark.PosseUp;

import com.android.volley.VolleyError;

import org.json.JSONObject;

/**
 * Created by Mark on 30/03/2016.
 */
public interface VolleyCallback{
    void onSuccess(JSONObject result);

    void onError(VolleyError error);
}