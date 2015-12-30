package org.shingo.shingoeventsapp;

import android.app.Activity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.shingo.shingoeventsapp.attendees.Attendees;

/**
 * A fragment representing a single Attendee detail screen.
 * This fragment is either contained in a {@link AttendeeListActivity}
 * in two-pane mode (on tablets) or a {@link AttendeeDetailActivity}
 * on handsets.
 */
public class AttendeeDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private Attendees.Attendee mAttendee;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AttendeeDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mAttendee = Attendees.ATTENDEE_MAP.get(getArguments().getString(ARG_ITEM_ID));

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mAttendee.toString());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_attendee_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mAttendee != null) {
            LinearLayout linearLayout = new LinearLayout(getContext());
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            TextView email = new TextView(getContext());
            email.setText("Email: " + mAttendee.email);
            TextView firstName = new TextView(getContext());
            firstName.setText("First Name: " + mAttendee.firstName);
            TextView lastName = new TextView(getContext());
            lastName.setText("Last Name: " + mAttendee.lastName);
            linearLayout.addView(email, params);
            linearLayout.addView(firstName, params);
            linearLayout.addView(lastName, params);
            NestedScrollView scroll = (NestedScrollView) rootView.findViewById(R.id.attendee_detail_container);
            scroll.addView(linearLayout);
        }

        return rootView;
    }
}
