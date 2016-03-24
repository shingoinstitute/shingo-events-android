package org.shingo.shingoeventsapp.ui.recipients;

import android.content.Intent;
import android.os.Bundle;
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

    private LinearLayout pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipient_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mEvent = getIntent().getExtras().getString("event_id");
        pb = (LinearLayout)findViewById(R.id.recipient_pb);

        RestApi rest = new RestApi(this, this);
        GetRecipientsTask getRecipientsTask = rest.getRecipients(mEvent);
        getRecipientsTask.execute((Void) null);

        pb.setVisibility(View.VISIBLE);

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
        try {
            Collections.sort(Recipients.AWARD_RECIPIENTS, Collections.reverseOrder());
            Collections.sort(Recipients.RESEARCH_RECIPIENTS);
            final List<Recipients.AwardRecipient> shingoPrize = new ArrayList<>();
            final List<Recipients.AwardRecipient> silverMedallion = new ArrayList<>();
            final List<Recipients.AwardRecipient> bronzeMedallion = new ArrayList<>();
            for(Recipients.AwardRecipient recipient : Recipients.AWARD_RECIPIENTS){
                switch (recipient.award){
                    case "Shingo Prize":
                        shingoPrize.add(recipient);
                        break;
                    case "Silver Medallion":
                        silverMedallion.add(recipient);
                        break;
                    case "Bronze Medallion":
                        bronzeMedallion.add(recipient);
                        break;
                }
            }

            final ListView recipients = (ListView) findViewById(R.id.recipient_list);
            final RecipientsListAdapter adapter = new RecipientsListAdapter(this);
            adapter.addSectionHeaderItem("Shingo Prize");
            adapter.addAllItems(shingoPrize);
            adapter.addSectionHeaderItem("Silver Medallion");
            adapter.addAllItems(silverMedallion);
            adapter.addSectionHeaderItem("Bronze Medallion");
            adapter.addAllItems(bronzeMedallion);
            adapter.addSectionHeaderItem("Research Recipients");
            adapter.addAllItems(Recipients.RESEARCH_RECIPIENTS);
            recipients.setAdapter(adapter);
            recipients.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Bundle arguments = new Bundle();
                    if (position < shingoPrize.size() + 1 && adapter.getItemViewType(position) == 0) {
                        if (mTwoPane) {
                            // In two-pane mode, show the detail view in this activity by
                            // adding or replacing the detail fragment using a
                            // fragment transaction.
                            arguments.putString(RecipientDetailFragment.ARG_ITEM_ID, shingoPrize.get(position - 1).id);
                            arguments.putString("recipient_type", "award");

                            RecipientDetailFragment fragment = new RecipientDetailFragment();
                            fragment.setArguments(arguments);
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.recipient_detail_container, fragment)
                                    .commit();

                        } else {
                            // In single-pane mode, simply start the detail activity
                            // for the selected item ID.
                            startRecipientDetailActivity(shingoPrize.get(position - 1).id, 0);
                        }
                    } else if(adapter.getItemViewType(position) == 0 && position < shingoPrize.size() + silverMedallion.size() + 2){
                        if (mTwoPane) {
                            // In two-pane mode, show the detail view in this activity by
                            // adding or replacing the detail fragment using a
                            // fragment transaction.
                            arguments.putString(RecipientDetailFragment.ARG_ITEM_ID, silverMedallion.get(position - (shingoPrize.size() + 2)).id);
                            arguments.putString("recipient_type", "award");

                            RecipientDetailFragment fragment = new RecipientDetailFragment();
                            fragment.setArguments(arguments);
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.recipient_detail_container, fragment)
                                    .commit();

                        } else {
                            // In single-pane mode, simply start the detail activity
                            // for the selected item ID.
                            startRecipientDetailActivity(silverMedallion.get(position - (shingoPrize.size() + 2)).id, 0);
                        }
                    } else if(adapter.getItemViewType(position) == 0 && position < shingoPrize.size() + silverMedallion.size() + bronzeMedallion.size() + 3){
                        if (mTwoPane) {
                            // In two-pane mode, show the detail view in this activity by
                            // adding or replacing the detail fragment using a
                            // fragment transaction.
                            arguments.putString(RecipientDetailFragment.ARG_ITEM_ID, bronzeMedallion.get(position - (shingoPrize.size() + silverMedallion.size() + 3)).id);
                            arguments.putString("recipient_type", "award");

                            RecipientDetailFragment fragment = new RecipientDetailFragment();
                            fragment.setArguments(arguments);
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.recipient_detail_container, fragment)
                                    .commit();

                        } else {
                            // In single-pane mode, simply start the detail activity
                            // for the selected item ID.
                            startRecipientDetailActivity(bronzeMedallion.get(position - (shingoPrize.size() + silverMedallion.size() + 3)).id, 0);
                        }
                    }
                    else {
                        if (mTwoPane) {
                            // In two-pane mode, show the detail view in this activity by
                            // adding or replacing the detail fragment using a
                            // fragment transaction.
                            arguments.putString(RecipientDetailFragment.ARG_ITEM_ID, Recipients.RESEARCH_RECIPIENTS.get(position - (shingoPrize.size() + silverMedallion.size() + bronzeMedallion.size() + 4)).id);
                            arguments.putString("recipient_type", "research");

                            RecipientDetailFragment fragment = new RecipientDetailFragment();
                            fragment.setArguments(arguments);
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.recipient_detail_container, fragment)
                                    .commit();

                        } else {
                            // In single-pane mode, simply start the detail activity
                            // for the selected item ID.
                            startRecipientDetailActivity(Recipients.RESEARCH_RECIPIENTS.get(position - (shingoPrize.size() + silverMedallion.size() + bronzeMedallion.size() + 4)).id, 1);
                        }
                    }
                }
            });

            pb.setVisibility(View.GONE);
        } catch(NullPointerException e){
            e.printStackTrace();
        }
    }

    private void startRecipientDetailActivity(String id, int type)
    {
        Intent detailIntent = new Intent(this, RecipientDetailActivity.class);
        if(type == 0) {
            detailIntent.putExtra(RecipientDetailFragment.ARG_ITEM_ID, id);
            detailIntent.putExtra("recipient_type", "award");
        } else {
            detailIntent.putExtra(RecipientDetailFragment.ARG_ITEM_ID, id);
            detailIntent.putExtra("recipient_type", "research");
        }
        startActivity(detailIntent);
    }
}
