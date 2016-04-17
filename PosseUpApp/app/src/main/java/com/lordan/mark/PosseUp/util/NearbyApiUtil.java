package com.lordan.mark.PosseUp.util;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.Strategy;
import com.google.gson.Gson;
import com.lordan.mark.PosseUp.Model.User;

import java.nio.charset.Charset;

/**
 * Created by Mark on 03/04/2016.
 */
public class NearbyApiUtil {

    private static final String TAG = NearbyApiUtil.class.getSimpleName();

    private static final String TYPE_NERD = "nerd";

    public static final String TYPE_BEACON = "beacon";

    private NearbyApiUtil() {
        // static class
    }

    // The Strategy for our Nearby messages.  The defaults are to allow messages to be exchanged
    // over any distance for 5 minutes. Other options are listed here:
    // https://developers.google.com/android/reference/com/google/android/gms/nearby/messages/Strategy
    public static final Strategy MESSAGE_STRATEGY = new Strategy.Builder()
            .setTtlSeconds(Strategy.TTL_SECONDS_DEFAULT)
            .setDiscoveryMode(Strategy.DISCOVERY_MODE_DEFAULT)
            .build();

    public static Message newNearbyMessage(Context context, User payload) {
        Gson gson = new Gson();
        Wrapper wrapper = new Wrapper(context, payload);
        byte[] bytes = gson.toJson(wrapper).getBytes(Charset.forName("UTF-8"));
        return new Message(bytes, TYPE_NERD);
    }

    public static User parseNearbyMessage(Message nearbyMessage) {
        Gson gson = new Gson();
        String string = new String(nearbyMessage.getContent()).trim();
        Wrapper message = gson
                .fromJson(new String(string.getBytes(Charset.forName("UTF-8"))), Wrapper.class);
        if (message == null) {
            Log.w(TAG, "Unable to parse Nearby Message");
            return null;
        } else {
            return message.payload;
        }
    }

    // NearbyApiUtil.Wrapper is a convenience class for wrapping a payload with a Google Play
    // Services instance identifier. This allows the Nearby API to distinguish identical payloads
    // that originate from different devices.
    private static class Wrapper {

        private final String id;

        public final User payload;

        public Wrapper(Context context, User payload) {
            this.id = InstanceID.getInstance(context.getApplicationContext()).getId();
            this.payload = payload;
        }
    }
}
