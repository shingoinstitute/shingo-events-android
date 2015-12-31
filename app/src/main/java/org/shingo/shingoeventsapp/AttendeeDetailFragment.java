package org.shingo.shingoeventsapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentTransaction;
import android.os.AsyncTask;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.shingo.shingoeventsapp.attendees.Attendees;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * A fragment representing a single Attendee detail screen.
 * This fragment is either contained in a {@link AttendeeListActivity}
 * in two-pane mode (on tablets) or a {@link AttendeeDetailActivity}
 * on handsets.
 */
public class AttendeeDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private Attendees.Attendee mAttendee;

    private SendConnectRequestTask sendConnectRequestTask = null;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AttendeeDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mAttendee = Attendees.ATTENDEE_MAP.get(getArguments().getString(ARG_ITEM_ID));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Activity activity = this.getActivity();
        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
        if (appBarLayout != null) {
            appBarLayout.setTitle(mAttendee.toString());
        }

        View rootView = inflater.inflate(R.layout.fragment_attendee_detail, container, false);
        View parent = container.getRootView();
        System.out.println("fragment_attendee_detail parent view: " + parent.getId());
        if (mAttendee != null) {
            TextView emailView = (TextView) rootView.findViewById(R.id.attendee_email);
            TextView nameView = (TextView) rootView.findViewById(R.id.attendee_display_name);
            TextView titleView = (TextView) rootView.findViewById(R.id.attendee_title);
            TextView companyView = (TextView) rootView.findViewById(R.id.attendee_company);
            emailView.setText(mAttendee.email);
            nameView.setText(mAttendee.toString());
            titleView.setText(mAttendee.title);
            companyView.setText(mAttendee.company);
            FloatingActionButton fab = (FloatingActionButton) parent.findViewById(R.id.fab);
            System.out.println("mAttendee.status " + mAttendee.status);
            switch(mAttendee.status){
                case 0:
                    //TODO: send message
                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Snackbar.make(view, "Opening chat...", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    });
                    break;
                case 1:
                    fab.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_info_details));
                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Snackbar.make(view, "Request pending...", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    });
                    break;
                default:
                    fab.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_send));
                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            sendRequest();
                            Snackbar.make(view, "Request sent!", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    });
            }
        }

        return rootView;
    }

    private void sendRequest(){
        SharedPreferences sharedPreferences = getActivity().getApplicationContext().getSharedPreferences("login", Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");
        String connection = mAttendee.email;
        sendConnectRequestTask = new SendConnectRequestTask(email, connection);
        sendConnectRequestTask.execute((Void) null);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    private class SendConnectRequestTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mConnection;
        private String output;

        SendConnectRequestTask(String email,String connection) {
            mEmail = email;
            mConnection = connection;
            System.out.println("SendConnectRequestTask created");
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            System.out.println("SendConnectRequestTask.doInBackground called");
            boolean success  = false;
            try {
                String data = URLEncoder.encode("connection", "UTF-8") + "=" + URLEncoder.encode(mConnection,"UTF-8");
                URL url = new URL("https://shingo-events.herokuapp.com/api/attendees/addconnection/"+mEmail+"/?client_id=" + LoginActivity.CLIENT_ID + "&client_secret=" + LoginActivity.CLIENT_SECRET);
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
                System.out.println("SendReqeust response: " + output);
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
            sendConnectRequestTask = null;

            if (success) {
                System.out.println("Restarting activity");
                mAttendee.status = 1;
                Fragment currentFrag = getFragmentManager().findFragmentById(getId());
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.detach(currentFrag);
                fragmentTransaction.attach(currentFrag);
                fragmentTransaction.commit();
            } else {
                System.out.println("Error occurred sending request");
            }
        }

        @Override
        protected void onCancelled() {
            sendConnectRequestTask = null;
        }
    }
}
