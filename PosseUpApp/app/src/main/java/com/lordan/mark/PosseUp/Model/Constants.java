package com.lordan.mark.PosseUp.Model;

import java.text.SimpleDateFormat;

/**
 * Created by Mark on 10/4/2015
 */
public final class Constants {
    public static final String baseUrl = "http://possenetapiapp.azurewebsites.net/";
    public static final int TTL_IN_SECONDS = 3 * 60;

    public static final int REQUEST_RESOLVE_ERROR = 1001;
    // Keys to get and set the current subscription and publication tasks using SharedPreferences.
    public static final String KEY_SUBSCRIPTION_TASK = "subscription_task";
    public static final String KEY_PUBLICATION_TASK = "publication_task";

    // Tasks constants.
    public static final String TASK_SUBSCRIBE = "task_subscribe";
    public static final String TASK_UNSUBSCRIBE = "task_unsubscribe";
    public static final String TASK_PUBLISH = "task_publish";
    public static final String TASK_UNPUBLISH = "task_unpublish";
    public static final String TASK_NONE = "task_none";

    public static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    public static final SimpleDateFormat chatFormatter = new SimpleDateFormat("HH:mm");
    public static final SimpleDateFormat fancyFormatter = new SimpleDateFormat("E, MMM d, h:mm aa");
    public static final SimpleDateFormat allDayFancyFormatter = new SimpleDateFormat("E, MMM d, 'All Day'");
}
