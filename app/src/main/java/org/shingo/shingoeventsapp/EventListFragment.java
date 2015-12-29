package org.shingo.shingoeventsapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.shingo.shingoeventsapp.events.Events;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A list fragment representing a list of Events. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link EventDetailFragment}.
 * <p/>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class EventListFragment extends ListFragment {

    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private Callbacks mCallbacks = sDummyCallbacks;

    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

    private GetEventsTask getEventsTask = null;

    public Events mEvents = new Events();

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(String id);
    }

    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(String id) {
        }
    };

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EventListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getActivity().getApplicationContext().getSharedPreferences("login", Context.MODE_PRIVATE);
        String mEmail = sharedPreferences.getString("email","");
        System.out.println("Got preference email: " + mEmail);
        if(!mEmail.isEmpty()){
            System.out.println("Starting GetEventsTask with email: " + mEmail);
            getEventsTask = new GetEventsTask(mEmail);
            getEventsTask.execute((Void) null);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.
        mCallbacks.onItemSelected(Events.EVENTS.get(position).id);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     */
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        getListView().setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    private class GetEventsTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private String output;

        GetEventsTask(String email) {
            mEmail = email;
            System.out.println("GetEventsTask created");
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            System.out.println("GetEventsTask.doInBackground called");
            boolean success  = false;
            try {
                String data = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(mEmail,"UTF-8");
                URL url = new URL("https://shingo-events.herokuapp.com/api/regevents?client_id=" + LoginActivity.CLIENT_ID + "&client_secret=" + LoginActivity.CLIENT_SECRET);
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = "";
                while((line = reader.readLine()) != null){
                    sb.append(line);
                }
                output = sb.toString();
                JSONObject response = new JSONObject(output);
                System.out.println("RegOnline response: " + output);
                success = response.getBoolean("success");
                if(success){
                    System.out.println("EventIDs fetched successfully");
                    JSONArray jEvents = response.getJSONArray("events");
                    if(jEvents.length() != 0) {
                        String jString = "";
                        for (int i = 0; i < jEvents.length(); i++) {
                            jString += jEvents.getString(i);
                            if (i != jEvents.length() - 1) {
                                jString += ",";
                            }
                        }
                        String eventData = URLEncoder.encode("event_ids", "UTF-8") + "=" + URLEncoder.encode(jString, "UTF-8");
                        System.out.println("Making api call: " + eventData);
                        URL sfurl = new URL("https://shingo-events.herokuapp.com/api/sfevents?client_id=" + LoginActivity.CLIENT_ID + "&client_secret=" + LoginActivity.CLIENT_SECRET);
                        URLConnection sfconn = sfurl.openConnection();
                        sfconn.setDoOutput(true);
                        OutputStreamWriter sfwr = new OutputStreamWriter(sfconn.getOutputStream());
                        sfwr.write(eventData);
                        sfwr.flush();

                        BufferedReader sfreader = new BufferedReader(new InputStreamReader(sfconn.getInputStream()));
                        StringBuilder eventsb = new StringBuilder();
                        String sfline = "";
                        while ((sfline = sfreader.readLine()) != null) {
                            eventsb.append(sfline);
                        }
                        output = eventsb.toString();
                        response = new JSONObject(output);
                        System.out.println("SFEvents " + output);
                        success = response.getBoolean("success");
                        if (success) {
                            JSONArray jSfevents = response.getJSONObject("events").getJSONArray("records");
                            for (int i = 0; i < jSfevents.length(); i++) {
                                JSONObject jEvent = jSfevents.getJSONObject(i);
                                Events.Event mEvent = new Events.Event(jEvent.getString("Id"), jEvent.getString("Name"), jEvent.getJSONObject("attributes").getString("url"), jEvent.getString("Event_Start_Date__c"),
                                        jEvent.getString("Event_End_Date__c"), jEvent.getString("Host_City__c"), "");
                                Events.addEvent(mEvent);
                            }
                        }
                    }
                }
            } catch(UnsupportedEncodingException e){
                return success;
            } catch(IOException e ){
                return success;
            } catch(JSONException e){
                return false;
            }

            return success ;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            getEventsTask = null;

            if (success) {
                System.out.println("Setting list adapter");
                Collections.sort(Events.EVENTS);
                setListAdapter(new ArrayAdapter<Events.Event>(
                        getActivity(),
                        android.R.layout.simple_list_item_activated_1,
                        android.R.id.text1,
                        Events.EVENTS));
            } else {
                System.out.println("An error occurred...");
            }
        }

        @Override
        protected void onCancelled() {
            getEventsTask = null;
        }
    }
}
