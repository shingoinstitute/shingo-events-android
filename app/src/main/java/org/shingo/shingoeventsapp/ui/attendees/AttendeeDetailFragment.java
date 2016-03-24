package org.shingo.shingoeventsapp.ui.attendees;

import android.app.Activity;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.shingo.shingoeventsapp.R;
import org.shingo.shingoeventsapp.api.OnTaskComplete;
import org.shingo.shingoeventsapp.api.RestApi;
import org.shingo.shingoeventsapp.data.attendees.Attendees;
import org.shingo.shingoeventsapp.data.attendees.ConnectionRequestTask;

/**
 * A fragment representing a single Attendee detail screen.
 * This fragment is either contained in a {@link AttendeeListActivity}
 * in two-pane mode (on tablets) or a {@link AttendeeDetailActivity}
 * on handsets.
 */
public class AttendeeDetailFragment extends Fragment implements OnTaskComplete {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The Attendee this fragment is presenting.
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
            mAttendee = Attendees.ATTENDEE_MAP.get(getArguments().getString(ARG_ITEM_ID));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Activity activity = this.getActivity();
        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
        if (appBarLayout != null) {
            appBarLayout.setTitle(mAttendee.toString());
        }

        View rootView = inflater.inflate(R.layout.fragment_attendee_detail, container, false);
        View parent = container.getRootView();
        System.out.println("fragment_attendee_detail parent view: " + parent.getId());
        if (mAttendee != null) {
            TextView emailView = (TextView) rootView.findViewById(R.id.attendee_email);
            TextView nameView = (TextView) rootView.findViewById(R.id.attendee_display_name);
            TextView titleView = (TextView) rootView.findViewById(R.id.attendee_title);
            TextView companyView = (TextView) rootView.findViewById(R.id.attendee_company);
            emailView.setText(mAttendee.email);
            nameView.setText(mAttendee.toString());
            titleView.setText(mAttendee.title);
            companyView.setText(mAttendee.company);
            FloatingActionButton fab = (FloatingActionButton) parent.findViewById(R.id.fab);
            System.out.println("mAttendee.status " + mAttendee.status);
            switch(mAttendee.status){
                case "approved":
                    //TODO: send message
                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Snackbar.make(view, "Opening chat...", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    });
                    break;
                case "waiting":
                    fab.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_info_details));
                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Snackbar.make(view, "Request pending...", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    });
                    break;
                default:
                    fab.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_send));
                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            sendRequest();
                            Snackbar.make(view, "Request sent!", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    });
            }
        }

        return rootView;
    }

    private void sendRequest(){
        RestApi api = new RestApi(this, getContext());
        ConnectionRequestTask connectionRequestTask = api.sendRequest(mAttendee.email);
        connectionRequestTask.execute((Void) null);
    }

    @Override
    public void onTaskComplete() {
        mAttendee.status = "waiting";

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.detach(this).attach(this).commit();
    }

    @Override
    public void onTaskComplete(String response) {

    }
}
