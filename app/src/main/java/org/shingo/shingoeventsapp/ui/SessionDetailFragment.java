package org.shingo.shingoeventsapp.ui;

import android.app.Activity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.shingo.shingoeventsapp.R;
import org.shingo.shingoeventsapp.api.OnTaskComplete;
import org.shingo.shingoeventsapp.api.RestApi;
import org.shingo.shingoeventsapp.data.GetSessionTask;
import org.shingo.shingoeventsapp.data.GetSessionsTask;
import org.shingo.shingoeventsapp.data.sessions.Sessions;

/**
 * A fragment representing a single Session detail screen.
 * This fragment is either contained in a {@link SessionListActivity}
 * in two-pane mode (on tablets) or a {@link SessionDetailActivity}
 * on handsets.
 */
public class SessionDetailFragment extends Fragment implements OnTaskComplete {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The session content this fragment is presenting.
     */
    private Sessions.Session mSession;

    private View rootView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SessionDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mSession = Sessions.SESSION_MAP.get(getArguments().getString(ARG_ITEM_ID));

            if(mSession != null) {
                Activity activity = this.getActivity();
                CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
                if (appBarLayout != null) {
                    appBarLayout.setTitle(mSession.name);
                }
            } else {
                RestApi api = new RestApi(this, getContext());
                GetSessionTask getSessionTask = api.getSession(getArguments().getString(ARG_ITEM_ID));
                getSessionTask.execute((Void) null);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_session_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mSession != null) {
            ((TextView) rootView.findViewById(R.id.session_detail)).setText(mSession.sAbstract);
        }

        return rootView;
    }

    @Override
    public void onTaskComplete() {
        // Load the session content

        mSession = Sessions.SESSION_MAP.get(getArguments().getString(ARG_ITEM_ID));

        Activity activity = this.getActivity();
        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
        if (appBarLayout != null) {
            appBarLayout.setTitle(mSession.name);
        }

        if (mSession != null) {
            ((TextView) rootView.findViewById(R.id.session_detail)).setText(mSession.sAbstract);
        }
    }
}
