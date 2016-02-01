package org.shingo.shingoeventsapp.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.shingo.shingoeventsapp.R;
import org.shingo.shingoeventsapp.api.OnTaskComplete;
import org.shingo.shingoeventsapp.api.RestApi;
import org.shingo.shingoeventsapp.data.sessions.GetSessionTask;
import org.shingo.shingoeventsapp.data.sessions.Sessions;
import org.shingo.shingoeventsapp.data.speakers.Speakers;
import org.shingo.shingoeventsapp.data.speakers.SpeakersListAdapter;

/**
 * A fragment representing a single Session detail screen.
 * This fragment is either contained in a {@link SessionListActivity}
 * in two-pane mode (on tablets) or a {@link SessionDetailActivity}
 * on handsets.
 */
public class SessionDetailFragment extends Fragment {
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

            while(mSession == null){
                try {
                    Thread.sleep(1000);
                    mSession = Sessions.SESSION_MAP.get(getArguments().getString(ARG_ITEM_ID));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if(mSession != null) {
                Activity activity = this.getActivity();
                CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
                if (appBarLayout != null) {
                    appBarLayout.setTitle(mSession.name);
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_session_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mSession != null) {
            ((TextView) rootView.findViewById(R.id.session_name)).setText(mSession.name);
            ((TextView) rootView.findViewById(R.id.session_room)).setText(mSession.room);
            ((TextView) rootView.findViewById(R.id.session_abstract)).setText(mSession.sAbstract);
            ((ListView) rootView.findViewById(R.id.session_speakers)).setAdapter(new ArrayAdapter<Sessions.Session.sSpeaker>(
                    getActivity(),
                    android.R.layout.simple_list_item_activated_1,
                    android.R.id.text1,
                    Sessions.SESSION_MAP.get(mSession.id).speakers));
            ((ListView) rootView.findViewById(R.id.session_speakers)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent i = new Intent(getContext(), SpeakerListActivity.class);
                    i.putExtra("speaker_id", Sessions.SESSION_MAP.get(mSession.id).speakers.get(position).id);
                    i.putExtra("event_id", AgendaListActivity.mEventId);
                    startActivity(i);
                }
            });
        }

        return rootView;
    }
}
