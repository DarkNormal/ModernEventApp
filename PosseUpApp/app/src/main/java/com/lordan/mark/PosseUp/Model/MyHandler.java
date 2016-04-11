package com.lordan.mark.PosseUp.Model;

/**
 * Created by Mark on 27/01/2016
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import com.lordan.mark.PosseUp.R;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.lordan.mark.PosseUp.UI.MainActivityGroup.MainActivity;
import com.lordan.mark.PosseUp.UI.StartActivity;
import com.microsoft.windowsazure.notifications.NotificationsHandler;


public class MyHandler extends NotificationsHandler {
    private static final int NOTIFICATION_ID = 1;
    NotificationCompat.Builder builder;
    private Context ctx;

    static public StartActivity startActivity;

    @Override
    public void onReceive(Context context, Bundle bundle) {
        ctx = context;
        String nhMessage = bundle.getString("message");

        sendNotification(nhMessage);
        //mainActivity.ToastNotify(nhMessage);
    }

    private void sendNotification(String msg) {
        NotificationManager mNotificationManager = (NotificationManager)
                ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0,
                new Intent(ctx, MainActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(ctx)
                        .setSmallIcon(R.drawable.ic_people)
                        .setColor(ContextCompat.getColor(ctx, R.color.primaryThemeColor))
                        .setContentTitle("Posse Up")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
