package org.shingo.shingoeventsapp.ui.sessions;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.MenuItem;

import org.shingo.shingoeventsapp.R;
import org.shingo.shingoeventsapp.ui.agendas.AgendaListActivity;

/**
 * An activity representing a list of Sessions. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link SessionDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p/>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link SessionListFragment} and the item details
 * (if present) is a {@link SessionDetailFragment}.
 * <p/>
 * This activity also implements the required
 * {@link SessionListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class SessionListActivity extends AppCompatActivity
        implements SessionListFragment.Callbacks {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    public static String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getIntent().getExtras();
        eventId = args.getString("event_id");

        setContentView(R.layout.activity_session_app_bar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());


        // Show the Up button in the action bar.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (findViewById(R.id.session_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((SessionListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.session_list))
                    .setActivateOnItemClick(true);
        }

        if(getIntent().getExtras().containsKey("session_id")){
            System.out.println("got session_id: " + getIntent().getExtras().getString("session_id"));
            onItemSelected(getIntent().getExtras().getString("session_id"));
        }
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
            Intent i = new Intent(this, AgendaListActivity.class);
            i.putExtra("event_id", eventId);
            i.putExtra("day_id", getIntent().getExtras().getString("day_id"));
            navigateUpTo(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Callback method from {@link SessionListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(SessionDetailFragment.ARG_ITEM_ID, id);
            arguments.putString("event_id",eventId);
            arguments.putString("day_id", getIntent().getExtras().getString("day_id"));
            SessionDetailFragment fragment = new SessionDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.session_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, SessionDetailActivity.class);
            detailIntent.putExtra(SessionDetailFragment.ARG_ITEM_ID, id);
            detailIntent.putExtra("event_id", eventId);
            detailIntent.putExtra("day_id", getIntent().getExtras().getString("day_id"));
            startActivity(detailIntent);
        }
    }
}
