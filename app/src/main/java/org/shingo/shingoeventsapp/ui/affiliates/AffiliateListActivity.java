package org.shingo.shingoeventsapp.ui.affiliates;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;

import android.view.MenuItem;

import org.shingo.shingoeventsapp.R;
import org.shingo.shingoeventsapp.ui.events.EventDetailActivity;
import org.shingo.shingoeventsapp.ui.events.EventDetailFragment;
import org.shingo.shingoeventsapp.ui.events.EventListActivity;
import org.shingo.shingoeventsapp.ui.events.EventListFragment;

/**
 * An activity representing a list of Affiliates. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link AffiliateDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p/>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link AffiliateListFragment} and the item details
 * (if present) is a {@link AffiliateDetailFragment}.
 * <p/>
 * This activity also implements the required
 * {@link AffiliateListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class AffiliateListActivity extends AppCompatActivity
        implements AffiliateListFragment.Callbacks {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private String mEventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_affiliate_app_bar);

        if(getIntent().getExtras() != null)
            mEventId = getIntent().getExtras().getString("event_id");
        else
            mEventId = EventDetailFragment.mEvent_id;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        // Show the Up button in the action bar.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (findViewById(R.id.affiliate_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((AffiliateListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.affiliate_list))
                    .setActivateOnItemClick(true);
        }

        // TODO: If exposing deep links into your app, handle intents here.
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
     * Callback method from {@link AffiliateListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(AffiliateDetailFragment.ARG_ITEM_ID, id);
            AffiliateDetailFragment fragment = new AffiliateDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.affiliate_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, AffiliateDetailActivity.class);
            detailIntent.putExtra(AffiliateDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }
}
