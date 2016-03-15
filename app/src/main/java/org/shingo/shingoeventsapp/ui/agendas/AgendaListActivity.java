package org.shingo.shingoeventsapp.ui.agendas;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;

import android.view.MenuItem;

import org.shingo.shingoeventsapp.R;
import org.shingo.shingoeventsapp.ui.events.EventListActivity;

/**
 * An activity representing a list of Agendas. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link AgendaDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link AgendaListFragment} and the item details
 * (if present) is a {@link AgendaDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link AgendaListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class AgendaListActivity extends AppCompatActivity
        implements AgendaListFragment.Callbacks {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    public static String mEventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEventId = getIntent().getExtras().getString("event_id");

        setContentView(R.layout.activity_agenda_app_bar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        // Show the Up button in the action bar.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (findViewById(R.id.agenda_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((AgendaListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.agenda_list))
                    .setActivateOnItemClick(true);
        }

        if(getIntent().getExtras().containsKey("day_id"))
            onItemSelected(getIntent().getExtras().getString("day_id"));
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
            Intent i = new Intent(this, EventListActivity.class);
            i.putExtra("event_id", mEventId);
            navigateUpTo(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Callback method from {@link AgendaListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(AgendaDetailFragment.ARG_ITEM_ID, id);
            arguments.putString("event_id", getIntent().getExtras().getString("event_id"));
            AgendaDetailFragment fragment = new AgendaDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.agenda_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, AgendaDetailActivity.class);
            detailIntent.putExtra(AgendaDetailFragment.ARG_ITEM_ID, id);
            detailIntent.putExtra("event_id", getIntent().getExtras().getString("event_id"));
            startActivity(detailIntent);
        }
    }
}
