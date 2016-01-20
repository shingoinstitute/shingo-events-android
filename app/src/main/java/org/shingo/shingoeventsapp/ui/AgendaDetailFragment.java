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

import org.shingo.shingoeventsapp.R;
import org.shingo.shingoeventsapp.api.OnTaskComplete;
import org.shingo.shingoeventsapp.api.RestApi;
import org.shingo.shingoeventsapp.data.agendas.GetDayTask;
import org.shingo.shingoeventsapp.data.agendas.Agendas;

/**
 * A fragment representing a single Agenda detail screen.
 * This fragment is either contained in a {@link AgendaListActivity}
 * in two-pane mode (on tablets) or a {@link AgendaDetailActivity}
 * on handsets.
 */
public class AgendaDetailFragment extends Fragment implements OnTaskComplete {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The day this fragment is presenting.
     */
    private Agendas.Day mDay;
    private View rootView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AgendaDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mDay = Agendas.AGENDA_MAP.get(getArguments().getString(ARG_ITEM_ID));

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mDay.toString());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_agenda_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mDay != null) {
            RestApi api = new RestApi(this, getContext());
            GetDayTask getDayTask = api.getDay(mDay.id);
            getDayTask.execute((Void) null);
        }

        return rootView;
    }

    @Override
    public void onTaskComplete() {
        ListView sessions = (ListView)rootView.findViewById(R.id.agenda_sessions);
        sessions.setAdapter(new ArrayAdapter<Agendas.Day.Session>(
                getActivity(),
                android.R.layout.simple_list_item_activated_1,
                android.R.id.text1,
                Agendas.AGENDA_MAP.get(mDay.id).sessions));
        sessions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String sId = Agendas.AGENDA_MAP.get(mDay.id).sessions.get(position).id;
                Bundle args = new Bundle();
                args.putString("session_id", sId);
                args.putString("event_id", AgendaListActivity.mEventId);
                Intent i = new Intent(getContext(), SessionListActivity.class);
                i.putExtras(args);
                startActivity(i);
            }
        });
    }
}
