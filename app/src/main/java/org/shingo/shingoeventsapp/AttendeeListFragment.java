package org.shingo.shingoeventsapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.shingo.shingoeventsapp.attendees.Attendees;
import org.shingo.shingoeventsapp.connections.Connections;

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
 * A list fragment representing a list of Attendees. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link AttendeeDetailFragment}.
 * <p/>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class AttendeeListFragment extends ListFragment {

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

    private GetAttendeesTask getAttendeesTask = null;

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
    public AttendeeListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        System.out.println("Starting GetAttendeesTask");
        getAttendeesTask = new GetAttendeesTask();
        getAttendeesTask.execute((Void) null);
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
        mCallbacks.onItemSelected(Attendees.ATTENDEES.get(position).email);
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
    private class GetAttendeesTask extends AsyncTask<Void, Void, Boolean> {

        private String output;

        GetAttendeesTask() {
            System.out.println("GetAttendeesTask created");
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            System.out.println("GetAttendees.doInBackground called");
            SharedPreferences sharedPreferences = getActivity().getApplicationContext().getSharedPreferences("login", Context.MODE_PRIVATE);
            String email = sharedPreferences.getString("email", "");
            boolean success  = false;
            try {
                URL url = new URL("https://shingo-events.herokuapp.com/api/attendees?client_id=" + LoginActivity.CLIENT_ID + "&client_secret=" + LoginActivity.CLIENT_SECRET);
                System.out.println("Opening connection: " + url.getPath());
                URLConnection conn = url.openConnection();
                System.out.println("Got connection: " + conn.getURL());

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = "";
                while((line = reader.readLine()) != null){
                    sb.append(line);
                }
                output = sb.toString();
                JSONObject response = new JSONObject(output);
                System.out.println("getAttendees() response: " + output);
                success = response.getBoolean("success");
                if(success){
                    System.out.println("Attendees fetched successfully");
                    Attendees.clear();
                    JSONArray jAttendees = response.getJSONArray("attendees");
                    for(int i = 0; i < jAttendees.length(); i++){
                        JSONObject jAttendee = jAttendees.getJSONObject(i);
                        JSONArray jConnections = jAttendee.getJSONArray("connections");
                        List<String> connections = new ArrayList<>();
                        for(int j = 0; j < jConnections.length(); j++){
                            JSONObject connection = jConnections.getJSONObject(j);
                            connections.add(connection.getString("email"));
                        }
                        System.out.println("connections " + connections.toString());
                        if(connections.contains(email) && jConnections.getJSONObject(connections.indexOf(email)).getString("status").toLowerCase().equals("approved"))
                            Attendees.addAttendee(new Attendees.Attendee(jAttendee.getString("email"),
                                    jAttendee.getString("first_name"), jAttendee.getString("last_name"),
                                    jAttendee.getString("display_name"), jAttendee.getString("title"),
                                    jAttendee.getString("company"),connections, 0));
                        else if(connections.contains(email) && jConnections.getJSONObject(connections.indexOf(email)).getString("status").toLowerCase().equals("pending"))
                            Attendees.addAttendee(new Attendees.Attendee(jAttendee.getString("email"),
                                    jAttendee.getString("first_name"), jAttendee.getString("last_name"),
                                    jAttendee.getString("display_name"), jAttendee.getString("title"),
                                    jAttendee.getString("company"),connections, 1));
                        else if(connections.contains(email) && jConnections.getJSONObject(connections.indexOf(email)).getString("status").toLowerCase().equals("rejected")
                                || jAttendee.getString("email").equals(email)){}
                            // DO NOTHING
                        else
                            Attendees.addAttendee(new Attendees.Attendee(jAttendee.getString("email"),
                                    jAttendee.getString("first_name"), jAttendee.getString("last_name"),
                                    jAttendee.getString("display_name"), jAttendee.getString("title"),
                                    jAttendee.getString("company"),connections, -1));
                    }
                }
            } catch(UnsupportedEncodingException e){
                e.printStackTrace();
                return success;
            } catch(IOException e ){
                e.printStackTrace();
                return success;
            } catch(JSONException e){
                e.printStackTrace();
                return false;
            }

            return success ;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            getAttendeesTask = null;

            if (success) {
                System.out.println("Setting list adapter");
                Collections.sort(Attendees.ATTENDEES);
                setListAdapter(new ArrayAdapter<Attendees.Attendee>(
                        getActivity(),
                        android.R.layout.simple_list_item_activated_1,
                        android.R.id.text1,
                        Attendees.ATTENDEES));
            } else {
                System.out.println("An error occurred...");
            }
        }

        @Override
        protected void onCancelled() {
            getAttendeesTask = null;
        }
    }
}
