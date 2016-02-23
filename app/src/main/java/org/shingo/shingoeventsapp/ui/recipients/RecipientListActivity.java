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
import android.widget.AdapterView;
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

import java.util.ArrayList;
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

    public static String mEvent;

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

        mEvent = getIntent().getExtras().getString("event_id");

        RestApi rest = new RestApi(this, this);
        GetRecipientsTask getRecipientsTask = rest.getRecipients(mEvent);
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
            i.putExtra("event_id", mEvent);
            navigateUpTo(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTaskComplete() {
        Collections.sort(Recipients.AWARD_RECIPIENTS);
        Collections.sort(Recipients.RESEARCH_RECIPIENTS);
        List<Object> list = new ArrayList<Object>(Recipients.AWARD_RECIPIENTS);
        list.addAll(Recipients.RESEARCH_RECIPIENTS);


        final ListView recipients = (ListView)findViewById(R.id.recipient_list);
        final RecipientsListAdapter adapter = new RecipientsListAdapter(this);
        adapter.addSectionHeaderItem("Prize Challengers");
        adapter.addAllItems(Recipients.AWARD_RECIPIENTS);
        adapter.addSectionHeaderItem("Research Recipients");
        adapter.addAllItems(Recipients.RESEARCH_RECIPIENTS);
        recipients.setAdapter(adapter);
        recipients.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position < Recipients.AWARD_RECIPIENTS.size() + 1 && adapter.getItemViewType(position) == 0){
                    if (mTwoPane) {
                        // In two-pane mode, show the detail view in this activity by
                        // adding or replacing the detail fragment using a
                        // fragment transaction.
                        Bundle arguments = new Bundle();
                        arguments.putString(RecipientDetailFragment.ARG_ITEM_ID, Recipients.AWARD_RECIPIENTS.get(position - 1).id);
                        arguments.putString("recipient_type", "award");
                        RecipientDetailFragment fragment = new RecipientDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.event_detail_container, fragment)
                                .commit();

                    } else {
                        // In single-pane mode, simply start the detail activity
                        // for the selected item ID.
                        startRecipientDetailActivity(position - 1, 0);
                    }
                } else if (adapter.getItemViewType(position) == 0 && position > Recipients.AWARD_RECIPIENTS.size() + 1) {
                    if (mTwoPane) {
                        // In two-pane mode, show the detail view in this activity by
                        // adding or replacing the detail fragment using a
                        // fragment transaction.
                        Bundle arguments = new Bundle();
                        arguments.putString(RecipientDetailFragment.ARG_ITEM_ID, Recipients.AWARD_RECIPIENTS.get(position - (Recipients.AWARD_RECIPIENTS.size() + 2)).id);
                        arguments.putString("recipient_type", "research");
                        RecipientDetailFragment fragment = new RecipientDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.event_detail_container, fragment)
                                .commit();

                    } else {
                        // In single-pane mode, simply start the detail activity
                        // for the selected item ID.
                        startRecipientDetailActivity(position - (Recipients.AWARD_RECIPIENTS.size() + 2), 1);
                    }
                }
            }
        });

    }

    private void startRecipientDetailActivity(int position, int type)
    {
        Intent detailIntent = new Intent(this, RecipientDetailActivity.class);
        if(type == 0) {
            detailIntent.putExtra(RecipientDetailFragment.ARG_ITEM_ID, Recipients.AWARD_RECIPIENTS.get(position).id);
            detailIntent.putExtra("recipient_type", "award");
        } else {
            detailIntent.putExtra(RecipientDetailFragment.ARG_ITEM_ID, Recipients.RESEARCH_RECIPIENTS.get(position).id);
            detailIntent.putExtra("recipient_type", "research");
        }
        startActivity(detailIntent);
    }
}
