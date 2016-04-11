package org.shingo.shingoeventsapp.ui.agendas;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONObject;
import org.shingo.shingoeventsapp.R;
import org.shingo.shingoeventsapp.api.GetAsyncData;
import org.shingo.shingoeventsapp.api.OnTaskComplete;
import org.shingo.shingoeventsapp.api.RestApi;
import org.shingo.shingoeventsapp.data.agendas.Agendas;
import org.shingo.shingoeventsapp.data.sessions.Sessions;
import org.shingo.shingoeventsapp.data.sessions.SessionsListAdapter;
import org.shingo.shingoeventsapp.data.speakers.Speakers;
import org.shingo.shingoeventsapp.ui.sessions.SessionListActivity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    private LinearLayout progressBar;
    private ProgressDialog pd;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AgendaDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume(){
        if(pd != null) pd.dismiss();
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_agenda_detail, container, false);
        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mDay = Agendas.AGENDA_MAP.get(getArguments().getString(ARG_ITEM_ID));
            System.out.println("New day selected: " + mDay.name + " : " + mDay.id);

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mDay.toString());
            }

            progressBar = (LinearLayout)activity.findViewById(R.id.agenda_progress);
            if(progressBar != null) progressBar.setVisibility(View.VISIBLE);
        }

        if(progressBar==null) progressBar = (LinearLayout)rootView.findViewById(R.id.agenda_progress);

        if (mDay != null) {
            try{
                RestApi api = new RestApi(this, getContext());
                String[] params = {"/sfevents/day", URLEncoder.encode("day_id", "UTF-8") + "="
                            + URLEncoder.encode(mDay.id, "UTF-8")};
                GetAsyncData getDayTask = api.getAsyncData();
                getDayTask.execute(params);
            }catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        return rootView;
    }

    @Override
    public void onTaskComplete() {
        throw new UnsupportedOperationException("onTaskComplete() has not been implemented. Did you mean onTaskComplete(String response)?");
    }

    @Override
    public void onTaskComplete(String json) {
        try {
            JSONObject response = new JSONObject(json);
            JSONObject jDay = response.getJSONObject("day");
            if (jDay != null) {
                JSONArray jSessions = jDay.getJSONObject("Sessions").getJSONArray("records");
                for (int i = 0; i < jSessions.length(); i++) {
                    JSONObject jSession = jSessions.getJSONObject(i);
                    List<Speakers.Speaker> speakers = new ArrayList<>();
                    JSONArray jSpeakers = jSession.getJSONObject("Speakers").getJSONArray("records");
                    for (int j = 0; j < jSpeakers.length(); j++) {
                        JSONObject jSpeaker = jSpeakers.getJSONObject(j);
                        speakers.add(new Speakers.Speaker(jSpeaker.getString("Id"),
                                jSpeaker.getString("Name"), jSpeaker.getString("Name"), jSpeaker.getString("Title"),
                                jSpeaker.getString("Organization"), "", null));
                    }
                    if (!jSession.has("Room")) jSession.put("Room", "null");
                    Agendas.AGENDA_MAP.get(jDay.getString("Id")).sessions.add((new Sessions.Session(jSession.getString("Id"),
                            jSession.getString("Name"), jSession.getString("Rich_Session_Abstract"),
                            jSession.getString("Session_Notes__c"), jSession.getString("Session_Date__c"), jSession.getString("Session_Format__c"),
                            jSession.getString("Session_Time__c"), speakers, jSession.getString("Room"))));
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }


        LinearLayout sessions = (LinearLayout)rootView.findViewById(R.id.agenda_sessions);
        SessionsListAdapter listAdapter = new SessionsListAdapter(getContext());
        Collections.sort(Agendas.AGENDA_MAP.get(mDay.id).sessions);
        listAdapter.addAllItems(Agendas.AGENDA_MAP.get(mDay.id).sessions);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        layoutParams.setMargins(10, 10, 10, 10);

        for(int i = 0; i < listAdapter.getCount(); i++) {
            View item = listAdapter.getView(i,null,null);
            final int position = i;

            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pd = new ProgressDialog(getContext());
                    pd.setMessage("Loading session...");
                    pd.show();
                    String sId = Agendas.AGENDA_MAP.get(mDay.id).sessions.get(position).id;
                    Bundle args = new Bundle();
                    args.putString("session_id", sId);
                    args.putString("event_id", AgendaListActivity.mEventId);
                    args.putString("day_id", mDay.id);
                    Intent i = new Intent(getContext(), SessionListActivity.class);
                    i.putExtras(args);
                    startActivity(i);
                }
            });
            sessions.addView(item, layoutParams);
        }
        progressBar.setVisibility(View.GONE);
    }
}
