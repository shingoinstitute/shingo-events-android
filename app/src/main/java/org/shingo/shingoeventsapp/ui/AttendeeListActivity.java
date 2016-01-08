package org.shingo.shingoeventsapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;

import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import org.shingo.shingoeventsapp.R;
import org.shingo.shingoeventsapp.data.attendees.Attendees;

import java.util.ArrayList;
import java.util.List;

/**
 * An activity representing a list of Attendees. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link AttendeeDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p/>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link AttendeeListFragment} and the item details
 * (if present) is a {@link AttendeeDetailFragment}.
 * <p/>
 * This activity also implements the required
 * {@link AttendeeListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class AttendeeListActivity extends AppCompatActivity
        implements AttendeeListFragment.Callbacks {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private boolean mSearchOpened;
    private String mSearchQuery;
    private EditText mSearchEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendee_app_bar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        mSearchOpened = false;

        // Show the Up button in the action bar.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (findViewById(R.id.attendee_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((AttendeeListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.attendee_list))
                    .setActivateOnItemClick(true);
        }

        mSearchQuery = "";

        // TODO: If exposing deep links into your app, handle intents here.
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            Intent i = new Intent(this, ConnectionListActivity.class);
            navigateUpTo(i);
            return true;
        } else if(id == R.id.action_search){
            if(mSearchOpened){
                closeSearchBar();
            } else {
                openSearchBar(mSearchQuery);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void closeSearchBar() {
        getSupportActionBar().setDisplayShowCustomEnabled(false);

        mSearchOpened = false;
    }

    private void openSearchBar(String mSearchQuery) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.search_bar);

        mSearchEt = (EditText) actionBar.getCustomView().findViewById(R.id.search_query);
        mSearchEt.addTextChangedListener(new SearchWatcher());
        mSearchEt.requestFocus();

        mSearchOpened = true;
    }

    /**
     * Callback method from {@link AttendeeListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(AttendeeDetailFragment.ARG_ITEM_ID, id);
            AttendeeDetailFragment fragment = new AttendeeDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.attendee_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, AttendeeDetailActivity.class);
            detailIntent.putExtra(AttendeeDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }

    private class SearchWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence c, int i, int i2, int i3) {

        }

        @Override
        public void onTextChanged(CharSequence c, int i, int i2, int i3) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            mSearchQuery = mSearchEt.getText().toString();
            AttendeeListFragment.filteredAttendees = performSearch(Attendees.ATTENDEES, mSearchQuery);
            AttendeeListFragment.attendeeArrayAdapter = new ArrayAdapter<Attendees.Attendee>(
                    AttendeeListActivity.this,
                    android.R.layout.simple_list_item_activated_1,
                    android.R.id.text1,
                    AttendeeListFragment.filteredAttendees);
            AttendeeListFragment.setAdapter(AttendeeListFragment.attendeeArrayAdapter);
        }

        private List<Attendees.Attendee> performSearch(List<Attendees.Attendee> attendees, String mSearchQuery) {
            List<Attendees.Attendee> filtered = new ArrayList<>();
            if(mSearchQuery.contains("@")){
                for(Attendees.Attendee a : attendees){
                    if(a.email.equals(mSearchQuery))
                        filtered.add(a);
                }
            } else {
                for(Attendees.Attendee a : attendees){
                    if(a.displayName.contains(mSearchQuery) || a.firstName.contains(mSearchQuery) || a.lastName.contains(mSearchQuery))
                        filtered.add(a);
                }
            }

            return filtered;
        }

        private ArrayAdapter<Attendees.Attendee> getListAdapter(){
            return AttendeeListFragment.attendeeArrayAdapter;
        }
    }
}
