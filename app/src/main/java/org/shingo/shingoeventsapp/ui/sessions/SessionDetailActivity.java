package org.shingo.shingoeventsapp.ui.sessions;

import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import org.shingo.shingoeventsapp.R;
import org.shingo.shingoeventsapp.data.agendas.Agendas;
import org.shingo.shingoeventsapp.data.sessions.Sessions;
import org.shingo.shingoeventsapp.ui.agendas.AgendaListActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * An activity representing a single Session detail screen. This
 * activity is only used on handset devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link SessionListActivity}.
 * <p/>
 * This activity is mostly just a 'shell' activity containing nothing
 * more than a {@link SessionDetailFragment}.
 */
public class SessionDetailActivity extends AppCompatActivity {

    private String mEventId;
    private String mDayId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        mEventId = getIntent().getExtras().getString("event_id");

        if(getIntent().getExtras().containsKey("day_id"))
            mDayId = getIntent().getExtras().getString("day_id");

        // Show the Up button in the action bar.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(SessionDetailFragment.ARG_ITEM_ID,
                    getIntent().getStringExtra(SessionDetailFragment.ARG_ITEM_ID));
            SessionDetailFragment fragment = new SessionDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.session_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            Intent i = new Intent(this, AgendaListActivity.class);
            i.putExtra("event_id", mEventId);
            if(mDayId != null) i.putExtra("day_id", mDayId);
            navigateUpTo(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        Sessions.Session session = Sessions.SESSION_MAP.get(getIntent().getStringExtra(SessionDetailFragment.ARG_ITEM_ID));
        if(session != null){
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
            String day_id = null;
            try {
                Date start = formatter.parse(session.startTime);
                for(Agendas.Day ag : Agendas.AGENDAS){
                    if(ag.name.equals(getDayToStringMap(start.getDay()))){
                        day_id = ag.id;
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if(day_id != null){
                Intent i = new Intent(this, AgendaListActivity.class);
                i.putExtra("day_id",day_id);
                i.putExtra("event_id",mEventId);
                navigateUpTo(i);
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

    private String getDayToStringMap(int day){
        switch (day){
            case 0:
                return "Sunday";
            case 1:
                return "Monday";
            case 2:
                return "Tuesday";
            case 3:
                return "Wednesday";
            case 4:
                return "Thursday";
            case 5:
                return "Friday";
            case 6:
                return "Saturday";
            default:
                return null;
        }
    }
}
