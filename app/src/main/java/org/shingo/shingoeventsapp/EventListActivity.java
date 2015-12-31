package org.shingo.shingoeventsapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


/**
 * An activity representing a list of Events. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link EventDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p/>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link EventListFragment} and the item details
 * (if present) is a {@link EventDetailFragment}.
 * <p/>
 * This activity also implements the required
 * {@link EventListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class EventListActivity extends AppCompatActivity
        implements EventListFragment.Callbacks {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    private AddRegIdTask addRegIdTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_app_bar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add registration id");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String regid = input.getText().toString();
                if (!regid.isEmpty()) {
                    addRegId(regid);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.show();
                Snackbar.make(view, "Adding registration ID", Snackbar.LENGTH_LONG)
                        .setAction("Undo", null).show();
            }
        });

        if (findViewById(R.id.event_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((EventListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.event_list))
                    .setActivateOnItemClick(true);
        }

        // TODO: If exposing deep links into your app, handle intents here.
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Callback method from {@link EventListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {
        System.out.println("Item selected with id: " + id);
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(EventDetailFragment.ARG_ITEM_ID, id);
            EventDetailFragment fragment = new EventDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.event_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, EventDetailActivity.class);
            detailIntent.putExtra(EventDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (id == android.R.id.home) {
//            // This ID represents the Home or Up button. In the case of this
//            // activity, the Up button is shown. For
//            // more details, see the Navigation pattern on Android Design:
//            //
//            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
//            //
//            Intent i = new Intent(this, EventListActivity.class);
//            navigateUpTo(i);
//            return true;
//        }
        switch(item.getItemId()){
            case android.R.id.home:
                Intent i = new Intent(this, EventListActivity.class);
                navigateUpTo(i);
                return true;
            case R.id.action_connect:
                System.out.println("Connect clicked!");
                Intent intent = new Intent(this, ConnectionListActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addRegId(String regId) {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("login", Context.MODE_PRIVATE);
        String mEmail = sharedPreferences.getString("email", "");
        String mPassword = sharedPreferences.getString("password","");
        addRegIdTask = new AddRegIdTask(mEmail, mPassword, regId);
        addRegIdTask.execute((Void) null);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    private class AddRegIdTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private final String mRegId;
        private String output;

        AddRegIdTask(String email, String password, String regId) {
            mEmail = email;
            mPassword = password;
            mRegId = regId;
            System.out.println("AddRegIdTask created");
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            System.out.println("AddRegIdTask.doInBackground called");
            boolean success  = false;
            try {
                String data = URLEncoder.encode("reg_id", "UTF-8") + "=" + URLEncoder.encode(mRegId,"UTF-8");
                data += "&" + URLEncoder.encode("email","UTF-8") + "=" + URLEncoder.encode(mEmail, "UTF-8");
                data += "&" + URLEncoder.encode("password", "UTF-8") + "=" +URLEncoder.encode(mPassword, "UTF-8");
                URL url = new URL("https://shingo-events.herokuapp.com/api/attendees/addregid/?client_id=" + LoginActivity.CLIENT_ID + "&client_secret=" + LoginActivity.CLIENT_SECRET);
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = "";
                while((line = reader.readLine()) != null){
                    sb.append(line);
                }
                output = sb.toString();
                JSONObject response = new JSONObject(output);
                System.out.println("AddRegId response: " + output);
                success = response.getBoolean("success");
            } catch(UnsupportedEncodingException e){
                return false;
            } catch(IOException e ){
                return false;
            } catch(JSONException e){
                return false;
            }

            return success ;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            addRegIdTask = null;

            if (success) {
                System.out.println("Restarting activity");
                Intent i = getIntent();
                finish();
                startActivity(i);
            } else {
                System.out.println("Error occurred adding regid");
            }
        }

        @Override
        protected void onCancelled() {
            addRegIdTask = null;
        }
    }
}
