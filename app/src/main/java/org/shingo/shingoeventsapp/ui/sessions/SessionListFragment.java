package org.shingo.shingoeventsapp.ui.sessions;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

import org.shingo.shingoeventsapp.api.OnTaskComplete;
import org.shingo.shingoeventsapp.api.RestApi;
import org.shingo.shingoeventsapp.data.sessions.SessionsListAdapter;
import org.shingo.shingoeventsapp.data.sessions.GetSessionsTask;
import org.shingo.shingoeventsapp.data.sessions.Sessions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * A list fragment representing a list of Sessions. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link SessionDetailFragment}.
 * <p/>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class SessionListFragment extends ListFragment implements OnTaskComplete {

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

    private List monday = new ArrayList<>();
    private List tuesday = new ArrayList<>();
    private List wednesday = new ArrayList<>();
    private List thursday = new ArrayList<>();
    private List friday = new ArrayList<>();

    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

    @Override
    public void onTaskComplete() {
        try {
            SessionsListAdapter adapter = new SessionsListAdapter(getContext());
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
            for(Sessions.Session s : Sessions.SESSIONS){
                Date start = formatter.parse(s.startTime);
                switch(start.getDay()){
                    // 0 = Sunday, 1 = Monday, 2 = Tuesday, 3 = Wednesday, 4 = Thursday, 5 = Friday, 6 = Saturday
                    case 1:
                        monday.add(s);
                        break;
                    case 2:
                        tuesday.add(s);
                        break;
                    case 3:
                        wednesday.add(s);
                        break;
                    case 4:
                        thursday.add(s);
                        break;
                    case 5:
                        friday.add(s);
                        break;
                }
            }

            Collections.sort(monday);
            Collections.sort(tuesday);
            Collections.sort(wednesday);
            Collections.sort(thursday);
            Collections.sort(friday);
            adapter.addSectionHeaderItem("Monday");
            adapter.addAllItems(monday);
            adapter.addSectionHeaderItem("Tuesday");
            adapter.addAllItems(tuesday);
            adapter.addSectionHeaderItem("Wednesday");
            adapter.addAllItems(wednesday);
            adapter.addSectionHeaderItem("Thursday");
            adapter.addAllItems(thursday);
            adapter.addSectionHeaderItem("Friday");
            adapter.addAllItems(friday);

            setListAdapter(adapter);
        } catch (NullPointerException e){
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

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
    public SessionListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String id = SessionListActivity.eventId;

        RestApi api = new RestApi(this, getContext());
        GetSessionsTask getSessionsTask = api.getSessions(id);
        getSessionsTask.execute((Void) null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            int position = terpPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
            setActivatedPosition(position);
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

    private int terpPosition(int position){
        if(position <= monday.size() + 1) position -= 1;
        else if(position <= monday.size() + tuesday.size() + 2) position -= monday.size() + 2;
        else if(position <= monday.size() + tuesday.size() + wednesday.size() + 3) position -= monday.size() + tuesday.size() + 3;
        else if(position <= monday.size() + tuesday.size() + wednesday.size() + thursday.size() + 4) position -= monday.size() + tuesday.size() + wednesday.size() + 4;
        else if(position <= monday.size() + tuesday.size() + wednesday.size() + thursday.size() + friday.size() + 5) position -= monday.size() + tuesday.size() + wednesday.size() + thursday.size() + 5;

        return position;
    }

    private int terpDay(int position){
        if(position <= monday.size() + 1) return 1;
        else if(position <= monday.size() + tuesday.size() + 2) return 2;
        else if(position <= monday.size() + tuesday.size() + wednesday.size() + 3) return 3;
        else if(position <= monday.size() + tuesday.size() + wednesday.size() + thursday.size() + 4) return 4;
        else if(position <= monday.size() + tuesday.size() + wednesday.size() + thursday.size() + friday.size() + 5) return 5;
        return -1;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        int day = terpDay(position);
        super.onListItemClick(listView, view, position, id);

        position = terpPosition(position);
        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.
        switch (day){
            case 1:
                mCallbacks.onItemSelected(((Sessions.Session)monday.get(position)).id);
                break;
            case 2:
                mCallbacks.onItemSelected(((Sessions.Session)tuesday.get(position)).id);
                break;
            case 3:
                mCallbacks.onItemSelected(((Sessions.Session)wednesday.get(position)).id);
                break;
            case 4:
                mCallbacks.onItemSelected(((Sessions.Session)thursday.get(position)).id);
                break;
            case 5:
                mCallbacks.onItemSelected(((Sessions.Session)friday.get(position)).id);
                break;
        }
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
}
