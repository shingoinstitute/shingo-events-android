package org.shingo.shingoeventsapp.ui.events;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.shingo.shingoeventsapp.R;
import org.shingo.shingoeventsapp.data.sessions.Sessions;
import org.shingo.shingoeventsapp.data.speakers.SpeakersListAdapter;

import java.util.Collections;

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
        View rootView = inflater.inflate(R.layout.fragment_session_detail, container, false);

        if (mSession != null) {
            ((TextView) rootView.findViewById(R.id.session_name)).setText(mSession.name);
            ((TextView) rootView.findViewById(R.id.session_room)).setText(mSession.room);
            ((TextView) rootView.findViewById(R.id.session_abstract))
                    .setText(Html.fromHtml("<font color='black'>Summary:</font>&nbsp;&nbsp;&nbsp;&nbsp;"
                            + mSession.sAbstract), TextView.BufferType.SPANNABLE);
            LinearLayout sessions = (LinearLayout) rootView.findViewById(R.id.session_speakers);
            SpeakersListAdapter listAdapter = new SpeakersListAdapter(getContext(),
                    Sessions.SESSION_MAP.get(mSession.id).speakers, true);
            Collections.sort(Sessions.SESSION_MAP.get(mSession.id).speakers);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            layoutParams.setMargins(10, 10, 10, 10);

            for(int i = 0; i < listAdapter.getCount(); i++) {
                View item = listAdapter.getView(i,null,null);
                final int position = i;

                item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ProgressDialog pd = new ProgressDialog(getContext());
                        pd.show();
                        String sId = Sessions.SESSION_MAP.get(mSession.id).speakers.get(position).id;
                        Bundle args = new Bundle();
                        args.putString("speaker_id", sId);
                        args.putString("event_id", SessionListActivity.eventId);
                        args.putString("session_id", mSession.id);
                        Intent i = new Intent(getContext(), SpeakerListActivity.class);
                        i.putExtras(args);
                        startActivity(i);
                        pd.dismiss();
                    }
                });
                sessions.addView(item,layoutParams);
            }
        }

        return rootView;
    }
}
