package com.lordan.mark.PosseUp.UI.EventDetailGroup;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lordan.mark.PosseUp.Model.Event;
import com.lordan.mark.PosseUp.Model.User;
import com.lordan.mark.PosseUp.R;

/**
 * A fragment representing a list of Items.
 * <p>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class UserFragment extends Fragment {

    private static final String EXTRA_USER_OBJECT = "UserFragment.user";
    private static final String EXTRA_VIEW_TYPE = "UserFragment.viewtype";

    private OnListFragmentInteractionListener mListener;
    private Event e;
    private User u;
    private String userType;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public UserFragment() {
    }
    public static UserFragment newInstance(User u, String viewType) {
        UserFragment fragment = new UserFragment();
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_USER_OBJECT, u);
        args.putString(EXTRA_VIEW_TYPE, viewType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            Bundle b = this.getArguments();
            e = b.getParcelable("Event");
            u = b.getParcelable(EXTRA_USER_OBJECT);
            if(u != null){
                userType = b.getString(EXTRA_VIEW_TYPE);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            if(e != null) {
                recyclerView.setAdapter(new UserAdapter(e.getAttendees(), mListener));
            }
            else {
                switch (userType) {
                    case "followers":
                        recyclerView.setAdapter(new UserAdapter(u.getFollowers(), mListener));
                        break;
                    case "following":
                        recyclerView.setAdapter(new UserAdapter(u.getFollowing(), mListener));
                        break;
                }
            }
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(User u);
    }
}
