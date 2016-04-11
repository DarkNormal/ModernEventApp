package com.lordan.mark.PosseUp.UI.EventDetailGroup;

import android.content.Context;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.nearby.messages.NearbyMessagesStatusCodes;
import com.google.android.gms.nearby.messages.SubscribeCallback;
import com.google.android.gms.nearby.messages.SubscribeOptions;
import com.lordan.mark.PosseUp.BasicAdapter;
import com.lordan.mark.PosseUp.DataOperations.Settings;
import com.lordan.mark.PosseUp.Model.Constants;
import com.lordan.mark.PosseUp.Model.DeviceMessage;
import com.lordan.mark.PosseUp.Model.User;
import com.lordan.mark.PosseUp.NearbySubscribeInterface;
import com.lordan.mark.PosseUp.R;
import com.lordan.mark.PosseUp.util.NearbyApiUtil;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Activities that contain this fragment must implement the
 * {@link NearbySubscribeFragment.OnNearbyFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NearbySubscribeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NearbySubscribeFragment extends Fragment implements
        NearbySubscribeInterface,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{
    /**
     * A {@link MessageListener} for processing messages from nearby devices.
     */
    private MessageListener mMessageListener;
    private final ArrayList<String> mNearbyDevicesArrayList = new ArrayList<>();
    private RecyclerView.Adapter mNearbyDevicesArrayAdapter;
    /**
     * Tracks if we are currently resolving an error related to Nearby permissions. Used to avoid
     * duplicate Nearby permission dialogs if the user initiates both subscription and publication
     * actions without having opted into Nearby.
     */
    private boolean mResolvingNearbyPermissionError = false;
    private NearbySubscribeInterface nearbyInterface;
    private static final String TAG = "NearbySubscribeFragment";
    private OnNearbyFragmentInteractionListener mListener;
    private static final String ARG_SECTION_NUMBER = "section_number";
    private GoogleApiClient mGoogleApiClient;
    private LinearLayoutManager mLayoutManager;

    @Bind(R.id.start_attendance_tracking)
    AppCompatButton nearbyButton;

    @Bind(R.id.nearby_progress_bar)
    ProgressBar nearbyProgressBar;
    @Bind(R.id.add_to_confirmed_list)
    AppCompatButton confirmButton;

    public NearbySubscribeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Nearby.MESSAGES_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    public static NearbySubscribeFragment newInstance(int sectionNumber) {
        NearbySubscribeFragment fragment = new NearbySubscribeFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_attendance, container, false);
        ButterKnife.bind(this, rootView);
        final RecyclerView nearbyDevicesListView = (RecyclerView) rootView.findViewById(R.id.nearby_devices_list_view);
        mLayoutManager = new LinearLayoutManager(getContext());
        nearbyDevicesListView.setLayoutManager(mLayoutManager);
        mNearbyDevicesArrayAdapter = new BasicAdapter(mNearbyDevicesArrayList);
        nearbyDevicesListView.setAdapter(mNearbyDevicesArrayAdapter);
        initializeMessageListener();
        return rootView;
    }

    @OnClick(R.id.start_attendance_tracking)
    public void beginNearby(View v){
        if (Settings.isPublishing(getActivity()) || Settings.isSubscribing(getActivity())) {
            nearbyInterface.unsubscribe();
            nearbyButton.setText(getResources().getString(R.string.start_nearby_scan));
            nearbyProgressBar.setVisibility(View.INVISIBLE);
        } else {
            nearbyInterface.subscribe();
            nearbyButton.setText(getResources().getString(R.string.stop_nearby_scan));
            nearbyProgressBar.setVisibility(View.VISIBLE);
        }
    }
    @OnClick(R.id.add_to_confirmed_list)
    public void confirmUsers(View v){
        mListener.onFragmentInteraction(mNearbyDevicesArrayList);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        nearbyInterface = this;
        if (context instanceof OnNearbyFragmentInteractionListener) {
            mListener = (OnNearbyFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    @Override
    public void onStart(){
        super.onStart();
       if(!mGoogleApiClient.isConnected()){
           mGoogleApiClient.connect();
       }
    }
    @Override
    public void onStop(){
        super.onStop();
        unsubscribe();
        if(mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();
        }
        Settings.setSubscribing(getContext(), false);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "GoogleApiClient connected");
        if (Settings.isSubscribing(getContext())) {
            subscribe();
        } else {
            unsubscribe();
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

    /**
     * Subscribes to messages from nearby devices. If not successful, attempts to resolve any error
     * related to Nearby permissions by displaying an opt-in dialog. Registers a callback which
     * updates state when the subscription expires.
     */
    @Override
    public void subscribe() {
        Log.i(TAG, "trying to subscribe");
        Settings.setSubscribing(getContext(), true);
        // Cannot proceed without a connected GoogleApiClient. Reconnect and execute the pending
        // task in onConnected().
        if (!mGoogleApiClient.isConnected()) {
            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        } else {
            SubscribeOptions options = new SubscribeOptions.Builder()
                    .setStrategy(NearbyApiUtil.MESSAGE_STRATEGY)
                    .setCallback(new SubscribeCallback() {
                        @Override
                        public void onExpired() {
                            super.onExpired();
                            Log.i(TAG, "no longer subscribing");
                            Settings.setSubscribing(getActivity(), false);
                        }
                    }).build();

            Nearby.Messages.subscribe(mGoogleApiClient, mMessageListener, options)
                    .setResultCallback(new ResultCallback<Status>() {

                        @Override
                        public void onResult(Status status) {
                            if (status.isSuccess()) {
                                Log.i(TAG, "subscribed successfully");
                                Settings.setSubscribing(getActivity(), true);
                            } else {
                                Log.i(TAG, "could not subscribe");
                                Settings.setSubscribing(getActivity(), false);
                                handleUnsuccessfulNearbyResult(status);
                            }
                        }
                    });
        }
    }

    /**
     * Ends the subscription to messages from nearby devices. If successful, resets state. If not
     * successful, attempts to resolve any error related to Nearby permissions by
     * displaying an opt-in dialog.
     */
    public void unsubscribe() {
        Log.i(TAG, "trying to unsubscribe");
        // Cannot proceed without a connected GoogleApiClient. Reconnect and execute the pending
        // task in onConnected().
        if (!mGoogleApiClient.isConnected()) {
            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        } else {
            Nearby.Messages.unsubscribe(mGoogleApiClient, mMessageListener)
                    .setResultCallback(new ResultCallback<Status>() {

                        @Override
                        public void onResult(Status status) {
                            if (status.isSuccess()) {
                                Log.i(TAG, "unsubscribed successfully");
                                Settings.setSubscribing(getActivity(), false);
                            } else {
                                Log.i(TAG, "could not unsubscribe");
                                Settings.setSubscribing(getActivity(), true);
                                handleUnsuccessfulNearbyResult(status);
                            }
                        }
                    });
        }
    }
    /**
     * Handles errors generated when performing a subscription or publication action. Uses
     * {@link Status#startResolutionForResult} to display an opt-in dialog to handle the case
     * where a device is not opted into using Nearby.
     */
    private void handleUnsuccessfulNearbyResult(Status status) {
        Log.i(TAG, "processing error, status = " + status);
        if (status.getStatusCode() == NearbyMessagesStatusCodes.APP_NOT_OPTED_IN) {
            if (!mResolvingNearbyPermissionError) {
                try {
                    mResolvingNearbyPermissionError = true;
                    status.startResolutionForResult(getActivity(),
                            Constants.REQUEST_RESOLVE_ERROR);

                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (status.getStatusCode() == ConnectionResult.NETWORK_ERROR) {
                Toast.makeText(getActivity(),
                        "No connectivity, cannot proceed. Fix in 'Settings' and try again.",
                        Toast.LENGTH_LONG).show();
            } else {
                // To keep things simple, pop a toast for all other error messages.
                Toast.makeText(getActivity(), "Unsuccessful: " +
                        status.getStatusMessage(), Toast.LENGTH_LONG).show();
            }

        }
    }
    private void initializeMessageListener() {
        mMessageListener = new MessageListener() {
            @Override
            public void onFound(final Message message) {
                final User user = NearbyApiUtil.parseNearbyMessage(message);
                Log.i(TAG, user.getUsername());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(mNearbyDevicesArrayList.size() > 0) {
                            for (int i = 0; i < mNearbyDevicesArrayList.size(); i++) {

                                //check if the attendee is already present in the current list
                                if (!mNearbyDevicesArrayList.get(i).equals(user.getUsername())) {
                                    mNearbyDevicesArrayList.add(user.getUsername());
                                    mNearbyDevicesArrayAdapter.notifyItemInserted(mNearbyDevicesArrayList.size()-1);
                                }
                                else{
                                    //User is already in the list
                                }
                            }
                        }
                        else{
                            mNearbyDevicesArrayList.add(user.getUsername());
                            confirmButton.setVisibility(View.VISIBLE);
                            mNearbyDevicesArrayAdapter.notifyItemInserted(mNearbyDevicesArrayList.size()-1);

                        }

                    }
                });
            }

            @Override
            public void onLost(final Message message) {
                /** Called when a message is no longer detectable nearby.
                 *  Ignored here as we want all detected attendees to remain in the list
                 *  regardless of whether they move in/out of range
                 */

            }
        };
    }

    public interface OnNearbyFragmentInteractionListener {
        void onFragmentInteraction(ArrayList<String> usersList);
    }
    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
