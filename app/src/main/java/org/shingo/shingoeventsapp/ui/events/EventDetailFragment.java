package org.shingo.shingoeventsapp.ui.events;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.shingo.shingoeventsapp.R;
import org.shingo.shingoeventsapp.api.InitLoader;
import org.shingo.shingoeventsapp.data.events.Events;
import org.shingo.shingoeventsapp.ui.MapsActivity;
import org.shingo.shingoeventsapp.ui.affiliates.AffiliateListActivity;
import org.shingo.shingoeventsapp.ui.agendas.AgendaListActivity;
import org.shingo.shingoeventsapp.ui.exhibitors.ExhibitorListActivity;
import org.shingo.shingoeventsapp.ui.recipients.RecipientListActivity;
import org.shingo.shingoeventsapp.ui.speakers.SpeakerListActivity;
import org.shingo.shingoeventsapp.ui.sponsors.SponsorListActivity;
import org.shingo.shingoeventsapp.ui.venuemaps.VenueMapsActivity;

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
    public static final String ARG_ITEM_ID = "event_id";

    /**
     * The Event this fragment is presenting.
     */
    private Events.Event mEvent;
    public static String mEvent_id;

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
            mEvent_id = getArguments().getString(ARG_ITEM_ID);
            mEvent = Events.EVENT_MAP.get(mEvent_id);

            InitLoader loader = new InitLoader(mEvent_id, getContext());
            loader.load();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        while(mEvent == null && getArguments().containsKey(ARG_ITEM_ID)){
            mEvent_id = getArguments().getString(ARG_ITEM_ID);
            mEvent = Events.EVENT_MAP.get(mEvent_id);
        }
        Activity activity = this.getActivity();
        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
        if (appBarLayout != null) {
            appBarLayout.setTitle(mEvent.name);
        }

        View rootView = inflater.inflate(R.layout.fragment_event_detail, container, false);

        // Show the Event content as text in a TextView.
        if (mEvent != null) {
            (rootView.findViewById(R.id.action_agenda)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navigateTo(AgendaListActivity.class);
                }
            });
            (rootView.findViewById(R.id.action_speakers)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navigateTo(SpeakerListActivity.class);
                }
            });
            (rootView.findViewById(R.id.action_city_map)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navigateTo(MapsActivity.class);
                }
            });
            (rootView.findViewById(R.id.action_affiliates)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navigateTo(AffiliateListActivity.class);
                }
            });
            (rootView.findViewById(R.id.action_exhibitors)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navigateTo(ExhibitorListActivity.class);
                }
            });
            (rootView.findViewById(R.id.action_recipients)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navigateTo(RecipientListActivity.class);
                }
            });
            (rootView.findViewById(R.id.action_sponsors)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navigateTo(SponsorListActivity.class);
                }
            });
            (rootView.findViewById(R.id.action_venue_maps)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navigateTo(VenueMapsActivity.class);
                }
            });
        }

        return rootView;
    }

    private void navigateTo(Class dest){
        Intent i = new Intent(getContext(),dest);
        i.putExtra("event_id", mEvent.id);
        startActivity(i);
    }
}
