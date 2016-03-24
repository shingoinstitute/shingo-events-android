package org.shingo.shingoeventsapp.ui.exhibitors;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import org.shingo.shingoeventsapp.R;
import org.shingo.shingoeventsapp.api.OnTaskComplete;
import org.shingo.shingoeventsapp.api.RestApi;
import org.shingo.shingoeventsapp.data.exhibitors.Exhibitors;
import org.shingo.shingoeventsapp.data.exhibitors.ExhibitorsListAdapter;
import org.shingo.shingoeventsapp.data.exhibitors.GetExhibitorsTask;
import org.shingo.shingoeventsapp.ui.events.EventListActivity;

import java.util.Collections;

/**
 * An activity representing a list of Exhibitors. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ExhibitorDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ExhibitorListActivity extends AppCompatActivity implements OnTaskComplete{

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    public static String mEvent_id;
    private LinearLayout pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exhibitor_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mEvent_id = getIntent().getExtras().getString("event_id");  // Related Event SF ID
        pb = (LinearLayout)findViewById(R.id.exhibitor_pb);

        // Async Call to api to get list of Exhibitors
        RestApi api = new RestApi(this, this);
        GetExhibitorsTask getExhibitorsTask = api.getExhibitors(mEvent_id);
        getExhibitorsTask.execute((Void) null);

        // Set the progress bar to visible
        pb.setVisibility(View.VISIBLE);
        if (findViewById(R.id.exhibitor_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
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
            Intent i = new Intent(this, EventListActivity.class);
            i.putExtra("event_id", mEvent_id);
            navigateUpTo(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupListView(@NonNull ListView listView) {
        Collections.sort(Exhibitors.EXHIBITORS);
        listView.setAdapter(new ExhibitorsListAdapter(this, Exhibitors.EXHIBITORS));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mTwoPane) {
                    // In two-pane mode, show the detail view in this activity by
                    // adding or replacing the detail fragment using a
                    // fragment transaction.
                    Bundle arguments = new Bundle();
                    arguments.putString(ExhibitorDetailFragment.ARG_ITEM_ID, Exhibitors.EXHIBITORS.get(position).id);
                    ExhibitorDetailFragment fragment = new ExhibitorDetailFragment();
                    fragment.setArguments(arguments);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.event_detail_container, fragment)
                            .commit();

                } else {
                    // In single-pane mode, simply start the detail activity
                    // for the selected item ID.
                    startExhibitorDetailActivity(position);
                }
            }
        });
    }

    void startExhibitorDetailActivity(int position){
        Intent detailIntent = new Intent(this, ExhibitorDetailActivity.class);
        detailIntent.putExtra(ExhibitorDetailFragment.ARG_ITEM_ID, Exhibitors.EXHIBITORS.get(position).id);
        startActivity(detailIntent);
    }

    @Override
    public void onTaskComplete() {
        try {
            View listView = findViewById(R.id.exhibitor_list);
            assert listView != null;
            setupListView((ListView) listView);
            pb.setVisibility(View.GONE); // Dismiss progress bar
        } catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onTaskComplete(String response) {

    }
}
