package org.shingo.shingoeventsapp.ui.recipients;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import org.shingo.shingoeventsapp.R;
import org.shingo.shingoeventsapp.api.OnTaskComplete;
import org.shingo.shingoeventsapp.api.RestApi;
import org.shingo.shingoeventsapp.data.recipients.GetRecipientsTask;
import org.shingo.shingoeventsapp.data.recipients.Recipients;
import org.shingo.shingoeventsapp.data.recipients.RecipientsListAdapter;
import org.shingo.shingoeventsapp.ui.events.EventListActivity;

import java.util.Collections;
import java.util.List;

/**
 * An activity representing a list of Recipients. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link RecipientDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class RecipientListActivity extends AppCompatActivity implements OnTaskComplete {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipient_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        RestApi rest = new RestApi(this, this);
        GetRecipientsTask getRecipientsTask = rest.getRecipients(getIntent().getExtras().getString("event_id"));
        getRecipientsTask.execute((Void) null);

        if (findViewById(R.id.recipient_detail_container) != null) {
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
            i.putExtra("event_id", getIntent().getExtras().getString("event_id"));
            navigateUpTo(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTaskComplete() {
        Collections.sort(Recipients.AWARD_RECIPIENTS);
        Collections.sort(Recipients.RESEARCH_RECIPIENTS);
        //ListView awardRecipients = (ListView)findViewById(R.id.award_recipient_list);
        //awardRecipients.setAdapter(new RecipientsListAdapter<>(this, Recipients.AWARD_RECIPIENTS));
        //ListView researchRecipients = (ListView)findViewById(R.id.research_recipient_list);
        //researchRecipients.setAdapter(new RecipientsListAdapter<>(this, Recipients.RESEARCH_RECIPIENTS));

        final LinearLayout awardRecipients = (LinearLayout)findViewById(R.id.award_recipient_list);
        final RecipientsListAdapter listAdapter = new RecipientsListAdapter<>(this, Recipients.AWARD_RECIPIENTS);

        final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        layoutParams.setMargins(10, 10, 10, 10);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i < listAdapter.getCount(); i++) {
                    View item = listAdapter.getView(i,null,null);
                    final int position = i;

//            item.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                }
//            });
                    awardRecipients.addView(item,layoutParams);
                }
                awardRecipients.invalidate();
            }
        };
        runOnUiThread(runnable);
    }
}
