package org.shingo.shingoeventsapp;

import android.app.Activity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.shingo.shingoeventsapp.connections.Connections;

/**
 * A fragment representing a single Connection detail screen.
 * This fragment is either contained in a {@link ConnectionListActivity}
 * in two-pane mode (on tablets) or a {@link ConnectionDetailActivity}
 * on handsets.
 */
public class ConnectionDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private Connections.Connection mConnection;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ConnectionDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mConnection = Connections.CONNECTION_MAP.get(getArguments().getString(ARG_ITEM_ID));


        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Activity activity = this.getActivity();
        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
        if (appBarLayout != null) {
            appBarLayout.setTitle(mConnection.toString());
        }

        View rootView = inflater.inflate(R.layout.fragment_connection_detail, container, false);
        View parent = container.getRootView();
        if (mConnection != null) {
            TextView emailView = (TextView) rootView.findViewById(R.id.connection_email);
            TextView nameView = (TextView) rootView.findViewById(R.id.connection_display_name);
            TextView titleView = (TextView) rootView.findViewById(R.id.connection_title);
            TextView companyView = (TextView) rootView.findViewById(R.id.connection_company);
            emailView.setText(mConnection.email);
            nameView.setText(mConnection.toString());
            titleView.setText(mConnection.title);
            companyView.setText(mConnection.company);
            FloatingActionButton fab = (FloatingActionButton) parent.findViewById(R.id.fab);
            System.out.println("mAttendee.status " + mConnection.status);
            switch(mConnection.status.toLowerCase()){
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
                case "pending":
                    fab.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_edit));
                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //TODO: Approve/Deny request
                        }
                    });
                    break;
                default:
                    break;
            }
        }

        return rootView;
    }
}
