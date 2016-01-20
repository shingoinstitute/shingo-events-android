package org.shingo.shingoeventsapp.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.shingo.shingoeventsapp.R;
import org.shingo.shingoeventsapp.data.events.Events;

/**
 * A fragment representing a single Event detail screen.
 * This fragment is either contained in a {@link EventListActivity}
 * in two-pane mode (on tablets) or a {@link EventDetailActivity}
 * on handsets.
 */
public class EventDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The Event this fragment is presenting.
     */
    private Events.Event mEvent;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EventDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            mEvent = Events.EVENT_MAP.get(getArguments().getString(ARG_ITEM_ID));

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Activity activity = this.getActivity();
        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
        if (appBarLayout != null) {
            appBarLayout.setTitle(mEvent.name);
        }

        View rootView = inflater.inflate(R.layout.fragment_event_detail, container, false);

        // Show the Event content as text in a TextView.
        if (mEvent != null) {
            Button sessions = (Button) rootView.findViewById(R.id.action_agenda);
            sessions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startAgendaActivity();
                }
            });
            Button speakers = (Button) rootView.findViewById(R.id.action_speakers);
            speakers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startSpeakersActivity();
                }
            });
            ((Button) rootView.findViewById(R.id.action_city_map)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startCityMapActivity();
                }
            });
        }

        return rootView;
    }

    private void startCityMapActivity(){
        Intent i = new Intent(getContext(), MapsActivity.class);
        i.putExtra("event_id", mEvent.id);
        startActivity(i);
    }

    private void startAgendaActivity(){
        Bundle args = new Bundle();
        args.putString("event_id", mEvent.id);
        Intent i = new Intent(getContext(), AgendaListActivity.class);
        i.putExtras(args);
        startActivity(i);
    }

    private void startSpeakersActivity(){
        Bundle args = new Bundle();
        args.putString("event_id", mEvent.id);
        Intent i = new Intent(getContext(), SpeakerListActivity.class);
        i.putExtras(args);
        startActivity(i);
    }
}
