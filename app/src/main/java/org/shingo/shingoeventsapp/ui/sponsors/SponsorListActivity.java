package org.shingo.shingoeventsapp.ui.sponsors;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import org.shingo.shingoeventsapp.R;
import org.shingo.shingoeventsapp.api.OnTaskComplete;
import org.shingo.shingoeventsapp.api.RestApi;
import org.shingo.shingoeventsapp.data.sponsors.GetSponsorsTask;
import org.shingo.shingoeventsapp.data.sponsors.Sponsors;
import org.shingo.shingoeventsapp.data.sponsors.SponsorsListAdapter;
import org.shingo.shingoeventsapp.ui.events.EventListActivity;

import java.util.Collections;
import java.util.List;

/**
 * An activity representing a list of Sponsors. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link SponsorDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class SponsorListActivity extends AppCompatActivity implements OnTaskComplete {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    public static String mEvent;

    private LinearLayout pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sponsor_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mEvent = getIntent().getExtras().getString("event_id");
        pb = (LinearLayout)findViewById(R.id.sponsor_pb);

        RestApi api = new RestApi(this, this);
        GetSponsorsTask getSponsorsTask = api.getSponsors(mEvent);
        getSponsorsTask.execute((Void) null);
        pb.setVisibility(View.VISIBLE);

        if (findViewById(R.id.sponsor_detail_container) != null) {
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
            i.putExtra("event_id", mEvent);
            navigateUpTo(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTaskComplete() {
        try {
            ListView list = (ListView) findViewById(R.id.sponsor_list);
            SponsorsListAdapter adapter = new SponsorsListAdapter(this);
            List presidents = Sponsors.SPONSORS.get("President");
            List champions = Sponsors.SPONSORS.get("Champion");
            List benefactors = Sponsors.SPONSORS.get("Benefactor");
            List supporters = Sponsors.SPONSORS.get("Supporter");
            List friends = Sponsors.SPONSORS.get("Friend");
            if (presidents != null) {
                adapter.addSectionHeaderItem("Presidents");
                Collections.sort(presidents);
                adapter.addAllItems(presidents);
            }
            if (champions != null) {
                adapter.addSectionHeaderItem("Champions");
                Collections.sort(champions);
                adapter.addAllItems(champions);
            }
            if (benefactors != null) {
                adapter.addSectionHeaderItem("Benefactors");
                Collections.sort(benefactors);
                adapter.addAllItems(benefactors);
            }
            if (supporters != null) {
                adapter.addSectionHeaderItem("Supporters");
                Collections.sort(supporters);
                adapter.addAllItems(supporters);
            }
            if (friends != null) {
                adapter.addSectionHeaderItem("Friends");
                Collections.sort(friends);
                adapter.addAllItems(friends);
                list.setAdapter(adapter);
            }
            pb.setVisibility(View.GONE);

        } catch(NullPointerException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onTaskComplete(String response) {

    }
}
