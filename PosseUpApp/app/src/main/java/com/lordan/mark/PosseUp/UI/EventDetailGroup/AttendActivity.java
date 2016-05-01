package com.lordan.mark.PosseUp.UI.EventDetailGroup;

import android.content.IntentSender;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.NearbyMessagesStatusCodes;
import com.google.android.gms.nearby.messages.PublishCallback;
import com.google.android.gms.nearby.messages.PublishOptions;
import com.lordan.mark.PosseUp.DataOperations.AzureService;
import com.lordan.mark.PosseUp.DataOperations.Settings;
import com.lordan.mark.PosseUp.Model.Constants;
import com.lordan.mark.PosseUp.Model.DeviceMessage;
import com.lordan.mark.PosseUp.Model.User;
import com.lordan.mark.PosseUp.NearbyPublishInterface;
import com.lordan.mark.PosseUp.R;
import com.lordan.mark.PosseUp.util.NearbyApiUtil;

public class AttendActivity extends AppCompatActivity
implements NearbyPublishInterface,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private GoogleApiClient mGoogleApiClient;
    private NearbyPublishInterface nearbyInterface;
    private String currentUser;
    private static final String TAG = "Attend Activity";
    // Tracks if we are currently resolving an error related to Nearby permissions. Used to avoid
    // duplicate Nearby permission dialogs if the user initiates both subscription and publication
    // actions without having opted into Nearby.
    private boolean mResolvingNearbyPermissionError = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attend);
        Bundle bundle = getIntent().getExtras();
        currentUser = bundle.getString("currentUsername");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Nearby.MESSAGES_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        nearbyInterface = this;
        AppCompatButton broadcastButton = (AppCompatButton) findViewById(R.id.broadcast);
        broadcastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Settings.isPublishing(getApplicationContext()) || Settings.isSubscribing(getApplicationContext())) {
                    nearbyInterface.unpublish();
                } else {
                    nearbyInterface.publish();
                }
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();

        AzureService az = new AzureService();
        Message mDeviceMessage = DeviceMessage.newNearbyMessage(InstanceID.getInstance(this).getId(), az.getCurrentUsername(this));
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        unpublish();
        mGoogleApiClient.disconnect();

        // sometimes the app dies before the callbacks complete, so let's force the
        // unpublish/unsubscribe state so the FAB isn't spinning when the app starts back up.
        Settings.setPublishing(this, false);
        Settings.setSubscribing(this, false);

        super.onStop();
    }


    @Override
    public void publish() {
        if (!mGoogleApiClient.isConnected()) {
            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        } else {
            // finally, the part that actually uses the API we're demoing...
            Message message = NearbyApiUtil.newNearbyMessage(this, new User(currentUser));

            PublishOptions.Builder builder = new PublishOptions.Builder();
            builder.setStrategy(NearbyApiUtil.MESSAGE_STRATEGY);
            builder.setCallback(new PublishCallback() {
                @Override
                public void onExpired() {
                    Log.i(TAG, "PublishCallback.onExpired(): No longer publishing");
                    Settings.setPublishing(getApplicationContext(), false);
                }
            });
            PublishOptions options = builder.build();

            PendingResult<Status> result = Nearby.Messages
                    .publish(mGoogleApiClient, message, options);

            result.setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    if (status.isSuccess()) {
                        // we're done publishing!
                        Log.i(TAG, "Nearby publish successful");
                        Settings.setPublishing(getApplicationContext(), true);
                    } else {
                        Log.w(TAG, "Nearby publish unsuccessful");
                        Settings.setPublishing(getApplicationContext(), false);
                        handleUnsuccessfulNearbyResult(status);
                    }
                }
            });
        }
    }

    @Override
    public void unpublish() {
        Log.d(TAG, "unpublish");

        // Cannot proceed without a connected GoogleApiClient. Reconnect and execute the pending
        // task in onConnected().
        if (!mGoogleApiClient.isConnected()) {
            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        } else {
            Message message = NearbyApiUtil.newNearbyMessage(this, new User(currentUser));
            PendingResult<Status> result = Nearby.Messages.unpublish(mGoogleApiClient, message);
            result.setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    if (status.isSuccess()) {
                        Log.i(TAG, "Nearby unpublish successful");
                        Settings.setPublishing(getApplicationContext(), false);
                    } else {
                        Log.w(TAG, "Nearby unpublish unsuccessful");
                        Settings.setPublishing(getApplicationContext(), true);
                        handleUnsuccessfulNearbyResult(status);
                    }
                }
            });
        }
    }
    // Handles errors generated when performing a subscription or publication action. Uses
    // Status#startResolutionForResult to display an opt-in dialog to handle the case
    // where a device is not opted into using Nearby.
    private void handleUnsuccessfulNearbyResult(Status status) {
        Log.e(TAG, "processing error, status = " + status);
        if (status.getStatusCode() == NearbyMessagesStatusCodes.APP_NOT_OPTED_IN) {
            if (!mResolvingNearbyPermissionError) {
                try {
                    mResolvingNearbyPermissionError = true;
                    status.startResolutionForResult(this, Constants.REQUEST_RESOLVE_ERROR);

                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (status.getStatusCode() == ConnectionResult.NETWORK_ERROR) {
                Toast.makeText(this,
                        "No connectivity, cannot proceed. Fix in 'Settings' and try again.",
                        Toast.LENGTH_LONG).show();
            } else {
                // To keep things simple, pop a toast for all other error messages.
                Toast.makeText(this,
                        "Unsuccessful: " + status.getStatusMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "Google API Client connected");

        if (Settings.isPublishing(this)) {
            publish();
        } else {
            unpublish();
        }

    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "GoogleApiClient connection suspended: "
                + connectionSuspendedCauseToString(cause));
    }

    private static String connectionSuspendedCauseToString(int cause) {
        switch (cause) {
            case CAUSE_NETWORK_LOST:
                return "CAUSE_NETWORK_LOST";
            case CAUSE_SERVICE_DISCONNECTED:
                return "CAUSE_SERVICE_DISCONNECTED";
            default:
                return "CAUSE_UNKNOWN: " + cause;
        }
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "connection to GoogleApiClient failed");
    }
}
